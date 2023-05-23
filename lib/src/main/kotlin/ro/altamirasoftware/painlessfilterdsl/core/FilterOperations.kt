package ro.altamirasoftware.painlessfilterdsl.core


import com.querydsl.core.types.Ops
import com.querydsl.core.types.Path
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.EnumPath
import com.querydsl.core.types.dsl.Expressions
import ro.altamirasoftware.painlessfilterdsl.models.FilterRequest
import ro.altamirasoftware.painlessfilterdsl.outcome.*


abstract class BaseOperation {

    /**
     * The build function is responsible for constructing a Querydsl Predicate using a specific operation.
     *
     * @param filterRequest The filter request containing the column name, field type, operation and value(s) to filter.
     * @return An Outcome containing the constructed Predicate or a Failure with errors.
     */
    abstract fun build(filterRequest: FilterRequest): Outcome<Predicate>


    /**
     * Builds a generic Predicate for the given filter request with the specified operation.
     *
     * This function is used to construct a QueryDSL Predicate for filtering data based on the provided filter request.
     * It supports basic data types and Enum type, and handles the provided operation with the option to negate it.
     *
     * @param filterRequest The FilterRequest object containing the column path, field type, operation, and filter values.
     * @param operation The Ops object representing the operation to be applied for the filter request.
     * @param negateOperation A Boolean flag to determine if the operation should be negated (default is false).
     *
     * @return An Outcome object containing the built Predicate if successful, or containing errors if the parsing or building process fails.
     *
     * @see FilterRequest
     * @see Ops
     * @see Predicate
     * @see FieldType
     */
    protected fun buildGenericNegatable(
        filterRequest: FilterRequest, operation: Ops, negateOperation: Boolean = false
    ): Outcome<Predicate> {
        val (columnPath, fieldType, value) = filterRequest

        val parseValueOutcome = parseValue(fieldType, value, filterRequest.values)

        if (parseValueOutcome.isFailed) {
            return Failed(parseValueOutcome.errors())
        }

        val parsedValue = parseValueOutcome.value()

        return buildPredicate(fieldType, negateOperation, operation, columnPath, parsedValue)
    }

    /**
     * Parses the filter value(s) based on the FieldType provided.
     *
     * This function is responsible for parsing the filter value or values from the filter request.
     * If a single value is provided, it parses it using the FieldType's parse function.
     * If multiple values are provided, it aggregates the parsing results into a single Outcome.
     *
     * @param fieldType The FieldType of the column path in the filter request.
     * @param value The single value string to be parsed (nullable).
     * @param values The list of values to be parsed (nullable).
     *
     * @return An Outcome object containing the parsed value(s) if successful or errors if the parsing process fails.
     *
     * @see FieldType
     * @see Outcome
     */
    private fun parseValue(fieldType: FieldType, value: String?, values: List<Any>?): Outcome<out Any> {
        return value?.let { fieldType.parse(it) } ?: Outcome.aggregate(
            (values ?: listOf()).map { fieldType.parse(it.toString()) })
    }

    /**
     * Builds a Predicate based on the FieldType provided.
     *
     * This function delegates the Predicate building process to the appropriate function
     * depending on the FieldType (ENUM or other types).
     *
     * @param fieldType The FieldType of the column path in the filter request.
     * @param negateOperation A Boolean flag to determine if the operation should be negated.
     * @param operation The Ops object representing the operation to be applied for the filter request.
     * @param columnPath The Path object representing the column path for filtering.
     * @param parsedValue The parsed value for the filter request.
     *
     * @return An Outcome object containing the built Predicate if successful.
     *
     * @see FieldType
     * @see Predicate
     * @see Ops
     * @see Path
     */
    private fun buildPredicate(
        fieldType: FieldType,
        negateOperation: Boolean,
        operation: Ops,
        columnPath: Path<*>,
        parsedValue: Any,
    ): Outcome<Predicate> {
        return when (fieldType) {
            FieldType.ENUM -> buildEnumPredicate(negateOperation, operation, columnPath, parsedValue)
            else -> buildDefaultPredicate(negateOperation, operation, columnPath, parsedValue)
        }
    }

    /**
     * Builds an ENUM Predicate based on the provided parameters.
     *
     * This function constructs a QueryDSL Predicate specifically for filtering ENUM type fields.
     *
     * @param negateOperation A Boolean flag to determine if the operation should be negated.
     * @param operation The Ops object representing the operation to be applied for the filter request.
     * @param columnPath The Path object representing the column path for filtering.
     * @param parsedValue The parsed value for the filter request.
     *
     * @return An Outcome object containing the built Predicate if successful.
     *
     * @see Predicate
     * @see Ops
     * @see Path
     */
    private fun buildEnumPredicate(
        negateOperation: Boolean,
        operation: Ops,
        columnPath: Path<*>,
        parsedValue: Any
    ): Outcome<Predicate> {
        val predicate = Expressions.predicate(
            operation,
            (columnPath as EnumPath<*>).stringValue(),
            Expressions.constant(parsedValue.toString())
        )

        return if (negateOperation) Success(predicate.not()) else Success(predicate)
    }

    /**
     * Builds a default Predicate based on the provided parameters.
     *
     * This function constructs a QueryDSL Predicate for non-ENUM type fields.
     *
     * @param negateOperation A Boolean flag to determine if the operation should be negated.
     * @param operation The Ops object representing the operation to be applied for the filter request.
     * @param columnPath The Path object representing the column path for filtering.
     * @param parsedValue The parsed value for the filter request.
     *
     * @return An Outcome object containing the built Predicate if successful.
     *
     * @see Predicate
     * @see Ops
     * @see Path
     */
    private fun buildDefaultPredicate(
        negateOperation: Boolean,
        operation: Ops,
        columnPath: Path<*>,
        parsedValue: Any
    ): Outcome<Predicate> {

        val predicate = Expressions.predicate(
            operation, columnPath, Expressions.constant(parsedValue)
        )

        return if (negateOperation) Success(predicate.not()) else Success(predicate)
    }
}

class EqualOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.EQ)
}

class NotEqualOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.NE)
}

class ContainsOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.STRING_CONTAINS_IC)
}

class NotContainsOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(
            filterRequest, Ops.STRING_CONTAINS_IC, negateOperation = true
        )
}

class StartsWithOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.STARTS_WITH_IC)
}

class EndsWithOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.ENDS_WITH_IC)
}

class DateBeforeOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.LT)
}

class DateAfterOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.GT)
}

class InListOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.IN)
}

class NotInListOperation : BaseOperation() {
    override fun build(filterRequest: FilterRequest): Outcome<Predicate> =
        buildGenericNegatable(filterRequest, Ops.NOT_IN)
}
