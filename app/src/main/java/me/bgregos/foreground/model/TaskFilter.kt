package me.bgregos.foreground.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Represents a filter for Tasks. These filters can be instantiated and chained together by the user
 *
 * In the examples below, the user wants to filter to tasks that have "renovation" as the project field.
 *
 * @property type type of filter. ex: PROJECT
 * @property parameter optional user-supplied parameter. ex: "renovation"
 * @property enabled this filter is enabled. most filters should default to true since they are usually created explicitly by the user
 * @property includeMatching filter out tasks that do match this filter, instead of those that do not.
 */
@Entity
data class TaskFilter (
        @PrimaryKey(autoGenerate = true) val id: Int,
        val type: TaskFilterType,
        var parameter: String?,
        var enabled: Boolean = true,
        var includeMatching: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return other is TaskFilter &&
                this.type == other.type &&
                this.parameter == other.parameter &&
                this.includeMatching == other.includeMatching
    }
}

/**
 * Represents an available type of filter. These are fixed ahead of time. The user
 * can select one of these types when creating a [TaskFilter]
 * @property name user-friendly name
 * @property id name used for persistence
 * @property parameterFormat type of parameter, used for user display. ex: "ParameterFormat.STRING" would give the user a text field.
 *      ParameterFormat.NONE indicates that this filter does not need a parameter. ex: a simple on/off filter
 * @property filter how to apply this to a collection. ex { task: Task, param: String? -> task.project == param }
 */
data class TaskFilterType (
        val name: String,
        val id: String,
        var parameterFormat: ParameterFormat,
        val filter: (Task, String?) -> Boolean
) {
    override fun equals(other: Any?): Boolean {
        return other is TaskFilterType &&
                this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Available filter types for the user to choose from.
 */
class TaskFiltersAvailable {
    companion object{
        val filters = listOf(
                TaskFilterType (
                        name = "Name",
                        id = "name",
                        parameterFormat = ParameterFormat.STRING,
                        filter = {
                            task: Task, param: String? -> param?.let { task.name.contains(it) } ?: false
                        }
                ),
                TaskFilterType (
                        name = "Project",
                        id = "project",
                        parameterFormat = ParameterFormat.STRING,
                        filter = {
                            task: Task, param: String? -> param?.let { task.project?.contains(it) } ?: false
                        }
                ),
                TaskFilterType (
                        name = "Tag",
                        id = "tag",
                        parameterFormat = ParameterFormat.STRING,
                        filter = {
                            task: Task, param: String? -> task.tags.any{t -> t.contains(param ?: return@TaskFilterType false) }
                        }
                ),
                TaskFilterType (
                        name = "Priority",
                        id = "priority",
                        parameterFormat = ParameterFormat.STRING,
                        filter = {
                            task: Task, param: String? -> task.priority?.contains(param ?: return@TaskFilterType false) ?: false
                        }
                ),
                TaskFilterType (
                        name = "Waiting",
                        id = "waiting",
                        parameterFormat = ParameterFormat.NONE,
                        filter = {
                            task: Task, _ -> task.waitDate?.let { Date().before(it) } ?: false
                        }
                )
        )
    }
}

/**
 * Known parameter formats - dictates how the parameter field is displayed to the user
 */
enum class ParameterFormat {
    NONE, STRING, DATE
}

