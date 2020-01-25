package controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Test

import org.junit.Assert.*
import config.CORSFilter
import dto.TransactionDto
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.ValidationExceptionMapper
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.test.JerseyTest
import service.TransactionService
import service.UserService
import storage.Storage
import java.math.BigDecimal
import javax.inject.Singleton
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.ContextResolver


class TransactionControllerTest : JerseyTest() {

    override fun configure(): Application {
        return ResourceConfig(TransactionController::class.java, UserController::class.java).register(object : AbstractBinder() {
            override fun configure() {
                bindAsContract(Storage::class.java).`in`(Singleton::class.java)
                bindAsContract(UserService::class.java).`in`(Singleton::class.java)
                bindAsContract(TransactionService::class.java).`in`(Singleton::class.java)
            }
        })
            .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
            .register(CORSFilter::class.java)
            .register(CustomThrowableExceptionMapper::class.java)
            .register(ValidationExceptionMapper::class.java)
            .register(GenericExceptionMapper::class.java)
            .register(ContextResolver<ObjectMapper> { ObjectMapper().registerModule(KotlinModule()) })
    }

    @Test
    fun createTransaction() {
        val transaction = TransactionDto(1, 2, BigDecimal("10"))
        val response = target("/api/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE))
        assertEquals(response.status, 201)
    }

    @Test
    fun getTransactions() {
        var response = target("/api/transactions").request().get(List::class.java)
        assertEquals(response.size, 0)

        val transaction = TransactionDto(1, 2, BigDecimal("10"))
        target("/api/transactions").request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE))
        response = target("/api/transactions").request().get(List::class.java)
        assertEquals(response.size, 1)
    }
}