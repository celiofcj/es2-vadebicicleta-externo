package com.es2.vadebicicleta.externo.commons

import com.es2.vadebicicleta.externo.email.service.WrongEmailAdressFormatException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleAllUncaughtExceptions(ex: Exception) : ResponseEntity<MensagemErro>{
        val mensagemErro =  MensagemErro("100", "Um erro inesperado aconteceu")
        return ResponseEntity.internalServerError().body(mensagemErro)
    }

    @ExceptionHandler
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<Collection<MensagemErro>> {
        val bindingResult = ex.bindingResult
        val fieldErrors = bindingResult.fieldErrors

        val mensagensDeErro = fieldErrors.map {
            val campo = it.field
            val mensagem = it.defaultMessage
            val codigo = "422"
            MensagemErro(codigo, "$campo: $mensagem")
        }

        return ResponseEntity.unprocessableEntity().body(mensagensDeErro)
    }

    @ExceptionHandler
    fun handleWrongEmailAdressFormatException(ex: WrongEmailAdressFormatException) : ResponseEntity<Collection<MensagemErro>> {
        val codigo = "422"
        val mensagem = ex.message ?: ""
        val mensagensDeErro = listOf<MensagemErro>(MensagemErro(codigo, mensagem))

        return ResponseEntity.unprocessableEntity().body(mensagensDeErro)
    }
}

data class MensagemErro(
    val codigo: String,
    val mensagem: String
)