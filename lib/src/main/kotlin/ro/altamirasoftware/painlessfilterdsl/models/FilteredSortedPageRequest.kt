package ro.altamirasoftware.painlessfilterdsl.models

import com.querydsl.core.types.Predicate
import org.springframework.data.domain.PageRequest

data class FilteredSortedPageRequest(
    val pageRequest: PageRequest,
    val predicate: Predicate?
)
