package com.example.todolistapp.data


import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Required
import java.util.*

class TodoItem : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    @Required
    var text: String = ""
    var done: Boolean = false
}
