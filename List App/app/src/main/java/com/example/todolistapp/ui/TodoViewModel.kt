package com.example.todolistapp.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.data.TodoItem
import com.example.todolistapp.data.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repo = TodoRepository()
    val todos: StateFlow<List<TodoItem>> =
        repo.getAllTodos().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun add(text: String) = viewModelScope.launch {
        if (text.isNotBlank()) repo.addTodo(text.trim())
    }

    fun toggle(item: TodoItem) = viewModelScope.launch {
        repo.toggleDone(item)
    }

    fun delete(item: TodoItem) = viewModelScope.launch {
        repo.deleteTodo(item)
    }
}
