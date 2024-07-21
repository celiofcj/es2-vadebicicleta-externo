package com.es2.vadebicicleta.externo.cartaocredito.client

import com.es2.vadebicicleta.externo.cartaocredito.client.dto.CartaoDeCreditoCobrancaDto
import com.es2.vadebicicleta.externo.cartaocredito.client.dto.CartaoDeCreditoValidacaoDto
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations

interface OperadoraClient {
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao
    fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta
}

@Service
class OperadoraClientDefaultImpl(
    val restOperations: RestOperations,
    @Value("\${vadebicicleta.cartao-de-credito.operadora.url}")
    val urlOperadora: String,
    @Value("\${vadebicicleta.usar-servicos-reais}")
    val servicosReais : Boolean
    ) : OperadoraClient {

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoValidacao {
        if(!servicosReais) {
            return CartaoDeCreditoValidacao(true)
        }

        val response: ResponseEntity<CartaoDeCreditoValidacaoDto> = enviarRequsicaoComCartaoDeCredito(getUrlConsulta(), cartaoDeCredito)

        if(response.statusCode != HttpStatus.OK) {
            throw ExternalServiceException(
                "Conexão com a operadora de cartão de crédito mal sucedida. Código ${response.statusCode}")
        }

        return converterResponseValidacao(response.body ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio (validação): resposta com body null"))
    }

    override fun enviarCobranca(cartaoDeCredito: CartaoDeCredito) : CartaoDeCreditoCobrancaResposta {
        if(!servicosReais) {
            return CartaoDeCreditoCobrancaResposta("SUCESSO")
        }

        val response : ResponseEntity<CartaoDeCreditoCobrancaDto> =
            enviarRequsicaoComCartaoDeCredito(getUrlCobranca(), cartaoDeCredito)

        if(response.statusCode != HttpStatus.OK) {
            throw ExternalServiceException(
                "Conexão com a operadora de cartão de crédito mal sucedida. Código ${response.statusCode}")
        }

        return converteResponseCobranca(response.body ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio (cobrança): resposta com body null"))
    }

    private inline fun <reified T> enviarRequsicaoComCartaoDeCredito(url: String, cartaoDeCredito: CartaoDeCredito) :
            ResponseEntity<T> {

        try {
            return restOperations.postForEntity(url, cartaoDeCredito,  T::class.java)
        } catch (e: RestClientException) {
            throw ExternalServiceException("Erro na conexão com a operadora de cartão de crédito", e)
        }
    }

    private fun getUrlConsulta() = "$urlOperadora/consultar"

    private fun getUrlCobranca() = "$urlOperadora/cobranca"

    private fun converterResponseValidacao(body: CartaoDeCreditoValidacaoDto): CartaoDeCreditoValidacao {
        return CartaoDeCreditoValidacao(
            body.valido ?: throw ExternalServiceException(
            "Erro inesperado na integracação com a operadora de cartão de crédtio: campo \"valido\" null"),
            body.erros ?: emptyList()
            )
    }

    private fun converteResponseCobranca(body: CartaoDeCreditoCobrancaDto) : CartaoDeCreditoCobrancaResposta {
        return CartaoDeCreditoCobrancaResposta(
            body.status ?: throw ExternalServiceException(
                "Erro inesperado na integracação com a operadora de cartão de crédtio: campo \"status\" null"),
                body.erros ?: emptyList()
            )
    }
}