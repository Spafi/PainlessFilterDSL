package ro.altamirasoftware.example.entities
import ro.altamirasoftware.example.core.IEmbeddableId
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable


@Embeddable
class ProductForCategoryId(
    @Column(name = "product_id")
    private val productId: UUID? = null,

    @Column(name = "category_id")
    private val categoryId: UUID? = null
) : IEmbeddableId {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductForCategoryId

        return Objects.equals(productId, other.productId) &&
                Objects.equals(categoryId, other.categoryId)
    }

    override fun hashCode(): Int = Objects.hash(productId, categoryId)

    override fun equalsToId(to: Any): Boolean = equals(to)
}
