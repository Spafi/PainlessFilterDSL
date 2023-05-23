package ro.altamirasoftware.painlessfilterdsl.validation

fun valueIsEmpty(value: Any?): Boolean =
    when (value) {
        null -> true
        is Char -> value.isDefined()
        is String -> value.isBlank()
        is Collection<*> -> value.isEmpty()
        is Map<*, *> -> value.isEmpty()
        is Array<*> -> value.isEmpty()
        else -> false
    }

fun valueIsNotEmpty(value: Any?): Boolean =
    !valueIsEmpty(value)

fun Any?.isEmptyOrUndefined() =
    when (this) {
        null -> true
        is Char -> this.isDefined()
        is String -> this.isBlank()
        is Collection<*> -> this.isEmpty()
        is Map<*, *> -> this.isEmpty()
        is Array<*> -> this.isEmpty()
        else -> false
    }

fun Any?.isNotEmptyOrUndefined() =
        !this.isEmptyOrUndefined()
