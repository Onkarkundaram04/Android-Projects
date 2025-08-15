package com.example.todolistapp.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolistapp.data.TodoItem

// We need this annotation for Scaffold and TopAppBar
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoScreen(vm: TodoViewModel, modifier: Modifier = Modifier) {
    // State for the text field
    var newText by rememberSaveable { mutableStateOf("") }
    // State from the ViewModel
    val todos by vm.todos.collectAsState()

    Scaffold(
        // The TopAppBar provides a consistent header
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Todo App",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // The main content area
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    // Apply padding from the TopAppBar
                    .padding(innerPadding)
                    // Apply padding for the navigation bar at the bottom
                    .navigationBarsPadding()
                    // Apply padding for the keyboard when it appears
                    .imePadding()
            ) {
                // Check if the list is empty to show a placeholder
                if (todos.isEmpty()) {
                    EmptyState(modifier = Modifier.weight(1f))
                } else {
                    // LazyColumn is efficient for long lists
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f) // Takes up all available space
                            .padding(horizontal = 16.dp)
                    ) {
                        // Spacer for top of the list
                        item { Spacer(modifier = Modifier.size(16.dp)) }

                        items(
                            items = todos,
                            key = { it.id } // Use a stable key for better performance
                        ) { task ->
                            TaskItem(
                                item = task,
                                onToggle = { vm.toggle(task) },
                                onDelete = { vm.delete(task) },
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = tween(durationMillis = 300)
                                )
                            )
                        }

                        // Spacer for bottom of the list
                        item { Spacer(modifier = Modifier.size(16.dp)) }
                    }
                }

                // Input bar at the bottom, above the navigation
                TaskInputBar(
                    newText = newText,
                    onTextChange = { newText = it },
                    onAdd = {
                        vm.add(newText)
                        newText = "" // Clear text after adding
                    }
                )
            }
        }
    )
}

@Composable
private fun TaskItem(
    item: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for toggling the 'done' state
            Checkbox(
                checked = item.done,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Task text
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    // Apply strikethrough if the task is done
                    textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.done) Color.Gray else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TaskInputBar(
    newText: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text field for new task input
            OutlinedTextField(
                value = newText,
                onValueChange = onTextChange,
                placeholder = { Text("Add a new task...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Add button, disabled if text is empty
            IconButton(
                onClick = onAdd,
                enabled = newText.isNotBlank(), // Button is disabled if text is empty
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (newText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Add Task",
                    tint = if (newText.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    // Centered message for when the list is empty
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "No Tasks",
                modifier = Modifier.size(80.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Text(
                text = "All tasks completed!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )
        }
    }
}