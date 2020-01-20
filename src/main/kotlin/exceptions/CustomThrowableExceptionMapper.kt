package exceptions

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class CustomThrowableExceptionMapper : ExceptionMapper<CustomThrowableException> {

    override fun toResponse(exception: CustomThrowableException): Response {
        val errorMessage = ErrorResponseMessage()
        errorMessage.status = exception.status
        errorMessage.code = exception.code
        errorMessage.message= exception.message
        exception.printStackTrace()
        return Response.status(errorMessage.status)
            .entity(errorMessage)
            .type(MediaType.APPLICATION_JSON)
            .build()
    }
}