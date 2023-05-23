package ro.altamirasoftware.painlessfilterdsl.models

import com.querydsl.core.types.Path
import ro.altamirasoftware.painlessfilterdsl.core.FieldType
import ro.altamirasoftware.painlessfilterdsl.core.FilterOperation

data class FilterRequest(
    val columnPath: Path<*>,
    val fieldType: FieldType,
    val value: String? = null,
    val operation: FilterOperation,
    val valueTo: String? = null,
    val values: List<Any>? = null
)
