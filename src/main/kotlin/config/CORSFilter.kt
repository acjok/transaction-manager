package config

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter


class CORSFilter : ContainerResponseFilter {

    override fun filter(requestContext: ContainerRequestContext?, responseContext: ContainerResponseContext?) {
        responseContext?.headers?.add("Access-Control-Allow-Origin", "https://localhost")
        responseContext?.headers?.add("Access-Control-Allow-Methods", "API, GET, POST, PUT, UPDATE, OPTIONS")
        responseContext?.headers?.add("Access-Control-Max-Age", "151200")
        responseContext?.headers?.add("Access-Control-Allow-Headers", "x-requested-with,Content-Type")
        val requestHeader = requestContext?.getHeaderString("Access-Control-Request-Headers")

        if (requestHeader != null) {
            responseContext?.headers?.add("Access-Control-Allow-Headers", requestHeader)
        }
    }

}