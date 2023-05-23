package ro.altamirasoftware.painlessfilterdsl.core


import com.querydsl.core.types.PathType
import org.springframework.data.domain.Sort
import ro.altamirasoftware.painlessfilterdsl.errors.EmptySortFieldError
import ro.altamirasoftware.painlessfilterdsl.functions.removeRoot
import ro.altamirasoftware.painlessfilterdsl.models.SortRequest
import ro.altamirasoftware.painlessfilterdsl.outcome.Failed
import ro.altamirasoftware.painlessfilterdsl.outcome.Outcome
import ro.altamirasoftware.painlessfilterdsl.outcome.Success
import ro.altamirasoftware.painlessfilterdsl.validation.valueIsEmpty

enum class SortOrder {
    ASC {
        override fun build(sortRequest: SortRequest): Outcome<Any> = nullSafeBuild(sortRequest, Sort.Direction.ASC)
    },

    DESC {
        override fun build(sortRequest: SortRequest): Outcome<Any> = nullSafeBuild(sortRequest, Sort.Direction.DESC)
    };

    /**
     * Creates a null-safe sort order based on the [sortRequest] and [direction].
     *
     * @param sortRequest Sort request with column path and metadata.
     * @param direction Sort direction (ASCENDING or DESCENDING).
     * @return [Outcome] representing a successful sort order ([Success]) or failure ([Failed]).
     *
     * If it is a collection, the function constructs a new sort column using the parent path without the root
     * and the collection column name.
     * If not a collection, it simply removes the root from the column path.
     * If the column path is empty, returns [Failed] with [EmptySortFieldError].
     */
    fun nullSafeBuild(sortRequest: SortRequest, direction: Sort.Direction): Outcome<Any> {
        if (valueIsEmpty(sortRequest.columnPath)) {
            return Failed(EmptySortFieldError(sortRequest.direction))
        }

        val columnPathMetadata = sortRequest.columnPath.metadata
        val collectionParent = columnPathMetadata.parent?.metadata?.parent
        val isCollection = columnPathMetadata.parent?.metadata?.pathType == PathType.COLLECTION_ANY

        val sortColumn = if (isCollection) {
            val parentWithoutRoot = removeRoot(collectionParent!!)
            val collectionColumnName = columnPathMetadata.element
             "$parentWithoutRoot.$collectionColumnName"
        } else {
            removeRoot(sortRequest.columnPath)
        }

        return Success(Sort.Order(direction, sortColumn))
    }

    abstract fun build(sortRequest: SortRequest): Outcome<Any>
}
