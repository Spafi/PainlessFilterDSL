package ro.altamirasoftware.example.core

import ro.altamirasoftware.example.ProductDto
import com.querydsl.core.types.Path
import org.springframework.data.domain.Page
import ro.altamirasoftware.example.entities.ProductEntity
import ro.altamirasoftware.example.entities.QProductEntity
import ro.altamirasoftware.painlessfilterdsl.models.FilteredData
import java.io.Serializable


interface IDomainId<T> : Serializable {
    fun equalsToId(to: Any): Boolean
}


fun <E> filteredData(page: Page<E>): FilteredData<E> = FilteredData(
    page.content,
    page.totalElements,
    page.totalPages,
    page.hasNext(),
    page.hasPrevious(),
)


fun ProductDto.Companion.dtoToEntityPathMapper(entity: Class<ProductEntity>): Map<String, Path<*>> {
    val rootEntity = QProductEntity.productEntity
    val product = rootEntity
    return mapOf(
        ProductDto::id.name to product.id,
        ProductDto::dtoName.name to product.name,
        ProductDto::status.name to product.status,
    )
}
