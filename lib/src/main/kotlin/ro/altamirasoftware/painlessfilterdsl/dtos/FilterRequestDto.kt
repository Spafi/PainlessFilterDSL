package ro.altamirasoftware.painlessfilterdsl.dtos

import ro.altamirasoftware.painlessfilterdsl.core.FieldType
import ro.altamirasoftware.painlessfilterdsl.core.FilterOperation

data class FilterRequestDto(
    val columnName: String,
    val fieldType: FieldType,
    val value: String? = null,
    val operation: FilterOperation,
    val valueTo: String? = null,
    val values: List<Any>? = null
)
