package org.example.wallet.exception.handler;

import org.example.wallet.exception.WalletNoCash;
import org.example.wallet.exception.WalletNotFound;
import org.example.wallet.exception.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorDto> catchWalletNotFoundException(WalletNotFound e) {
        return new ResponseEntity<>(new ErrorDto(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> catchWalletNoCashException(WalletNoCash e) {
        return new ResponseEntity<>(new ErrorDto(HttpStatus.FORBIDDEN.value(), e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> catchWalletBadRequestException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Invalid operation type"), HttpStatus.BAD_REQUEST);
    }
}