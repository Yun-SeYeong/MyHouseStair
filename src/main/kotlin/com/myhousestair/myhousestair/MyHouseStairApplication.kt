package com.myhousestair.myhousestair

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MyHouseStairApplication

fun main(args: Array<String>) {
    runApplication<MyHouseStairApplication>(*args)
}
