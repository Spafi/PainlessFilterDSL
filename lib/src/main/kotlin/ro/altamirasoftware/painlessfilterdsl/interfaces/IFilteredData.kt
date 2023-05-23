package ro.altamirasoftware.painlessfilterdsl.interfaces

interface IFilteredData<T> {
    val data: List<T>
    val totalElements: Long
    val totalPages: Int
    val hasNext: Boolean
    val hasPrevious: Boolean
}
