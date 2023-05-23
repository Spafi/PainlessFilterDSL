package ro.altamirasoftware.painlessfilterdsl.dtos

import ro.altamirasoftware.painlessfilterdsl.core.SortOrder

data class SortRequestDto(
    val columnName: String,
    val direction: SortOrder
)
