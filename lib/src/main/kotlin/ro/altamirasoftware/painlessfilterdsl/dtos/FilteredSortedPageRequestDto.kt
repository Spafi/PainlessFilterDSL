package ro.altamirasoftware.painlessfilterdsl.dtos

data class FilteredSortedPageRequestDto(
    val filters: List<FilterRequestDto>,
    val sort: List<SortRequestDto>,
    val page: Int,
    val size: Int
)

