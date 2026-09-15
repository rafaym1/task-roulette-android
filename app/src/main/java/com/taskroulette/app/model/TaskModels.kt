package com.taskroulette.app.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskItem(
    val id: String,
    val text: String,
    val done: Boolean = false,
)

@Serializable
data class TaskList(
    val id: String,
    val name: String,
    val tasks: List<TaskItem> = emptyList(),
)
