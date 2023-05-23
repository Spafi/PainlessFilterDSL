package ro.altamirasoftware.painlessfilterdsl.models

import com.querydsl.core.types.Path
import ro.altamirasoftware.painlessfilterdsl.core.SortOrder

data class SortRequest(
    val columnPath: Path<*>,
    val direction: SortOrder
)
