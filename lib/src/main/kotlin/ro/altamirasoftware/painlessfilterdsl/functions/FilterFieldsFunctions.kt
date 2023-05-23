package ro.altamirasoftware.painlessfilterdsl.functions

import com.querydsl.core.types.Path
import ro.altamirasoftware.painlessfilterdsl.core.FieldType
import ro.altamirasoftware.painlessfilterdsl.models.FilterableField
import ro.altamirasoftware.painlessfilterdsl.validation.isNotEmptyOrUndefined
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KProperty1

/**
 * Returns a nested property name as a string.
 *
 * @param properties A vararg of properties.
 * @return A concatenated string of property names separated by periods.
 */
fun nestedPropertyName(vararg properties: KProperty1<*, *>): String {
    return properties.joinToString(separator = ".") { it.name }
}


/**
 * Returns a list of filterable fields for the given entity class.
 *
 * @param entityClass The entity class to inspect.
 * @param maxDepth The maximum depth of nested fields.
 * @param ignoredFields A list of field names to ignore.
 * @param parentField The parent field name (if applicable).
 * @return A list of [FilterableField] objects.
 */
@Suppress("UNCHECKED_CAST")
fun getFilterableFieldsAsListRecursively(
    entityClass: Class<*>,
    maxDepth: Int,
    ignoredFields: List<String> = mutableListOf(),
    parentField: String? = null
): List<FilterableField> =
    getFilterableFieldsAsListRecursivelyUnflattened(entityClass, maxDepth, ignoredFields, parentField).flatMap {
        when (it) {
            is List<*> -> it
            else       -> it.asMutableList()
        }
    } as List<FilterableField>


/**
 * A private helper function to recursively retrieve filterable fields.
 *
 * @param entityClass The entity class to inspect.
 * @param maxDepth The maximum depth of nested fields.
 * @param ignoredFields A list of field names to ignore.
 * @param parentField The parent field name (if applicable).
 *
 * @return A list of filterable fields, unflattened.
 */
private fun getFilterableFieldsAsListRecursivelyUnflattened(
    entityClass: Class<*>,
    maxDepth: Int,
    ignoredFields: List<String> = mutableListOf(),
    parentField: String? = null
): List<Any> {

    val entityFieldsList = mutableListOf<Any>()
    if (maxDepth == 0) return entityFieldsList

    entityClass.declaredFields.forEach { field ->

        val fieldWithParent = if (parentField.isNotEmptyOrUndefined()) "$parentField.${field.name}" else field.name
        val isIgnoredField = ignoredFields.contains(fieldWithParent)

        when {
            isIgnoredField -> return@forEach

            field.type.isEnum ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.ENUM,
                    values = field.type.enumConstants.map { it.toString() }
                )

            field.type.equals(String::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.STRING,
                    values = listOf()
                )

            field.type.equals(Boolean::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.BOOLEAN,
                    values = listOf()
                )

            field.type.equals(Int::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.INTEGER,
                    values = listOf()
                )

            field.type.equals(Long::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.LONG,
                    values = listOf()
                )

            field.type.equals(Double::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.DOUBLE,
                    values = listOf()
                )

            field.type.equals(ZonedDateTime::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.DATE,
                    values = listOf()
                )

            field.type.equals(UUID::class.java) ->
                entityFieldsList.addField(
                    name = fieldWithParent,
                    type = FieldType.STRING,
                    values = listOf()
                )

            field.type.declaredFields.isNotEmpty() -> {
                val nestedEntities =
                    getFilterableFieldsAsListRecursivelyUnflattened(
                        field.type,
                        maxDepth - 1,
                        ignoredFields,
                        fieldWithParent
                    )

                val flattened = nestedEntities.flatMap {
                    when (it) {
                        is List<*> -> it
                        else       -> it.asMutableList()
                    }
                }
                entityFieldsList.add(flattened)
            }
        }
    }

    return entityFieldsList
}

fun removeRoot(path: Path<*>): String = path.toString().replaceFirst("${path.root}.", "")

private fun MutableList<Any>.addField(name: String, type: FieldType, values: List<Any>) {
    this.add(
        FilterableField(
            name,
            type = type.toString(),
            values
        )
    )
}


fun <T : Any?> T?.asMutableList(): MutableList<T> = this?.let { mutableListOf(it) } ?: mutableListOf()
