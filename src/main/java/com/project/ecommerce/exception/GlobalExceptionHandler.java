package com.project.ecommerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de negocio (como "No hay stock")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Captura el error de concurrencia cuando dos personas compran al mismo tiempo
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleConcurrency(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(409).body("Error de concurrencia: El producto fue actualizado por otro usuario. Intenta de nuevo.");
    }
}
