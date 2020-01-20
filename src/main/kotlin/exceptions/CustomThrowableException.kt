package exceptions

import java.lang.RuntimeException

abstract class CustomThrowableException (
    var status: Int,
    var code: String,
    override var message: String
) : RuntimeException()