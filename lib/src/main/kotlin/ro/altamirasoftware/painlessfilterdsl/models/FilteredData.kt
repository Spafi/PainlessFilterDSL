package ro.altamirasoftware.painlessfilterdsl.models

import java.io.Serializable

data class FilteredData<T>(
    val data: List<T> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
) : Serializable {
    private val serialVersionUID = 1234567L
}
