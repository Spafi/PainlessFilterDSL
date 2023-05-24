package ro.altamirasoftware.example.entities

import ro.altamirasoftware.example.core.BaseEntity
import ro.altamirasoftware.example.enums.ProductStatusEnum
import java.util.*
import javax.persistence.*

@Entity(name = "products")
@Table(name = "products")
class ProductEntity(

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    val status: ProductStatusEnum,

    @OneToMany(mappedBy = "product")
    val categories: List<ProductForCategoryEntity>? = null

    ) : BaseEntity()
