package ee.kemit.aks.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AksExceptionHandler {

    @ExceptionHandler(AksException.class)
    public ResponseEntity<Map<String, Object>> handleException(AksException exception,
                                                               HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
