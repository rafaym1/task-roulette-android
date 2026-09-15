package com.taskroulette.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.taskroulette.app.model.TaskList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "task_roulette")

private val LISTS_KEY = stringPreferencesKey("lists_json")
private val ACTIVE_LIST_ID_KEY = stringPreferencesKey("active_list_id")

/** Persists the user's task lists locally on-device; there is no account or server, so this is the only copy. */
class TaskListRepository(private val context: Context) {

    val savedLists: Flow<List<TaskList>?> = context.dataStore.data.map { prefs ->
        prefs[LISTS_KEY]?.let { json -> Json.decodeFromString<List<TaskList>>(json) }
    }

    val savedActiveListId: Flow<String?> = context.dataStore.data.map { prefs -> prefs[ACTIVE_LIST_ID_KEY] }

    suspend fun saveLists(lists: List<TaskList>) {
        context.dataStore.edit { prefs -> prefs[LISTS_KEY] = Json.encodeToString(lists) }
    }

    suspend fun saveActiveListId(id: String?) {
        context.dataStore.edit { prefs ->
            if (id == null) prefs.remove(ACTIVE_LIST_ID_KEY) else prefs[ACTIVE_LIST_ID_KEY] = id
        }
    }
}
