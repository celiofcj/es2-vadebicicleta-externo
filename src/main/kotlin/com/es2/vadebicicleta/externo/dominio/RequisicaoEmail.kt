package com.es2.vadebicicleta.externo.dominio

import jakarta.persistence.*

@Entity
class RequisicaoEmail (
    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var assunto: String,

    @Column(nullable = false)
    var mensagem: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequisicaoEmail

        if (email != other.email) return false
        if (assunto != other.assunto) return false
        if (mensagem != other.mensagem) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + assunto.hashCode()
        result = 31 * result + mensagem.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
