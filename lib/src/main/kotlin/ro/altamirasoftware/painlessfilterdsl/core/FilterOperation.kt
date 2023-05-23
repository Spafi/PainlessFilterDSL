package ro.altamirasoftware.painlessfilterdsl.core

import com.querydsl.core.types.Predicate
import ro.altamirasoftware.painlessfilterdsl.models.FilterRequest
import ro.altamirasoftware.painlessfilterdsl.outcome.Outcome

enum class FilterOperation {

    EQUAL,
    NOT_EQUAL,
    CONTAINS,
    NOT_CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    DATE_BEFORE,
    DATE_AFTER,
    IN_LIST,
    NOT_IN_LIST;


    /**
     * Builds a Predicate for the specified filter operation based on the given filter request
     * and the entity class.
     *
     * @param filterRequest The filter request containing the filter information.
     * @return An Outcome object containing the Predicate for the filter operation or a list of errors if the building process fails.
     */
    fun build(
        filterRequest: FilterRequest
    ): Outcome<Predicate> {
        val operation = when (this) {
            EQUAL, NOT_EQUAL -> getOperationForEqualNotEqual()
            CONTAINS, NOT_CONTAINS -> getOperationForContainsNotContains()
            STARTS_WITH, ENDS_WITH -> getOperationForStartsWithEndsWith()
            DATE_BEFORE, DATE_AFTER -> getOperationForDateBeforeAfter()
            IN_LIST, NOT_IN_LIST -> getOperationForInListNotInList()
        }
        return operation.build(filterRequest)
    }

    private fun getOperationForEqualNotEqual(): BaseOperation =
        if (this == EQUAL) EqualOperation() else NotEqualOperation()

    private fun getOperationForContainsNotContains(): BaseOperation =
        if (this == CONTAINS) ContainsOperation() else NotContainsOperation()

    private fun getOperationForStartsWithEndsWith(): BaseOperation =
        if (this == STARTS_WITH) StartsWithOperation() else EndsWithOperation()

    private fun getOperationForDateBeforeAfter(): BaseOperation =
        if (this == DATE_BEFORE) DateBeforeOperation() else DateAfterOperation()

    private fun getOperationForInListNotInList(): BaseOperation =
        if (this == IN_LIST) InListOperation() else NotInListOperation()
}
