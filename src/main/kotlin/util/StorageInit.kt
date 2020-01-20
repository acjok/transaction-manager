package util

import model.User
import model.Wallet
import java.math.BigDecimal

object StorageInit {
    fun initWallets (wallets: MutableList<Wallet>) {
        for(i in 0..4) {
            val wallet = Wallet(BigDecimal(100))
            wallet.id = i + 1L
            wallets.add(wallet)
        }
    }

    fun initUsers (
        users: MutableList<User>,
        wallets: MutableList<Wallet>
    ) {
        for(i in 0..4) {
            val user = User("user" + (i + 1) + "@gmail.com")
            user.wallet = wallets[i]
            user.id = i + 1L
            users.add(user)
        }
    }
}