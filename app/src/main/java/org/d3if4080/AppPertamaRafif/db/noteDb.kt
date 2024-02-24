package org.d3if4080.AppPertamaRafif.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
        entities = [noteEntity::class],
        version = 1

)


abstract class noteDb : RoomDatabase() {
    abstract fun noteDao() : noteDao
    companion object {

        @Volatile private var instance : noteDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            noteDb::class.java,
            "note.db"
        ).build()

    }
}