package ro.altamirasoftware.example

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ro.altamirasoftware.example.core.dtoToEntityPathMapper
import ro.altamirasoftware.example.entities.ProductEntity
import ro.altamirasoftware.painlessfilterdsl.FilteringService
import ro.altamirasoftware.painlessfilterdsl.dtos.FilteredSortedPageRequestDto
import ro.altamirasoftware.painlessfilterdsl.models.EntityFilterAndSortPageRequest
import ro.altamirasoftware.painlessfilterdsl.outcome.errors
import ro.altamirasoftware.painlessfilterdsl.outcome.value


@RestController
@RequestMapping("/api/example")
class DataController(private val productRepository: ProductRepository) {


    /**
     * Endpoint handling POST requests to "/filteredData".
     *
     * @param request The [FilteredSortedPageRequestDto] object received as the request body.
     * @return A [ResponseEntity] containing either the filtered and sorted data with an OK HTTP status code, or validation error messages with a BadRequest HTTP status code.
     *
     * The method processes the following steps:
     * 1. It creates an [EntityFilterAndSortPageRequest] instance using the incoming request data,
     *    the `ProductEntity` class, and the pathMap of `ProductDto`.
     * 2. It calls `FilteringService.validateAndAddOptionalFilters` method to validate and process the filters
     *    contained in the [EntityFilterAndSortPageRequest].
     * 3. If the validation fails (indicated by [Outcome.isFailed]), it returns a BadRequest HTTP status code with
     *    the validation error messages. The error messages can be retrieved from the [Outcome] instance using
     *    the [Outcome.errors] method.
     * 4. If the validation is successful, it calls `productRepository.findAllFiltered` method with the validated
     *    filters to retrieve the filtered data from the database.
     * 5. It returns the retrieved data encapsulated in a [ResponseEntity] with an OK HTTP status code.
     */
    @PostMapping("/filteredData")
    fun getFilteredSortedData(@RequestBody request: FilteredSortedPageRequestDto): ResponseEntity<Any> {


        val entityFilters = EntityFilterAndSortPageRequest(
            request,
            ProductEntity::class.java,
            ProductDto.Companion::dtoToEntityPathMapper
        )

        val processedFilters = FilteringService.validateAndAddOptionalFilters(entityFilters)
        if (processedFilters.isFailed) {
            return ResponseEntity.badRequest().body(processedFilters.errors())
        }

        val data = productRepository.findAllFiltered(processedFilters.value())

        return ResponseEntity.ok(data)
    }
}
