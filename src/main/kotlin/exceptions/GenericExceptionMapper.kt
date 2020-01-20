package exceptions

import javax.ws.rs.WebApplicationException
import com.sun.deploy.association.utility.AppConstants
import constants.ErrorStatusCodes
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.IllegalArgumentException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper


class GenericExceptionMapper : ExceptionMapper<Throwable> {


    override fun toResponse(ex: Throwable): Response {

        val errorMessage = ErrorResponseMessage()
        setHttpStatus(ex, errorMessage)
        errorMessage.code = ErrorStatusCodes.GENERIC_APP_ERROR
        errorMessage.message= ex.message
        ex.printStackTrace()
        return Response.status(errorMessage.status)
            .entity(errorMessage)
            .type(MediaType.APPLICATION_JSON)
            .build()
    }

    private fun setHttpStatus(ex: Throwable, errorMessage: ErrorResponseMessage) {
        if (ex is WebApplicationException) {
            errorMessage.status = ex.response.status
        } else {
            errorMessage.status = Response.Status.INTERNAL_SERVER_ERROR.statusCode
        }
    }
}