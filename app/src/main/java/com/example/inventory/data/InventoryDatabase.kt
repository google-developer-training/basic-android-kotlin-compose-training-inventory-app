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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database Room berfungsi untuk merepresentasikan lokal di aplikasi.
 * Pada Inventory Database disediakan akses ke database dengan menggunakan DAO.
 * Melalui database hanya akan ada satu instance dari database yang digunakan
 * untuk menghemat resources.
 */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {
    /**
     * Fungsi abstrak ini, mengembalikan instance dari ItemDao.
     * DAO ini digunakan untuk mengakses dan mengelola data `Item` di dalam database.
     * Semua operasi database, seperti menambah, memperbarui, menghapus, atau mengambil data `Item`,
     * dilakukan melalui ItemDao.
     */
    abstract fun itemDao(): ItemDao

    companion object {
        // Variabel `Instance` bersifat volatile untuk memastikan sinkronisasi antar-thread.
        @Volatile
        private var Instance: InventoryDatabase? = null

        /**
         * Ini adalah fungsi getDatabase menyediakan instance InventoryDatabase yang bisa digunakan oleh komponen
         * aplikasi lain. Jika `Instance` sudah ada, maka fungsi akan mengembalikan instance tersebut.
         * Jika belum, fungsi akan membuat instance baru menggunakan `Room.databaseBuilder`.
         * context, digunakan untuk membangun atau mengakses database.
         * @return Instance dari InventoryDatabase yang akan digunakan oleh seluruh aplikasi.
         */
        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    /**
                     * Metode fallbackToDestructiveMigration digunakan saat migrasi belum didefinisikan.
                     * Room akan menghapus seluruh data yang ada agar dapat menginisialisasi ulang database
                     * dengan struktur terbaru, menghindari kesalahan migrasi.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
