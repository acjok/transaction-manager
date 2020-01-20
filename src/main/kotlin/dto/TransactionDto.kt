package dto

import java.math.BigDecimal
import javax.validation.constraints.Min

data class TransactionDto (
    @field:Min(1)
    val fromUser: Long,
    @field:Min(1)
    val toUser: Long,
    @field:Min(1)
    val amount: BigDecimal
)