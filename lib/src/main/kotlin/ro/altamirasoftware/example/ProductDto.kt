package ro.altamirasoftware.example

import java.util.*

data class ProductDto(
    val id: UUID,
    val dtoName: String,
    val status: String,
) {
    companion object
}
