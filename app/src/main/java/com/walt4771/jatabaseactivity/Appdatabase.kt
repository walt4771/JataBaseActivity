package com.walt4771.jatabaseactivity

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LibData::class], version = 1)

abstract class Appdatabase : RoomDatabase() {
    abstract fun libDataDao(): LibDataDao
}