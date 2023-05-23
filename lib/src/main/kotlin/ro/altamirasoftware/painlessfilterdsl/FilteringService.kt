package ro.altamirasoftware.painlessfilterdsl

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ro.altamirasoftware.painlessfilterdsl.core.DEFAULT_PAGE
import ro.altamirasoftware.painlessfilterdsl.core.DEFAULT_PAGE_SIZE
import ro.altamirasoftware.painlessfilterdsl.interfaces.IEntityFilterAndSortPageRequest
import ro.altamirasoftware.painlessfilterdsl.models.FilterRequest
import ro.altamirasoftware.painlessfilterdsl.models.FilteredSortedPageRequest
import ro.altamirasoftware.painlessfilterdsl.models.SortRequest
import ro.altamirasoftware.painlessfilterdsl.outcome.*
import ro.altamirasoftware.painlessfilterdsl.validation.isNotEmptyOrUndefined
import java.util.*


/**
 *
 * Provides utility methods to build and validate filter and sort requests with pagination for a specific entity class.
 *
 * @property [createFilteredSortedPageRequest] Builds a [FilteredSortedPageRequest] from a PageRequest and a Predicate.
 * @property [buildPageRequest] Constructs a [PageRequest] with sorting, page size, and page number. Returns an [Outcome] of PageRequest.
 * @property [validateFilters] Validates a list of [FilterRequest] objects and returns an [Outcome] of the validated list.
 * @property [buildValidatedPredicateFromFilters] Validates filters and builds a Predicate. Returns an [Outcome] of Predicate.
 *
 */
object FilteringService {

    /**
     * Converts and adds optional filters to the given EntityFilterAndSortPageRequest, validates the filters, builds a predicate, and creates a FilteredSortedPageRequest.
     *
     *  @param <E> The entity type.
     *  @param [entityFilterAndSortPageRequest] The [IEntityFilterAndSortPageRequest] containing initial filters and sort requests.
     *  @param [additionalFilters] Additional [FilterRequest] objects (optional) to be added to the existing filters.
     *
     *  @return An [Outcome] of [FilteredSortedPageRequest] containing either the successfully constructed FilteredSortedPageRequest or errors.
     */
    fun <E> validateAndAddOptionalFilters(
        entityFilterAndSortPageRequest: IEntityFilterAndSortPageRequest<E>,
        vararg additionalFilters: FilterRequest
    ): Outcome<FilteredSortedPageRequest> {

        val allFilters = if (additionalFilters.isNotEmptyOrUndefined()) {
            entityFilterAndSortPageRequest.filters.plus(additionalFilters)
        } else {
            entityFilterAndSortPageRequest.filters
        }

        val filtersPredicateOutcome = buildValidatedPredicateFromFilters(allFilters)
        if (filtersPredicateOutcome.isFailed) {
            return Failed(filtersPredicateOutcome.errors())
        }

        val pageRequestOutcome =
            buildPageRequest(
                entityFilterAndSortPageRequest.sort,
                entityFilterAndSortPageRequest.size,
                entityFilterAndSortPageRequest.page
            )
        if (pageRequestOutcome.isFailed) {
            return Failed(pageRequestOutcome.errors())
        }

        return Success(
            createFilteredSortedPageRequest(pageRequestOutcome.value(), filtersPredicateOutcome.value())
        )
    }


    /**
     * Validates a list of [FilterRequest] objects and returns an Outcome of the validated list.
     * @param filters List of [FilterRequest] objects to be validated.
     * @return An [Outcome] of the validated list of [FilterRequest] objects.
     */
    fun validateFilters(filters: List<FilterRequest>): Outcome<List<FilterRequest>> {
        if (filters.isEmpty()) return Success(filters)

        val filtersOutcome = filters.map { filter ->
            filter.operation.build(filter)
        }

        val result = Outcome.aggregate(filtersOutcome)

        if (result.isFailed) {
            return Failed(result.errors())
        }

        return Success(filters)
    }


    /**
     * Builds a [FilteredSortedPageRequest] from a PageRequest and a Predicate.
     *
     * @param [pageRequest] The PageRequest to be used in the FilteredSortedPageRequest.
     * @param [predicate] The Predicate to be used in the FilteredSortedPageRequest.
     * @return A [FilteredSortedPageRequest] containing the PageRequest and Predicate.
     */
    private fun createFilteredSortedPageRequest(
        pageRequest: PageRequest,
        predicate: Predicate?
    ): FilteredSortedPageRequest =
        FilteredSortedPageRequest(pageRequest, predicate)


    /**
     * Constructs a PageRequest with sorting, page size, and page number.
     *
     * @param [sortOrder] List of [SortRequest] objects for sorting.
     * @param [pageSize] The number of records per page.
     * @param [pageNumber] The page number.
     * @return An [Outcome] of [PageRequest] containing either the successfully constructed [PageRequest] or errors.
     */
    private fun buildPageRequest(sortOrder: List<SortRequest>, pageSize: Int, pageNumber: Int): Outcome<PageRequest> {
        val sortOutcome = buildSortOrder(sortOrder)

        if (sortOutcome.isFailed) {
            return Failed(sortOutcome.errors())
        }

        val sortValues = sortOutcome.value()

        return when {
            sortValues.isNotEmptyOrUndefined() -> Success(
                PageRequest.of(
                    Objects.requireNonNullElse(pageNumber, DEFAULT_PAGE),
                    Objects.requireNonNullElse(pageSize, DEFAULT_PAGE_SIZE),
                    sortValues
                )
            )

            else -> Success(
                PageRequest.of(
                    Objects.requireNonNullElse(pageNumber, DEFAULT_PAGE),
                    Objects.requireNonNullElse(pageSize, DEFAULT_PAGE_SIZE)
                )
            )
        }
    }


    /**
     * Validates filters and builds a [Predicate].
     * @param filters List of [FilterRequest] objects to be validated and used to build a Predicate.
     * @return An [Outcome] of [Predicate] containing either the successfully constructed Predicate or errors.
     */
    fun buildValidatedPredicateFromFilters(filters: List<FilterRequest>): Outcome<Predicate> {

        if (filters.isEmpty()) return Success(BooleanBuilder())

        val filtersOutcome = filters.map { filter ->
            filter.operation.build(filter)
        }

        val result = Outcome.aggregate(filtersOutcome)

        if (result.isFailed) {
            return Failed(result.errors())
        }

        val filterValues = result.value()
        val filterPredicates = filterValues.reduce { acc, predicate -> ExpressionUtils.and(predicate, acc) }

        return Success(BooleanBuilder(filterPredicates))
    }

    private fun buildSortOrder(sortOrder: List<SortRequest>): Outcome<Sort> {

        val sortOutcome = sortOrder.map { sort -> sort.direction.build(sort) }
        val result = Outcome.aggregate(sortOutcome)

        if (result.isFailed) {
            return Failed(result.errors())
        }

        val sortValues = result.value().map { it as Sort.Order }

        return Success(Sort.by(sortValues))
    }
}
