package ro.altamirasoftware.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Demo

fun main(args: Array<String>) {
    runApplication<Demo>(*args)
}
