package com.example.todolistapp.data


import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepository {

    private val realm: Realm

    init {
        val config = RealmConfiguration.Builder(schema = setOf(TodoItem::class))
            .name("todos.realm")
            .build()
        realm = Realm.open(config)
    }

    fun getAllTodos(): Flow<List<TodoItem>> =
        realm.query<TodoItem>().asFlow().map { it.list }

    suspend fun addTodo(text: String) {
        realm.write {
            copyToRealm(TodoItem().apply { this.text = text })
        }
    }

    suspend fun toggleDone(item: TodoItem) {
        realm.write {
            findLatest(item)?.done = !(item.done)
        }
    }

    suspend fun deleteTodo(item: TodoItem) {
        realm.write {
            findLatest(item)?.let { delete(it) }
        }
    }
}
