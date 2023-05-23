package ro.altamirasoftware.painlessfilterdsl.errors

import ro.altamirasoftware.painlessfilterdsl.core.FieldType
import ro.altamirasoftware.painlessfilterdsl.core.SortOrder
import ro.altamirasoftware.painlessfilterdsl.exceptions.BaseError

class FilterFieldTypeParsingError(field: String) : BaseError(
    name = "invalid_filter_field_type",
    message = "invalid filter field type",
    field = field
)

class EmptyFilterValueError(field: FieldType) : BaseError(
    name = "empty_filter_value",
    message = "empty filter value",
    field = field.toString()
)

class EmptySortFieldError(order: SortOrder) : BaseError(
    name = "empty_sort_field",
    message = "empty sort field",
    field = order.toString()
)
