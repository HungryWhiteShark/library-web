package com.example.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LibraryWebApplication

fun main(args: Array<String>) {
	runApplication<LibraryWebApplication>(*args)
}
