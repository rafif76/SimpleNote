   package org.d3if4080.AppPertamaRafif.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.text.DateFormat
import java.util.*


@Entity(tableName = "catatan")

        data class noteEntity(

        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val teks: String,
        val date: String,
        val tanggalBikin: String
                //var tanggal: Long = System.currentTimeMillis(),
        )



/*@Entity(tableName = "tanggal")
       data class tanggal(

        @PrimaryKey(autoGenerate = true)
        val no : Int =0,
        val tanggalBuat: String

        )
 */
