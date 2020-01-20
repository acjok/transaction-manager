package model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal

data class Transaction(
    var fromWalletId: Long,
    var toWalletId: Long,
    var amount: BigDecimal
) {
    @JsonIgnore
    var id: Long = 0
}