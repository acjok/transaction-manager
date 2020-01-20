import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.ws.rs.core.UriBuilder
import java.io.IOException
import com.sun.net.httpserver.HttpServer
import config.CORSFilter
import controller.TransactionController
import controller.UserController
import exceptions.CustomThrowableExceptionMapper
import exceptions.GenericExceptionMapper
import exceptions.ValidationExceptionMapper
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory
import service.TransactionService
import javax.ws.rs.ext.ContextResolver
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ServerProperties
import service.UserService
import storage.Storage
import javax.inject.Singleton
import javax.ws.rs.core.Application


private val serverUri = UriBuilder.fromUri(getHostName()).port(8080).build()

@Throws(IOException::class)
fun main() {
    println("Starting Embedded Jersey HTTPServer...\n")
    createHttpServer()
    println(
        String.format(
            "\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n",
            serverUri
        )
    )
    println("Started Embedded Jersey HTTPServer Successfully !!!")
}

@Throws(IOException::class)
private fun createHttpServer(): HttpServer {
    val resourceConfig = ResourceConfig.forApplication(JerseyApplication())
    resourceConfig.register(object : AbstractBinder() {
        override fun configure() {
            bindAsContract(Storage::class.java).`in`(Singleton::class.java)
            bindAsContract(UserService::class.java).`in`(Singleton::class.java)
            bindAsContract(TransactionService::class.java).`in`(Singleton::class.java)
        }
    })
    resourceConfig.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
    resourceConfig.register(CORSFilter::class.java)
    resourceConfig.register(CustomThrowableExceptionMapper::class.java)
    resourceConfig.register(ValidationExceptionMapper::class.java)
    resourceConfig.register(GenericExceptionMapper::class.java)
    resourceConfig.register(ContextResolver<ObjectMapper> { ObjectMapper().registerModule(KotlinModule()) })
    return JdkHttpServerFactory.createHttpServer(serverUri, resourceConfig)
}

private fun getHostName() = "http://localhost/"

class JerseyApplication: Application() {
    override fun getSingletons(): MutableSet<Any> {
        return mutableSetOf(TransactionController(), UserController())
    }
}
