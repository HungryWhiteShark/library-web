package com.example.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling



@EnableScheduling
@SpringBootApplication
class LibraryWebApplication

fun main(args: Array<String>) {
	runApplication<LibraryWebApplication>(*args)
}
