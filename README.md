TESTMESSAGE

To Do List app
==================================



問題一
------------

*clone下來的存貨APP無法開啟* 

## 解決方法

更改虛擬機的版本以及系統



## 問題二

*變數 函式名 檔案名在重構的過程中有許多問題 或是在修改後不能使用*

## 解決方法

用Git進行版本控制 在每個重大改變前先commit



## 問題三

*修改 Enity後程式運行會崩潰* 

## 解決方法

查看log cat 找到崩潰原因

```kotlin
android.database.sqlite.SQLiteException: […]
no such column: priority (code 1 SQLITE_ERROR):
 , while compiling: SELECT * FROM tasks ORDER BY name ASC
```

詢問ChatGPT後是Room的語法問題

```kotlin
@Database(entities = [Task::class], version = 2, exportSchema = false)

//需要將version改成2
abstract class AppDatabase : RoomDatabase() {
 // …
 companion object {
 fun create(context: Context): AppDatabase =
 Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
 .fallbackToDestructiveMigration() // 直接砍掉舊 DB，重建新結構
 .build()
 }
}
```

## 問題四

*無法使用swipe元件*

## 解決方法

```kotlin
 implementation("androidx.compose.material:material:1.8.1")
```

修改gradle檔 導入相依元件









   


--------------
