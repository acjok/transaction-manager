package controller

import dto.UserDto
import org.glassfish.jersey.process.internal.RequestScoped
import service.UserService
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@RequestScoped
@Path("/api/users")
class UserController {
    @Inject
    private lateinit var userService: UserService

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createUser(@Valid userDto: UserDto): Response = Response.status(201)
        .entity(userService.createUser(userDto))
        .build()

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getUsers(): Response = Response.ok()
        .entity(userService.findAll())
        .build()

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getUser(@PathParam("userId") userId: Long): Response = Response.ok()
        .entity(userService.findById(userId))
        .build()
}