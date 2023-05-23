package ro.altamirasoftware.painlessfilterdsl.outcome

import ro.altamirasoftware.painlessfilterdsl.exceptions.BaseError
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
abstract class Outcome<T>(var isFailed: Boolean) : Serializable {

    protected abstract fun <R : Outcome<T>> getInstance(): R

    fun <R : Outcome<T>> isA(): R = getInstance()

    companion object {

        fun of(result: Outcome<*>) = object : ResultProvider {
            override fun <T, R> handle(
                onSuccess: (value: T) -> R,
                onError: (errors: List<BaseError>) -> R
            ): R = when {
                result.isFailed -> onError((result as Failed).getErrors())
                else -> onSuccess((result as Success<T>).getValue())
            }
        }

        fun of(vararg results: Outcome<*>) = object : ResultProviderAcc {
            override fun <T, R> handle(
                onSuccess: (value: List<T>) -> R,
                onError: (errors: List<BaseError>) -> R
            ): R {
                val accResult =
                    results.fold(OutcomeAccumulator<T>()) { acc, baseResult -> acc.addResult(baseResult as Outcome<T>) }

                return when (accResult.isFailed) {
                    true -> onError(accResult.getErrors())
                    else -> onSuccess(accResult.getValue())
                }
            }
        }

        fun of(results: List<Outcome<*>>) = object : ResultProviderAcc {
            override fun <T, R> handle(
                onSuccess: (value: List<T>) -> R,
                onError: (errors: List<BaseError>) -> R
            ): R {
                val accResult =
                    results.fold(OutcomeAccumulator<T>()) { acc, baseResult -> acc.addResult(baseResult as Outcome<T>) }

                return when (accResult.isFailed) {
                    true -> onError(accResult.getErrors())
                    else -> onSuccess(accResult.getValue())
                }
            }
        }

        fun <T> aggregate(results: List<Outcome<T>>): Outcome<List<T>> {
            val accResult =
                results.fold(OutcomeAccumulator<T>()) { acc, baseResult -> acc.addResult(baseResult) }

            if (accResult.isFailed) {
                return Failed(accResult.getErrors())
            }

            return Success(accResult.getValue().toList())

        }
    }
}

@Suppress("UNCHECKED_CAST")
class Success<T>(private val value: T) : Outcome<T>(false), Serializable {

    fun getValue(): T = value

    override fun <R : Outcome<T>> getInstance(): R = this as R
}

@Suppress("UNCHECKED_CAST")
class Failed<T>(private val errors: List<BaseError>) : Outcome<T>(true), Serializable {

    constructor(error: BaseError) : this(listOf(error))

    fun getErrors(): List<BaseError> = errors

    override fun <R : Outcome<T>> getInstance(): R = this as R
}
