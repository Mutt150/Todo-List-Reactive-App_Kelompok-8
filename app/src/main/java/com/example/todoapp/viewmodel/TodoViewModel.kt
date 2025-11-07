package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    enum class FilterType(val label: String) {
        SEMUA("Semua"),
        AKTIF("Aktif"),
        SELESAI("Selesai")
    }

    private val _filter = MutableStateFlow(FilterType.SEMUA)
    val filter: StateFlow<FilterType> = _filter

    fun setFilter(type: FilterType) {
        _filter.value = type
    }

    val filteredTodos: StateFlow<List<Todo>> = combine(_todos, _filter) { todos, filter ->
        when (filter) {
            FilterType.SEMUA -> todos
            FilterType.AKTIF -> todos.filter { !it.isDone }
            FilterType.SELESAI -> todos.filter { it.isDone }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addTask(title: String) {
        viewModelScope.launch {
            val nextId = (_todos.value.maxOfOrNull { it.id } ?: 0) + 1
            val newTask = Todo(id = nextId, title = title)
            _todos.value = _todos.value + newTask
        }
    }

    fun toggleTask(id: Int) {
        viewModelScope.launch {
            _todos.value = _todos.value.map { t ->
                if (t.id == id) t.copy(isDone = !t.isDone) else t
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            _todos.value = _todos.value.filterNot { it.id == id }
        }
    }
}
