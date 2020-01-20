package service

import dto.TransactionDto
import exceptions.NotEnoughBalanceException
import exceptions.TransactionNotAllowedException
import model.Transaction
import storage.Storage
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionService {

    @Inject
    private lateinit var storage: Storage

    @Inject
    private lateinit var userService: UserService

    fun createTransaction(transactionDto: TransactionDto): Transaction {
        if (transactionDto.fromUser == transactionDto.toUser)
            throw TransactionNotAllowedException("You can't transfer to the same account.")
        val fromUser = userService.findById(transactionDto.fromUser)
        val toUser = userService.findById(transactionDto.toUser)
        synchronized(fromUser) {
            synchronized(toUser) {
                if (fromUser.wallet!!.balance < transactionDto.amount) {
                    throw NotEnoughBalanceException("You don't have enough money for this transaction.")
                }
                fromUser.wallet!!.balance -= transactionDto.amount
                toUser.wallet!!.balance += transactionDto.amount
            }
        }
        val transaction = Transaction(fromUser.wallet!!.id, toUser.wallet!!.id, transactionDto.amount)
        return storage.createTransaction(transaction)
    }

    fun findAll(userId: Long?): List<Transaction> {
        userId?: return storage.findAllTransactions()
        return findAllForUser(userId)
    }

    private fun findAllForUser(userId: Long): List<Transaction> {
        val user = userService.findById(userId)
        return storage.findAllTransactionsForUser(user)
    }
}