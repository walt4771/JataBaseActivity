package com.walt4771.a080callcheckin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Insert(onConflict = REPLACE)
    fun insertHistory(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()
}

// DAO는 Data Access Object의 약자이다.
//
//DAO를 통해서 쿼리문을 사용해 데이터베이스의 데이터에 접근할 수 있다.

// DAO로 정의하기 위해선 @Dao라는 어노테이션이 필요하다.
//
//그리고 DAO는 인터페이스로 작성된다.
//
//
//
//@Query를 통해 직접 SQL을 작성할 수 있다.
//
//
//
//그 외에도 @Insert @Delete @Update 등으로 간편하게 구현할 수 있는 기능을 제공한다.
//
//활용 방법이 많으니 안드로이드 개발자 문서를 찾아보면 좋을 것 같다.