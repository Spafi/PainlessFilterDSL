package ro.altamirasoftware.painlessfilterdsl.logger.extensions


import org.slf4j.LoggerFactory
import ro.altamirasoftware.painlessfilterdsl.outcome.Outcome
import ro.altamirasoftware.painlessfilterdsl.outcome.errors
import ro.altamirasoftware.painlessfilterdsl.outcome.value
import kotlin.reflect.full.companionObject

interface Logging

@Suppress("unused")
inline fun <reified T : Logging> T.logger(): org.slf4j.Logger =
    LoggerFactory.getLogger(getClassForLogging(T::class.java))

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}

fun <T> Outcome<T>.toLoggableString(): String =
    if (this.isFailed) {
        "Failed with errors: ${this.errors().map { it.toString() }}"
    } else {
        "Successful for ${this.value()}"
    }
