package ro.altamirasoftware.example.entities

import ro.altamirasoftware.example.core.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "categories")
@Table(name = "categories")
class CategoryEntity(

    @Column(nullable = false)
    val name: String,

    @OneToMany(mappedBy = "category")
    val products: List<ProductForCategoryEntity>? = null,
) : BaseEntity()

