package ro.altamirasoftware.example.core

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseRelationshipEntity<T : Serializable> : Serializable {

    @EmbeddedId
    lateinit var id: T
}
