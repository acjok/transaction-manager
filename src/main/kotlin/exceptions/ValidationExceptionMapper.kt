package exceptions

import constants.ErrorStatusCodes
import org.hibernate.validator.internal.engine.path.PathImpl
import javax.validation.ConstraintViolationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class ValidationExceptionMapper : ExceptionMapper<ConstraintViolationException> {

    override fun toResponse(exception: ConstraintViolationException): Response {
        val validationViolations = exception.constraintViolations.toList()
        val errors = mutableListOf<ValidationError>()
        (validationViolations[0].propertyPath as PathImpl).leafNode
        validationViolations.forEach {
            errors.add(ValidationError((it.propertyPath as PathImpl).leafNode.toString(), it.invalidValue.toString(), it.message))
        }
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(ValidationErrors(ErrorStatusCodes.VALIDATION_FAILED, "Validation failed.", errors))
            .type(MediaType.APPLICATION_JSON)
            .build()
    }
}