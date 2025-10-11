
package com.example.inventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime// но Qwen рекомендует импортировать именно это


/**
 * Entity data class represents a single row in the database.
 * Класс данных Entity представляет собой одну строку в базе данных.
 *
 * Примечание: напоминаем, что основной конструктор является частью заголовка класса в Kotlin.
 * Он располагается после имени класса (и необязательных параметров типа).
 * т.е внутри скобок (), а не {}
 */

@Entity(tableName = "items")
class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Int,
    val description: String,

)
