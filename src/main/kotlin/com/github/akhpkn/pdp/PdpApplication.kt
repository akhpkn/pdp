package com.github.akhpkn.pdp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class PdpApplication

fun main(args: Array<String>) {
    runApplication<PdpApplication>(*args)
}
