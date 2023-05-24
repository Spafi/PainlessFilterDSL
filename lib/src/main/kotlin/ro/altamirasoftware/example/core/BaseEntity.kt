package ro.altamirasoftware.example.core

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
class BaseEntity : Serializable {

    @Id
    lateinit var id: UUID

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BaseEntity

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
