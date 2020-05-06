package com.walt4771.jatabaseactivity

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LibDataDao{
    @Query("SELECT * FROM LibData")
    fun getAll(): LiveData<List<LibData>>

    @Insert
    fun insert(libData: LibData)

    @Update
    fun update(libData: LibData)

    @Delete
    fun deleteAll(libData: LibData)

}