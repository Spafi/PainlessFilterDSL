package ro.altamirasoftware.painlessfilterdsl.outcome

import ro.altamirasoftware.painlessfilterdsl.exceptions.BaseError

@Suppress("UNCHECKED_CAST")
class OutcomeAccumulator<T> : Outcome<T>(false) {

    private val value: MutableList<T> = mutableListOf()
    private val errors: MutableList<BaseError> = mutableListOf()

    override fun <R : Outcome<T>> getInstance(): R = this as R

    fun getValue(): MutableList<T> {
        return value
    }

    fun getErrors(): List<BaseError> = errors

    fun addResult(result: Outcome<T>): OutcomeAccumulator<T> {

        when (result.isFailed) {
            true -> {
                isFailed = true
                errors += result.isA<Failed<T>>().getErrors()

            }

            else -> {
                if (isFailed) {
                    return this
                }

                value.add(result.isA<Success<T>>().getValue())
            }
        }

        return this
    }
}

interface ResultProvider {
    fun <T, R> handle(onSuccess: (value: T) -> R, onError: (errors: List<BaseError>) -> R): R
}

interface ResultProviderAcc {
    fun <T, R> handle(
        onSuccess: (value: List<T>) -> R,
        onError: (errors: List<BaseError>) -> R
    ): R
}

fun <T> Outcome<T>.errors(): List<BaseError> = this.isA<Failed<T>>().getErrors()

fun <T> Outcome<T>.value(): T = this.isA<Success<T>>().getValue()


