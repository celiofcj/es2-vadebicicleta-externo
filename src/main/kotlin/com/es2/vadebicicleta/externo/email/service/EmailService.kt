package com.es2.vadebicicleta.externo.email.service

import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.email.client.EmailClient
import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.repository.EmailRepository
import org.springframework.stereotype.Service

@Service
class EmailService (
    val repository: EmailRepository,
    val emailClient: EmailClient
) {
    fun enviarEmail(requisicaoEmail: RequisicaoEmail): RequisicaoEmail {
        try {
            emailClient.enviarEmail(requisicaoEmail)
        } catch (e: Exception) {
            throw ExternalServiceException("O email não pôde ser enviado. Tente novamente mais tarde.")
        }

        return repository.save(requisicaoEmail)
    }
}
