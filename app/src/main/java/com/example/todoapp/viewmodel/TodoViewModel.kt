package com.example.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.Todo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(title: String) {
        val nextId = (_todos.value.maxOfOrNull { it.id } ?: 0) + 1
        _todos.value = _todos.value + Todo(nextId, title)
    }

    fun toggleTask(id: Int) {
        _todos.value = _todos.value.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
    }

    fun deleteTask(id: Int) {
        _todos.value = _todos.value.filterNot { it.id == id }
    }

    val visibleTodos: StateFlow<List<Todo>> =
        combine(_todos, _searchQuery) { todos, query ->
            if (query.isBlank()) todos
            else todos.filter { it.title.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}