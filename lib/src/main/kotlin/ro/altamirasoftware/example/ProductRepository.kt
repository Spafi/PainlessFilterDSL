package ro.altamirasoftware.example

import com.querydsl.core.types.Predicate
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import ro.altamirasoftware.example.core.filteredData
import ro.altamirasoftware.example.entities.ProductEntity
import ro.altamirasoftware.painlessfilterdsl.models.FilteredData
import ro.altamirasoftware.painlessfilterdsl.models.FilteredSortedPageRequest
import ro.altamirasoftware.painlessfilterdsl.validation.isNotEmptyOrUndefined
import java.util.*

@Repository("IProductRepository")
interface IProductRepository : JpaRepository<ProductEntity, UUID>, QuerydslPredicateExecutor<ProductEntity> {
    override fun findAll(predicate: Predicate, pageable: Pageable): Page<ProductEntity>
    override fun findAll(pageable: Pageable): Page<ProductEntity>
}


@Primary
@Repository
@DependsOn("IProductRepository")
class ProductRepository(
    private val repository: IProductRepository
) {

    fun findAllFiltered(request: FilteredSortedPageRequest): FilteredData<ProductEntity> {
        val page = if (request.predicate.isNotEmptyOrUndefined()) {
            repository.findAll(request.predicate!!, request.pageRequest)
        } else {
            repository.findAll(request.pageRequest)
        }

        return filteredData(page)
    }

}
