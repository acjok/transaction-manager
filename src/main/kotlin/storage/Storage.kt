package storage

import exceptions.ServerException
import model.Transaction
import model.User
import model.Wallet
import util.StorageInit
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class Storage {
    private var transactions = mutableListOf<Transaction>()
    private var users = mutableListOf<User>()
    private var wallets = mutableListOf<Wallet>()

    init {
        StorageInit.initWallets(wallets)
        StorageInit.initUsers(users, wallets)
    }

    fun findUserById(id: Long) = users.find { it.id == id }

    fun createTransaction(transaction: Transaction): Transaction {
        transaction.id = transactions.size.toLong() + 1
        if (transactions.add(transaction))
            return transaction
        else
            throw ServerException("Error saving transaction")
    }

    fun createUser(user: User): User {
        user.id = users.size.toLong() + 1
        val wallet = Wallet(BigDecimal("0"))
        wallet.id = wallets.size.toLong() + 1
        if (wallets.add(wallet)){
            user.wallet = wallet
            if (users.add(user))
                return user
            else
                throw ServerException("Error saving user")
        } else
            throw ServerException("Error creating wallet for user")
    }

    fun findAllTransactions(): List<Transaction> = transactions

    fun findAllTransactionsForUser(user: User): List<Transaction> {
        val walletId = user.wallet?.id
        val userTransactions = mutableListOf<Transaction>()
        transactions.forEach {
            if (it.fromWalletId == walletId || it.toWalletId == walletId)
                userTransactions.add(it)
        }
        return userTransactions
    }

    fun existsByEmail(email: String): Boolean = users.any { it.email == email }

    fun findAllUsers(): List<User> = users
}