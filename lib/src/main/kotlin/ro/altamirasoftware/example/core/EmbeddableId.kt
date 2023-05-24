package ro.altamirasoftware.example.core

import java.io.Serializable

interface IEmbeddableId : Serializable, IDomainId<IEmbeddableId> {
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}
