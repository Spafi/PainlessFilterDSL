package ro.altamirasoftware.painlessfilterdsl.core

import ro.altamirasoftware.painlessfilterdsl.errors.EmptyFilterValueError
import ro.altamirasoftware.painlessfilterdsl.errors.FilterFieldTypeParsingError
import ro.altamirasoftware.painlessfilterdsl.logger.extensions.Logging
import ro.altamirasoftware.painlessfilterdsl.logger.extensions.logger
import ro.altamirasoftware.painlessfilterdsl.outcome.Failed
import ro.altamirasoftware.painlessfilterdsl.outcome.Outcome
import ro.altamirasoftware.painlessfilterdsl.outcome.Success
import ro.altamirasoftware.painlessfilterdsl.validation.valueIsEmpty
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Enum class representing the different field types that can be parsed.
 * Each field type has its own [parse] method to convert a [String] value into the corresponding data type.
 * The parsed value is wrapped in an [Outcome] object.
 */
@SuppressWarnings("SwallowedException")
enum class FieldType {

    BOOLEAN {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toBooleanStrict)
    },

    DOUBLE {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toDouble)
    },

    INTEGER {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toInt)
    },

    LONG {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toLong)
    },

    STRING {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toString)
    },

    ENUM {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvert(value, String::toString)
    },

    UUID {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvertWithExceptionHandling(value, ::parseUUID)
    },

    DATE {
        override fun parse(value: String?): Outcome<Any> = nullSafeConvertWithExceptionHandling(value, ::parseDate)
    };


    /**
     * Safely attempts to convert the given [String] [value] into a desired type using the provided [converter] function.
     * If the [value] is null or empty, returns a [Failed] outcome with an [EmptyFilterValueError].
     * If the conversion fails due to an [IllegalArgumentException], returns a [Failed] outcome with a [FilterFieldTypeParsingError].
     *
     * @param value The [String] value to be converted.
     * @param converter A function to convert the [String] value to the desired type.
     * @return An [Outcome] object containing the result of the conversion.
     */
    fun nullSafeConvert(
        value: String?,
        converter: String.() -> Any
    ): Outcome<Any> {
        if (valueIsEmpty(value)) {
            return Failed(EmptyFilterValueError(this))
        }
        return try {
            Success(converter.invoke(value!!))

        } catch (e: IllegalArgumentException) {
            Failed(FilterFieldTypeParsingError(value!!))
        }
    }

    /**
     * Safely attempts to convert the given [String] [value] into a desired type using the provided [converter] function
     * with exception handling.
     * If the [value] is null or empty, returns a [Failed] outcome with an [EmptyFilterValueError].
     * If the conversion fails due to any [Exception], returns a [Failed] outcome with a [FilterFieldTypeParsingError].
     *
     * @param value The [String] value to be converted.
     * @param converter A function to convert the [String] value to the desired type.
     * @return An [Outcome] object containing the result of the conversion.
     */
    fun nullSafeConvertWithExceptionHandling(
        value: String?,
        converter: (String) -> Any
    ): Outcome<Any> {
        if (valueIsEmpty(value)) {
            return Failed(EmptyFilterValueError(this))
        }
        return try {
            Success(converter.invoke(value!!))
        } catch (e: Exception) {
            Failed(FilterFieldTypeParsingError(value!!))
        }
    }

    fun parseUUID(value: String): Any = java.util.UUID.fromString(value)

    fun parseDate(value: String): Any {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss.SSS'Z']")
        val onlyDate: LocalDate = LocalDate.parse(value, formatter)
        return onlyDate.atStartOfDay(ZoneId.systemDefault())
    }

    abstract fun parse(value: String?): Outcome<Any>

    companion object : Logging {

        val logger = logger()
    }
}
