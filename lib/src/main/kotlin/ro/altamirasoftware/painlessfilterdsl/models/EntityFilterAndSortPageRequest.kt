package ro.altamirasoftware.painlessfilterdsl.models

import com.querydsl.core.types.Path
import ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto
import ro.altamirasoftware.painlessfilterdsl.interfaces.IEntityFilterAndSortPageRequest


/**
 * This Class transforms filters based on DTO objects to filters based on Entity objects.
 *
 * @param <E> The type of the entity class for which the filter and sort page request is created.
 * @param [filterRequest] The ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto object containing filter, sort, page, and size information.
 * @param [entityClass] The class object representing the type of the entity on which the filters will be applied.
 * @param [dtoToEntityMapper] A lambda function that maps a given entity class to a map of column names and their corresponding Path objects.
 * @property [filters] A list of FilterRequest objects, created by mapping the filter requests from the ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto
 * to the actual entity fields using the dtoToEntityMapper function. Throws an InternalServerError if a field is not mapped to an entity field.
 * @property [sort] A list of SortRequest objects, created by mapping the sort requests from the ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto
 * to the actual entity fields using the dtoToEntityMapper function. Throws an InternalServerError if a field is not mapped to an entity field.
 * @property [page] The page number for the paginated request, obtained from the ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto.
 * @property [size] The size (number of records per page) for the paginated request, obtained from the ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto.
 *
 *
 *  Call sample:
 *
 *    val excludeProductIds = FilterRequest(
 *                    columnPath = QProductEntity.productEntity.id,
 *                    fieldType = FieldType.UUID,
 *                    operation = ro.altamirasoftware.painlessfilterdsl.core.FilterOperation.NOT_IN_LIST,
 *                    values = incompatibleIdsList.map { it.getValue() }
 *               )
 *    val entityFilters = EntityFilterAndSortPageRequest(query.request, ProductEntity::class.java, ProductDto.Companion::mapTo)
 *    val processedFilters = FilteringService.validateAndAddOptionalFilters(entityFilters, excludeProductIds)
 *
 */
class EntityFilterAndSortPageRequest<E>(
    private val filterRequest: FilteredSortedPageRequestDto,
    private val entityClass: Class<E>,
    private val dtoToEntityMapper: (entityClass: Class<E>) -> Map<String, Path<*>>
) : IEntityFilterAndSortPageRequest<E> {

    override val filters: List<FilterRequest>
        get() = this.filterRequest.filters.map {

            try {
                val entityColumnPath = dtoToEntityMapper(entityClass).getValue(it.columnName)

                FilterRequest(
                    columnPath = entityColumnPath,
                    fieldType = it.fieldType,
                    value = it.value,
                    operation = it.operation,
                    valueTo = it.valueTo,
                    values = it.values
                )

            } catch (e: NoSuchElementException) {
                throw Exception("Field ${it.columnName} not mapped to an entity field")
            }
        }

    override val sort: List<SortRequest>
        get() = this.filterRequest.sort.map {

            try {
                val entityColumnPath = dtoToEntityMapper(entityClass).getValue(it.columnName)

                SortRequest(
                    columnPath = entityColumnPath,
                    direction = it.direction
                )
            } catch (e: NoSuchElementException) {
                throw Exception("Field ${it.columnName} not mapped to an entity field")
            }
        }

    override val page: Int
        get() = this.filterRequest.page

    override val size: Int
        get() = this.filterRequest.size

}
