package com.walt4771.a080callcheckin

import androidx.room.Database
import androidx.room.RoomDatabase
import com.walt4771.a080callcheckin.History
import com.walt4771.a080callcheckin.HistoryDao

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao() : HistoryDao
}

// Entity와 DAO가 작성되었으니 마지막으로 Database를 작성한다.
//
//Room에서 데이터베이스를 정의하기 위해서는 @Database를 사용하며 클래스는 추상 클래스로 작성되어야 한다.
//
//그리고 추상 클래스는 RoomDatabase()를 상속해야 하며
//
//매개 변수가 없는 추상 메서드를 포함해야 한다.
//
//반환 값은 DAO

// 어노테이션에는 데이터베이스와 연결된 항목의 목록과 버전을 포함해야 한다.
//
//만약 entity가 여러 개일 경우 arrayOf를 사용해 여러 Entity를 묶는다.

