package ro.altamirasoftware.painlessfilterdsl.exceptions


open class BaseError(
    val name: String,
    val message: String,
    val field: String? = null,
    val exception: Exception? = null
) {
    fun equalsTo(to: BaseError): Boolean {
        return this.name == to.name && this.field == to.field
    }
}

