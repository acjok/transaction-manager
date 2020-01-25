package service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import config.CORSFilter
import controller.TransactionController
import controller.UserController
import dto.TransactionDto
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.NotEnoughBalanceException
import exceptions.ValidationExceptionMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.Transaction
import model.User
import model.Wallet
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.test.JerseyTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.runners.MockitoJUnitRunner
import storage.Storage
import java.math.BigDecimal
import javax.inject.Singleton
import javax.ws.rs.core.Application
import javax.ws.rs.ext.ContextResolver

@RunWith(MockitoJUnitRunner::class)
class TransactionServiceTest : JerseyTest() {

    override fun configure(): Application {
        return ResourceConfig(TransactionService::class.java).register(object : AbstractBinder() {
            override fun configure() {
                bindAsContract(storage.javaClass).`in`(Singleton::class.java)
                bindAsContract(userService.javaClass).`in`(Singleton::class.java)
                bindAsContract(transactionService.javaClass).`in`(Singleton::class.java)
            }
        })
            .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
            .register(CORSFilter::class.java)
            .register(CustomThrowableExceptionMapper::class.java)
            .register(ValidationExceptionMapper::class.java)
            .register(GenericExceptionMapper::class.java)
            .register(ContextResolver<ObjectMapper> { ObjectMapper().registerModule(KotlinModule()) })
    }

    @Mock
    lateinit var userService: UserService

    @Mock
    lateinit var storage: Storage

    @InjectMocks
    lateinit var transactionService: TransactionService

    @Test
    fun createTransaction() {
        val wallet1 = Wallet(BigDecimal("100"))
        val wallet2 = Wallet(BigDecimal("200"))
        val user1 = User("test1@test.com")
        val user2 = User("test2@test.com")
        user1.wallet = wallet1
        user2.wallet = wallet2
        `when`(userService.findById(1L)).thenReturn(user1)
        `when`(userService.findById(2L)).thenReturn(user2)
        val transaction = Transaction(1L, 2L, BigDecimal("10"))
        `when`(storage.createTransaction(any(Transaction::class.java))).thenReturn(transaction)
        transactionService.createTransaction(TransactionDto(1L, 2L, BigDecimal("10")))
        assertEquals(wallet1.balance, BigDecimal("90"))
        assertEquals(wallet2.balance, BigDecimal("210"))
    }

    @Test
    fun testConcurrentTransactions() {
        val wallet1 = Wallet(BigDecimal("100"))
        val wallet2 = Wallet(BigDecimal("200"))
        val user1 = User("test1@test.com")
        val user2 = User("test2@test.com")
        user1.wallet = wallet1
        user2.wallet = wallet2
        `when`(userService.findById(1L)).thenReturn(user1)
        `when`(userService.findById(2L)).thenReturn(user2)
        val transaction = Transaction(1L, 2L, BigDecimal("10"))
        `when`(storage.createTransaction(any(Transaction::class.java))).thenReturn(transaction)

        val deferredTo = (1..5).map { n ->
            GlobalScope.async {
                transactionService.createTransaction(TransactionDto(1L, 2L, BigDecimal("10")))
                n
            }
        }
        val deferredFrom = (1..5).map { n ->
            GlobalScope.async {
                transactionService.createTransaction(TransactionDto(2, 1L, BigDecimal("11")))
                n
            }
        }
        runBlocking {
            deferredTo.map { it.await() }
            deferredFrom.map { it.await() }
        }

        assertEquals(wallet1.balance, BigDecimal("105"))
        assertEquals(wallet2.balance, BigDecimal("195"))
    }

    @Test(expected = NotEnoughBalanceException::class)
    fun createTransactionNoFunds() {
        val wallet1 = Wallet(BigDecimal("100"))
        val wallet2 = Wallet(BigDecimal("200"))
        val user1 = User("test1@test.com")
        val user2 = User("test2@test.com")
        user1.wallet = wallet1
        user2.wallet = wallet2
        `when`(userService.findById(1L)).thenReturn(user1)
        `when`(userService.findById(2L)).thenReturn(user2)
        transactionService.createTransaction(TransactionDto(1L, 2L, BigDecimal("105")))

    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
}