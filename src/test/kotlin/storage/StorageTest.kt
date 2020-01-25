package storage

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import config.CORSFilter
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.ValidationExceptionMapper
import model.Transaction
import model.User
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.runners.MockitoJUnitRunner
import service.UserService
import java.math.BigDecimal
import javax.inject.Singleton
import javax.ws.rs.core.Application
import javax.ws.rs.ext.ContextResolver

@RunWith(MockitoJUnitRunner::class)
class StorageTest : JerseyTest() {

    override fun configure(): Application {
        return ResourceConfig(Storage::class.java).register(object : AbstractBinder() {
            override fun configure() {
                bindAsContract(storage.javaClass).`in`(Singleton::class.java)
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
    lateinit var storage: Storage

    @Test
    fun findUserById() {
        val user = storage.findUserById(1L)
        assertNotNull(user)
        assertEquals(user!!.email, "user1@gmail.com")
    }

    @Test
    fun findUserNotExist() {
        val user = storage.findUserById(6L)
        assertNull(user)
    }

    @Test
    fun createTransaction() {
        val insertTransaction = Transaction(1L, 2L, BigDecimal("10"))
        val transaction = storage.createTransaction(insertTransaction)
        assertEquals(transaction.id, 1L)
    }

    @Test
    fun createUser() {
        val insertUser = User("test@test.com")
        val user = storage.createUser(insertUser)
        assertNotNull(user)
        assertNotNull(user.wallet)
        assertEquals(user.id, 6L)
    }

    @Test
    fun findAllTransactions() {
        var transactions = storage.findAllTransactions()
        assertEquals(transactions.size, 0)

        val insertTransaction = Transaction(1L, 2L, BigDecimal("10"))
        val transaction = storage.createTransaction(insertTransaction)
        transactions = storage.findAllTransactions()
        assertEquals(transactions.size, 1)
        assertTrue(transactions.contains(transaction))
    }

    @Test
    fun findAllTransactionsForUser() {
        val insertTransaction1 = Transaction(1L, 2L, BigDecimal("10"))
        val insertTransaction2 = Transaction(2L, 1L, BigDecimal("10"))
        val insertTransaction3 = Transaction(3L, 1L, BigDecimal("10"))
        storage.createTransaction(insertTransaction1)
        storage.createTransaction(insertTransaction2)
        storage.createTransaction(insertTransaction3)
        val user = storage.findUserById(2L)
        assertEquals(storage.findAllTransactionsForUser(user!!).size, 2)
    }

    @Test
    fun existsByEmail() {
        assertTrue(storage.existsByEmail("user1@gmail.com"))
        assertFalse(storage.existsByEmail("someuser@gmail.com"))
    }

    @Test
    fun findAllUsers() {
        assertEquals(storage.findAllUsers().size, 5)
    }
}