package ro.altamirasoftware.painlessfilterdsl.interfaces


import ro.altamirasoftware.painlessfilterdsl.models.FilterRequest
import ro.altamirasoftware.painlessfilterdsl.models.SortRequest

interface IEntityFilterAndSortPageRequest<E> {
    val filters: List<FilterRequest>
    val sort: List<SortRequest>
    val page: Int
    val size: Int
}
