package controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import config.CORSFilter
import dto.UserDto
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.ValidationExceptionMapper
import model.User
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test

import org.junit.Assert.*
import service.TransactionService
import service.UserService
import storage.Storage
import javax.inject.Singleton
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.ext.ContextResolver

class UserControllerTest : JerseyTest() {

    override fun configure(): Application {
        return ResourceConfig(UserController::class.java).register(object : AbstractBinder() {
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
    fun createUser() {
        var user = UserDto("aco.jok@gmail.com")
        var response = target("/api/users").request().post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE))
        assertEquals(response.status, 201)

        user = UserDto("")
        response = target("/api/users").request().post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE))
        assertEquals(response.status, 400)
    }

    @Test
    fun getUsers() {
        val response = target("/api/users").request().get(List::class.java)
        assertEquals(response.size, 5)
    }
}