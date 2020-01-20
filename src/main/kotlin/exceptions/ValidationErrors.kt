package exceptions

data class ValidationErrors (
    var code: String,
    var message: String,
    var fields: List<ValidationError>
)

data class ValidationError (
    var fieldName: String,
    var rejectedValue: String,
    var message: String
)