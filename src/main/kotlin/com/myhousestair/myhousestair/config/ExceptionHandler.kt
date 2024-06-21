package com.myhousestair.myhousestair.config


import com.myhousestair.myhousestair.dto.response.common.ErrorResponse
import com.myhousestair.myhousestair.exception.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(BindException::class)
    fun handleInvalidRequestBodyException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.bindingResult.allErrors[0].defaultMessage ?: ""
            )
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleInvalidRequestBodyException(e: WebExchangeBindException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.bindingResult.allErrors[0].defaultMessage ?: ""
            )
        )
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleInvalidRequestBodyException(e: MissingRequestHeaderException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase
            )
        )
    }

    @ExceptionHandler(HttpMessageConversionException::class)
    fun handleInvalidRequestBodyException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.reasonPhrase
            )
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleException(e: BadRequestException): ResponseEntity<ErrorResponse> {
        e.printStackTrace()
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.message ?: ""
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        e.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ""
            )
        )
    }
}