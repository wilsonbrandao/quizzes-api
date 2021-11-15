package com.example.quizzesapi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class FieldError(val name:String, val message: String)

@ControllerAdvice
class GlobalExceptionHandler {
//    MethodArgumentNotValidException

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handlerMethodArgumentNotValidException( ex: MethodArgumentNotValidException): ResponseEntity<List<FieldError>> {
        return ResponseEntity(ex.fieldErrors.map { FieldError(it.field, it.defaultMessage ?: "") }, HttpStatus.BAD_REQUEST)
    }
}