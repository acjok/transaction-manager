package dto

import javax.validation.constraints.Pattern

data class UserDto(
    @field:Pattern(message = "Invalid Email Address->" +
            "Valid emails:user@gmail.com or my.user@domain.com etc.",
        regexp = "^[A-Za-z0-9+_.-]+@(.+)\$")
    var email: String
)