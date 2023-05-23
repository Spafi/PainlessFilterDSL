package ro.altamirasoftware.painlessfilterdsl.mappers

import ro.altamirasoftware.painlessfilterdsl.dtos.FilterableFieldDto
import ro.altamirasoftware.painlessfilterdsl.models.FilterableField


fun FilterableField.toDto() = FilterableFieldDto(name, type, values.map { it.toString() })

fun List<FilterableField>.toDtoList(): List<FilterableFieldDto> {
    return this.map { it.toDto() }
}
