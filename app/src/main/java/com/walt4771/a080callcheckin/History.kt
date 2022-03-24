package com.walt4771.a080callcheckin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["name"])
// 객체, 흔히 데이터베이스에서 개념 스키마를 뜻하며 쉽게 테이블이라고 생각하면 될 것 같다
data class History(
    val name: String,
    var contact: String?,
    var location_x: Double,
    var location_y: Double
)

// data class를 정의하고, 그 위에 @Entity라는 어노테이션이 필요하다.
// Entity에는 하나 이상의 기본키를 설정해야 한다.
//@PrimaryKey로 선언된 변수가 기본키이다.
//기본키를 자동으로 할당하게 하려면 autoGenerate속성을 설정하면 되는데 자세한 사용법은 직접 찾아보길 바란다.
// 기본키는 복합키로 이루어질 수 있다. 이럴 경우 어노테이션에서 설정해준다.
//
//-> @Entity(primaryKeys = arrayOf("firstName", "lastName"))
//
//
//
//테이블 이름은 기본적으로 클래스 이름(예제에서는 History)이 된다.
//
//만약 별도로 테이블 이름을 설정해주고 싶다면 어노테이션에서 설정 가능하다.
//
//-> Entity(tableName = "users")
//
//
//
//열로 사용할 변수 설정은 @CcolumnInfo로 가능하다. 이때 기본적으로 변수 이름이 열 이름이 된다.
//
//만약 별도로 열 이름을 설정하고 싶다면 name 속성을 주면 된다.
//
//-> @ColumnInfo(name = "first_name") val firstName: String? (예제에서 result)