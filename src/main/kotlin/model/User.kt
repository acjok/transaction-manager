package model

import com.fasterxml.jackson.annotation.JsonIgnore

data class User (
    var email: String

) {
    var wallet: Wallet? = null
    @JsonIgnore
    var id: Long = 0
}