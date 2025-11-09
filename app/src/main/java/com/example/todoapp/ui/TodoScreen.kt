package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoViewModel
import com.example.todoapp.model.Todo

@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val todos by vm.visibleTodos.collectAsState()
    var text by rememberSaveable { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        // 🔍 Kolom pencarian real-time
        OutlinedTextField(
            value = vm.searchQuery.collectAsState().value,
            onValueChange = { vm.setSearchQuery(it) },
            label = { Text("Cari tugas...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        // 📝 Input tugas baru
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Tambah")
        }

        Divider()

        // 📋 Daftar tugas dinamis
        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}
