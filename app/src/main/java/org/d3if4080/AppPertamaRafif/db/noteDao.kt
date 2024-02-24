package org.d3if4080.AppPertamaRafif.db

import androidx.room.*
import java.util.concurrent.Flow

@Dao

interface noteDao {


    @Insert
    suspend fun insert(note : noteEntity)

  /*  @Insert
    suspend fun insertTanggal(note : tanggal)

   */

    @Update
    suspend fun updateData (note : noteEntity)

    @Delete
    suspend fun deleteData (note : noteEntity)


    //@Query("SELECT * FROM catatan ORDER BY id DESC")
    @Query("SELECT * FROM catatan")
    suspend fun getNotes(): List<noteEntity>


    @Query("DELETE from catatan")
    fun clearData()

  //  @Query("DELETE FROM catatan")
   // suspend fun clearData()

     @Query("SELECT * FROM catatan Where id=:note_id")
    suspend fun getNote(note_id : Int): List<noteEntity>

    //@Query("SELECT * FROM catatan WHERE teks LIKE :searchQuery OR tanggalBikin LIKE :searchQuery")
 //fun searchDatabase(searchQuery: String): List<noteEntity>


}