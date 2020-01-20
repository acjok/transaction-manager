package controller

import dto.TransactionDto
import org.glassfish.jersey.process.internal.RequestScoped
import service.TransactionService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RequestScoped
@Path("/api/transactions")
class TransactionController {

    @Inject
    lateinit var transactionService: TransactionService

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createTransaction(@Valid transaction: TransactionDto): Response = Response.status(201)
        .entity(transactionService.createTransaction(transaction))
        .build()

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getTransactions(@QueryParam("userId") userId: Long?): Response = Response.ok()
        .entity(transactionService.findAll(userId))
        .build()

}