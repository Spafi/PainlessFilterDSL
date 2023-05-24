package ro.altamirasoftware.example.entities
import com.querydsl.core.annotations.QueryInit
import org.hibernate.annotations.Where
import ro.altamirasoftware.example.enums.ProductForCategoryStatusEnum
import ro.altamirasoftware.example.core.BaseRelationshipEntity
import java.util.*
import javax.persistence.*

@Entity(name = "product_categories")
@Table(name = "product_categories")
@Where(clause = "status != 'DELETED'")
class ProductForCategoryEntity(

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    val category: CategoryEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @QueryInit("*.*")
    val product: ProductEntity,

    @Enumerated(EnumType.STRING)
    val status: ProductForCategoryStatusEnum
) : BaseRelationshipEntity<ProductForCategoryId>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductForCategoryEntity

        return Objects.equals(category, other.category) &&
                Objects.equals(product, other.product) &&
                Objects.equals(status, other.status)
    }

    override fun hashCode(): Int = Objects.hash(category, product, status)
}
