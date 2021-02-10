package com.escodro.task.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.escodro.task.R
import com.escodro.task.model.Category
import com.escodro.task.model.Task
import com.escodro.theme.AlkaaTheme
import com.escodro.theme.components.DefaultIconTextContent
import org.koin.androidx.compose.getViewModel

/**
 * Alkaa Task Detail Section.
 *
 * @param taskId the id from the task to be shown
 */
@Composable
fun TaskDetailSection(taskId: Long) {
    TaskDetailLoader(taskId = taskId)
}

@Composable
private fun TaskDetailLoader(
    taskId: Long,
    viewModel: TaskDetailViewModel = getViewModel()
) {
    viewModel.setTaskInfo(taskId = taskId)
    val state = viewModel.state.collectAsState()

    when (state.value) {
        TaskDetailState.Error -> TaskDetailError()
        is TaskDetailState.Loaded -> {
            val task = (state.value as TaskDetailState.Loaded).task
            val categories = (state.value as TaskDetailState.Loaded).categoryList
            TaskDetailContent(
                task = task,
                categories = categories,
                onTitleChanged = viewModel::updateTitle,
                onDescriptionChanged = viewModel::updateDescription,
                onCategoryChanged = viewModel::updateCategory
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    categories: List<Category>,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCategoryChanged: (Long?) -> Unit
) {
    Surface(color = MaterialTheme.colors.background) {
        Column {
            TaskTitleTextField(text = task.title, onTitleChanged = onTitleChanged)
            TaskDescriptionTextField(
                text = task.description,
                onDescriptionChanged = onDescriptionChanged
            )
            CategorySelection(
                categories = categories,
                currentCategory = task.categoryId,
                onCategoryChanged = onCategoryChanged
            )
        }
    }
}

@Composable
private fun TaskDetailError() {
    DefaultIconTextContent(
        icon = Icons.Outlined.Close,
        iconContentDescription = R.string.task_detail_header_error,
        header = R.string.task_detail_content_description_error
    )
}

@Composable
private fun TaskTitleTextField(text: String, onTitleChanged: (String) -> Unit) {
    val textState = remember { mutableStateOf(TextFieldValue(text)) }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = textState.value,
        onValueChange = {
            onTitleChanged(it.text)
            textState.value = it
        },
        textStyle = MaterialTheme.typography.h4,
        backgroundColor = MaterialTheme.colors.background,
    )
}

@Composable
private fun TaskDescriptionTextField(text: String?, onDescriptionChanged: (String) -> Unit) {
    val textState = remember { mutableStateOf(TextFieldValue(text ?: "")) }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "menu"
            )
        },
        value = textState.value,
        onValueChange = {
            onDescriptionChanged(it.text)
            textState.value = it
        },
        textStyle = MaterialTheme.typography.body1,
        backgroundColor = MaterialTheme.colors.background,
    )
}

@Composable
private fun CategorySelection(
    categories: List<Category>,
    currentCategory: Long?,
    onCategoryChanged: (Long?) -> Unit
) {
    val currentItem = categories.find { category -> category.id == currentCategory }
    val selectedState = remember { mutableStateOf(currentItem) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .preferredSize(56.dp)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow {
            items(
                items = categories,
                itemContent = { category ->
                    val isSelected = category == selectedState.value
                    CategoryItemChip(
                        category = category,
                        isSelected = isSelected,
                        selectedState,
                        onCategoryChanged = onCategoryChanged
                    )
                }
            )
        }
    }
}

@Composable
private fun CategoryItemChip(
    category: Category,
    isSelected: Boolean = false,
    selectedState: MutableState<Category?>,
    onCategoryChanged: (Long?) -> Unit
) {
    Surface(
        modifier = Modifier.padding(end = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) category.color else Color.White,
        border = if (isSelected) {
            BorderStroke(1.dp, Color.Transparent)
        } else {
            BorderStroke(1.dp, SolidColor(MaterialTheme.colors.onSecondary))
        }
    ) {
        Row(
            modifier = Modifier.toggleable(
                value = isSelected,
                onValueChange = {
                    val newCategory = if (selectedState.value == category) {
                        null
                    } else {
                        category
                    }

                    selectedState.value = newCategory
                    onCategoryChanged(newCategory?.id)
                }
            )
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                color = if (isSelected) Color.White else MaterialTheme.colors.onSecondary,
                text = category.name ?: ""
            )
        }
    }
}

@Suppress("UndocumentedPublicFunction")
@Preview
@Composable
fun TaskDetailPreview() {
    val task = Task(title = "Buy milk", description = "This is a amazing task!", dueDate = null)
    val category1 = Category(name = "Groceries", color = Color.Magenta)
    val category2 = Category(name = "Books", color = Color.Cyan)
    val category3 = Category(name = "Movies", color = Color.Red)

    val categories = listOf(category1, category2, category3)

    AlkaaTheme {
        TaskDetailContent(
            task = task,
            categories = categories,
            onTitleChanged = {},
            onDescriptionChanged = {},
            onCategoryChanged = {}
        )
    }
}
