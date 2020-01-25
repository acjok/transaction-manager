package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import config.CORSFilter
import dto.UserDto
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.UserAlreadyExistsException
import exceptions.ValidationExceptionMapper
import model.User
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner
import storage.Storage
import javax.inject.Singleton
import javax.ws.rs.core.Application
import javax.ws.rs.ext.ContextResolver

@RunWith(MockitoJUnitRunner::class)
class UserServiceTest : JerseyTest() {

    override fun configure(): Application {
        return ResourceConfig(UserService::class.java).register(object : AbstractBinder() {
            override fun configure() {
                bindAsContract(storage.javaClass).`in`(Singleton::class.java)
                bindAsContract(userService.javaClass).`in`(Singleton::class.java)
            }
        })
            .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
            .register(CORSFilter::class.java)
            .register(CustomThrowableExceptionMapper::class.java)
            .register(ValidationExceptionMapper::class.java)
            .register(GenericExceptionMapper::class.java)
            .register(ContextResolver<ObjectMapper> { ObjectMapper().registerModule(KotlinModule()) })
    }

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var storage: Storage

    @Test(expected = UserAlreadyExistsException::class)
    fun userAlreadyExists() {
        val userDto = UserDto("test1@test.com")
        `when`(storage.existsByEmail("test1@test.com")).thenReturn(true)
        userService.createUser(userDto)
    }

    @Test
    fun createUser() {
        val userDto = UserDto("test1@test.com")
        `when`(storage.existsByEmail("test1@test.com")).thenReturn(false)
        val insertUser = User("test1@test.com")
        `when`(storage.createUser(any(User::class.java))).thenReturn(insertUser)
        val user = userService.createUser(userDto)
        assertEquals(user.email, insertUser.email)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}