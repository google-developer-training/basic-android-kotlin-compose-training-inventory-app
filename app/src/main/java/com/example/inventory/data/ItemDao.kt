package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemDao {

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    // При вставке элементов в базу данных могут возникать конфликты.
    // Например, в нескольких местах кода выполняется попытка обновить сущность с разными конфликтующими значениями,
    // такими как один и тот же первичный ключ. Сущность — это строка в базе данных.
    // В приложении «Инвентаризация» мы вставляем сущность только из одного места — с экрана «Добавить элемент»,
    // поэтому мы не ожидаем никаких конфликтов и можем установить стратегию разрешения конфликтов «Игнорировать».
    //
    //Добавьте аргумент onConflict и присвойте ему значение OnConflictStrategy.IGNORE.
    //Аргумент onConflict сообщает Комнате, что делать в случае конфликта. Стратегия OnConflictStrategy.IGNORE игнорирует новый элемент.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)


    // Рекомендуется использовать Flow на уровне сохранения данных.
    // При использовании Flow в качестве типа возвращаемых данных вы будете получать уведомления при каждом изменении данных в базе данных.
    // Room обновляет Flow за вас, а это значит, что вам нужно будет явно получить данные только один раз.
    // Такая настройка полезна для обновления списка инвентаря, который вы реализуете в следующей лабораторной работе.
    // Благодаря Flow типу возвращаемых данных Room также выполняет запрос в фоновом потоке.
    // Вам не нужно явно делать его suspend функцией и вызывать внутри области сопрограммы.
    // Примечание: Flow в базе данных Room можно поддерживать актуальность данных, отправляя уведомления при каждом изменении данных в базе.
    // Это позволяет отслеживать данные и соответствующим образом обновлять пользовательский интерфейс.
    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<Item?>

    // Пусть запрос SQLite возвращает все столбцы из таблицы item в порядке возрастания.
    // Пусть getAllItems() возвращает список Item сущностей в виде Flow.
    // Room обновляет этот Flow список для вас, а это значит, что вам нужно получить данные только один раз.
    @Query("""
        SELECT * FROM items 
        ORDER BY 
            CASE WHEN amount >= 0 THEN 0 ELSE 1 END,
            ABS(amount) DESC
    """)
    fun getAllItems(): Flow<List<Item>>
}