package model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal

data class Wallet (
    var balance: BigDecimal
) {
    @JsonIgnore
    var id: Long = 0
}