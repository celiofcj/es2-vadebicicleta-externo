package com.es2.vadebicicleta.externo.email.service

import com.es2.vadebicicleta.externo.email.client.EmailClient
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.repository.EmailRepository
import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress
import org.springframework.stereotype.Service

@Service
class EmailService (
    val repository: EmailRepository,
    val emailClient: EmailClient
) {
    fun enviarEmail(requisicaoEmail: RequisicaoEmail): RequisicaoEmail {

        if(!validarFormatoEmail(requisicaoEmail.email)) {
            throw WrongEmailAdressFormatException("Formato de email inválido." +
                    " Recomendado consultar RFC 3696 e a errata associada")
        }

        emailClient.enviarEmail(requisicaoEmail.email, requisicaoEmail.assunto, requisicaoEmail.mensagem)

        return repository.save(requisicaoEmail)
    }

    fun validarFormatoEmail(email: String) =
        try {
            InternetAddress(email).validate()
            true
        } catch (e: AddressException) {
            false
        }
}