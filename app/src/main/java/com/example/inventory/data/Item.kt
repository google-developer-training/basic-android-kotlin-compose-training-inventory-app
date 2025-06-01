/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
//    val date: LocalDateTime,
//    val article: Int

    //    val id: Int = 0,
    //    val name: String,
    //    val price: Double,
    //    val quantity: Int
)
