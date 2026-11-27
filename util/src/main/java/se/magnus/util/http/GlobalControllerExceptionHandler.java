package se.magnus.util.http;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.magnus.api.exceptions.InvalidInputException;
import se.magnus.api.exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpErrorInfo> handleNotFound(
            ServerHttpRequest request, NotFoundException exception) {
        return createResponse(HttpStatus.NOT_FOUND, request, exception);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<HttpErrorInfo> handleInvalidInput(
            ServerHttpRequest request, InvalidInputException exception) {
        return createResponse(HttpStatus.valueOf(422), request, exception);
    }

    private ResponseEntity<HttpErrorInfo> createResponse(
            HttpStatus status, ServerHttpRequest request, RuntimeException exception) {
        HttpErrorInfo body = new HttpErrorInfo(
                ZonedDateTime.now(),
                request.getPath().value(),
                status,
                exception.getMessage());

        return ResponseEntity.status(status).body(body);
    }
}