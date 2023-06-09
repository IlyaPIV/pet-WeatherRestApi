package pet.skyapi.weatherforecast;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pet.skyapi.weatherforecast.common.ErrorDTO;
import pet.skyapi.weatherforecast.geolocation.GeolocationException;
import pet.skyapi.weatherforecast.location.LocationNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = getErrorDTO(request, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        errorDTO.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return errorDTO;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = getErrorDTO(request, ex, HttpStatus.BAD_REQUEST);
        errorDTO.addError(ex.getMessage());
        return errorDTO;
    }

    @ExceptionHandler(GeolocationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleGeolocationException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = getErrorDTO(request, ex, HttpStatus.BAD_REQUEST);
        errorDTO.addError(ex.getMessage());
        return errorDTO;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = getErrorDTO(request, ex, HttpStatus.BAD_REQUEST);

        ConstraintViolationException violationException = (ConstraintViolationException) ex;
        var constraintViolations = violationException.getConstraintViolations();
        constraintViolations.forEach(constraint -> {
            errorDTO.addError(constraint.getPropertyPath() + ": " + constraint.getMessage());
        });

        return errorDTO;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = getErrorDTO(request, ex, HttpStatus.NOT_FOUND);
        errorDTO.addError(ex.getMessage());
        return errorDTO;
    }

    private ErrorDTO getErrorDTO(HttpServletRequest request, Exception ex, HttpStatus status) {
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(status.value());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);

        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatusCode status, @NotNull WebRequest request) {
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest) request).getRequest().getServletPath());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(err -> error.addError(err.getDefaultMessage()));

        LOGGER.error(ex.getMessage(), ex);

        return new ResponseEntity<>(error, headers, status);
    }


}
