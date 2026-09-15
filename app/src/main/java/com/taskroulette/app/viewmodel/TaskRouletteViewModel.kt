package com.taskroulette.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taskroulette.app.data.TaskListRepository
import com.taskroulette.app.model.TaskItem
import com.taskroulette.app.model.TaskList
import com.taskroulette.app.util.FeedbackHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/** Config the web prototype exposed as design-tool props; fixed here since the app has no settings UI. */
private const val APP_NAME = "TASK ROULETTE"
private const val EXTRA_SPINS = 6
private const val SOUND_ENABLED = true
private const val SPIN_DURATION_MS = 3800L

private val SPIN_TICK_OFFSETS_MS = listOf(0L, 120L, 250L, 400L, 580L, 780L, 1000L, 1260L, 1560L, 1900L, 2300L, 2760L, 3280L, 3800L)

data class TaskRouletteUiState(
    val lists: List<TaskList> = listOf(
        TaskList(
            id = "l1",
            name = "Morning Chores",
            tasks = listOf(
                TaskItem("t1", "Do the laundry"),
                TaskItem("t2", "Wash the dishes"),
                TaskItem("t3", "Walk the dog"),
                TaskItem("t4", "Reply to emails"),
                TaskItem("t5", "Tidy the desk"),
            )
        )
    ),
    val activeListId: String? = "l1",
    val modalOpen: Boolean = false,
    val expandedListId: String? = "l1",
    val addingList: Boolean = false,
    val newListName: String = "",
    val taskDraft: String = "",
    val editingTaskId: String? = null,
    val editingText: String = "",
    val rotation: Float = 0f,
    val spinning: Boolean = false,
    val result: TaskItem? = null,
    val showResult: Boolean = false,
    val accountOpen: Boolean = false,
    val howItWorksOpen: Boolean = false,
    val privacyPolicyOpen: Boolean = false,
    val deleteConfirmOpen: Boolean = false,
) {
    val activeList: TaskList? get() = lists.find { it.id == activeListId } ?: lists.firstOrNull()
    val activePool: List<TaskItem> get() = activeList?.tasks?.filter { !it.done } ?: emptyList()
}

class TaskRouletteViewModel(application: Application) : AndroidViewModel(application) {

    private val feedback = FeedbackHelper(application)
    private val repository = TaskListRepository(application)

    private val _state = MutableStateFlow(TaskRouletteUiState())
    val state: StateFlow<TaskRouletteUiState> = _state.asStateFlow()

    val appName: String get() = APP_NAME

    private var idCounter = 100
    private fun genId(prefix: String): String {
        idCounter += 1
        return "$prefix$idCounter"
    }

    private var spinJob: Job? = null

    init {
        viewModelScope.launch {
            val savedLists = repository.savedLists.first()
            if (savedLists != null) {
                val savedActiveId = repository.savedActiveListId.first()
                val usedIds = savedLists.asSequence().flatMap { l -> sequenceOf(l.id) + l.tasks.asSequence().map { it.id } }
                val highest = usedIds.mapNotNull { Regex("(\\d+)$").find(it)?.value?.toIntOrNull() }.maxOrNull() ?: idCounter
                idCounter = maxOf(idCounter, highest)
                _state.update {
                    it.copy(
                        lists = savedLists,
                        activeListId = savedActiveId ?: savedLists.firstOrNull()?.id,
                        expandedListId = savedActiveId ?: savedLists.firstOrNull()?.id,
                    )
                }
            }

            launch {
                state.map { it.lists }.distinctUntilChanged().collect { repository.saveLists(it) }
            }
            launch {
                state.map { it.activeListId }.distinctUntilChanged().collect { repository.saveActiveListId(it) }
            }
        }
    }

    // ---- modal / sheet visibility ----

    fun openModal() = _state.update { it.copy(modalOpen = true) }
    fun closeModal() = _state.update { it.copy(modalOpen = false, addingList = false, editingTaskId = null) }
    fun startAddListQuick() = _state.update { it.copy(modalOpen = true, addingList = true, newListName = "") }

    fun openAccount() = _state.update { it.copy(accountOpen = true) }
    fun closeAccount() = _state.update { it.copy(accountOpen = false) }
    fun openHowItWorks() = _state.update { it.copy(accountOpen = false, howItWorksOpen = true) }
    fun closeHowItWorks() = _state.update { it.copy(howItWorksOpen = false) }
    fun openPrivacyPolicy() = _state.update { it.copy(accountOpen = false, privacyPolicyOpen = true) }
    fun closePrivacyPolicy() = _state.update { it.copy(privacyPolicyOpen = false) }
    fun confirmDeleteAccount() = _state.update { it.copy(accountOpen = false, deleteConfirmOpen = true) }
    fun closeDeleteConfirm() = _state.update { it.copy(deleteConfirmOpen = false) }

    fun deleteAccount() {
        _state.update {
            it.copy(
                lists = emptyList(),
                activeListId = null,
                deleteConfirmOpen = false,
                expandedListId = null,
                result = null,
                showResult = false,
            )
        }
    }

    // ---- list management ----

    fun selectList(id: String) = _state.update { it.copy(activeListId = id, modalOpen = false) }

    fun toggleExpand(id: String) = _state.update {
        it.copy(expandedListId = if (it.expandedListId == id) null else id, taskDraft = "")
    }

    fun startAddList() = _state.update { it.copy(addingList = true, newListName = "") }
    fun onNewListNameChange(value: String) = _state.update { it.copy(newListName = value) }

    fun confirmAddList() {
        val name = _state.value.newListName.trim()
        if (name.isEmpty()) {
            _state.update { it.copy(addingList = false) }
            return
        }
        val id = genId("l")
        _state.update {
            it.copy(
                lists = it.lists + TaskList(id = id, name = name),
                addingList = false,
                newListName = "",
                expandedListId = id,
            )
        }
    }

    fun removeList(id: String) {
        _state.update { s ->
            val lists = s.lists.filterNot { it.id == id }
            val activeListId = if (s.activeListId == id) lists.firstOrNull()?.id else s.activeListId
            s.copy(lists = lists, activeListId = activeListId)
        }
    }

    // ---- task management ----

    fun onTaskDraftChange(value: String) = _state.update { it.copy(taskDraft = value) }

    fun addTask(listId: String) {
        val text = _state.value.taskDraft.trim()
        if (text.isEmpty()) return
        val id = genId("t")
        _state.update { s ->
            s.copy(
                lists = s.lists.map { l -> if (l.id == listId) l.copy(tasks = l.tasks + TaskItem(id, text)) else l },
                taskDraft = "",
            )
        }
    }

    fun removeTask(listId: String, taskId: String) {
        _state.update { s ->
            s.copy(lists = s.lists.map { l -> if (l.id == listId) l.copy(tasks = l.tasks.filterNot { it.id == taskId }) else l })
        }
    }

    fun toggleTaskDone(listId: String, taskId: String) {
        _state.update { s ->
            s.copy(lists = s.lists.map { l ->
                if (l.id == listId) l.copy(tasks = l.tasks.map { t -> if (t.id == taskId) t.copy(done = !t.done) else t })
                else l
            })
        }
    }

    fun startEditTask(taskId: String, text: String) = _state.update { it.copy(editingTaskId = taskId, editingText = text) }
    fun onEditingTextChange(value: String) = _state.update { it.copy(editingText = value) }

    fun saveEdit(listId: String) {
        val taskId = _state.value.editingTaskId ?: return
        val text = _state.value.editingText.trim()
        _state.update { s ->
            s.copy(
                lists = s.lists.map { l ->
                    if (l.id == listId) l.copy(tasks = l.tasks.map { t ->
                        if (t.id == taskId) t.copy(text = text.ifEmpty { t.text }) else t
                    })
                    else l
                },
                editingTaskId = null,
                editingText = "",
            )
        }
    }

    // ---- spin ----

    fun spin() {
        val s = _state.value
        if (s.spinning) return
        val activeList = s.activeList ?: return
        val pool = activeList.tasks.filter { !it.done }
        val n = pool.size
        if (n == 0) return
        val idx = Random.nextInt(n)
        val chosen = pool[idx]
        val sliceAngle = 360f / n
        val sliceCenter = (idx + 0.5f) * sliceAngle
        val current = s.rotation % 360f
        var delta = (360f - sliceCenter - current) % 360f
        if (delta < 0f) delta += 360f
        val newRotation = s.rotation + EXTRA_SPINS * 360f + delta

        _state.update { it.copy(spinning = true, rotation = newRotation, result = null, showResult = false) }
        playSpinFeedback()

        spinJob?.cancel()
        spinJob = viewModelScope.launch {
            delay(SPIN_DURATION_MS)
            _state.update { it.copy(spinning = false, result = chosen, showResult = true) }
            feedback.reveal(SOUND_ENABLED)
        }
    }

    fun respin() {
        _state.update { it.copy(showResult = false) }
        viewModelScope.launch {
            delay(250)
            spin()
        }
    }

    fun closeResult() = _state.update { it.copy(showResult = false) }

    fun markResultDone() {
        val listId = _state.value.activeListId ?: return
        val taskId = _state.value.result?.id ?: return
        _state.update { s ->
            s.copy(
                lists = s.lists.map { l ->
                    if (l.id == listId) l.copy(tasks = l.tasks.map { t -> if (t.id == taskId) t.copy(done = true) else t })
                    else l
                },
                showResult = false,
            )
        }
    }

    private fun playSpinFeedback() {
        viewModelScope.launch {
            var previous = 0L
            for (offset in SPIN_TICK_OFFSETS_MS) {
                delay(offset - previous)
                previous = offset
                feedback.tick(SOUND_ENABLED)
            }
        }
    }

    override fun onCleared() {
        spinJob?.cancel()
        feedback.release()
        super.onCleared()
    }
}
