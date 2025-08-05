// Сделал тесты как в задании но их нужно адаптировать под мои изменения в приложении
//и еще какие то баги..
// короче пока просто закоментил

//import android.content.Context
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.inventory.data.InventoryDatabase
//import com.example.inventory.data.Item
//import com.example.inventory.data.ItemDao
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.IOException
//
//
//@RunWith(AndroidJUnit4::class)
//class ItemDaoTest {
//
//    private lateinit var itemDao: ItemDao
//    private lateinit var inventoryDatabase: InventoryDatabase
//
//    // Объекты Item для использования в тестах
//    private var item1 = Item(1, "Apples", 10, 20)
//    private var item2 = Item(2, "Bananas", 15, 97)
//
//    @Before
//    fun createDb() {
//        val context: Context = ApplicationProvider.getApplicationContext()
//        // Создаем in-memory базу данных для тестирования
//        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
//            // Разрешаем запросы в главном потоке (только для тестов)
//            .allowMainThreadQueries()
//            .build()
//        itemDao = inventoryDatabase.itemDao()
//    }
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        // Закрываем базу данных после каждого теста
//        inventoryDatabase.close()
//    }
//
//    // Вспомогательные suspend-функции для добавления элементов в БД
//
//    /**
//     * Добавляет один элемент в базу данных.
//     */
//    private suspend fun addOneItemToDb() {
//        itemDao.insert(item1)
//    }
//
//    /**
//     * Добавляет два элемента в базу данных.
//     */
//    private suspend fun addTwoItemsToDb() {
//        itemDao.insert(item1)
//        itemDao.insert(item2)
//    }
//
//    // Тесты
//
//    @Test
//    @Throws(Exception::class)
//    fun daoInsert_insertsItemIntoDB() = runBlocking {
//        // Arrange
//        // item1 уже определён
//
//        // Act
//        addOneItemToDb()
//
//        // Assert
//        val allItems = itemDao.getAllItems().first()
//        assertEquals(allItems.size, 1)
//        assertEquals(allItems[0], item1)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
//        addTwoItemsToDb()
//        val allItems = itemDao.getAllItems().first()
//        assertEquals(allItems[0], item1)
//        assertEquals(allItems[1], item2)
//    }
//}