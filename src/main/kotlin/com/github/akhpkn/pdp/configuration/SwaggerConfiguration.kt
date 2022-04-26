package com.github.akhpkn.pdp.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    private val apiKey = ApiKey("JWT", "Authorization" , "header")
    private val defaultAuth = listOf(
        SecurityReference(
            "JWT",
            arrayOf(AuthorizationScope("global", "accessEverything"))
        )
    )
    private val securityContext = SecurityContext.builder().securityReferences(defaultAuth).build()

    @Bean
    fun docket(): Docket = Docket(DocumentationType.SWAGGER_2)
        .securityContexts(listOf(securityContext))
        .securitySchemes(listOf(apiKey))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
}
