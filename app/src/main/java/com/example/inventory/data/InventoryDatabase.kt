package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.MasterKey
import com.commonsware.cwac.saferoom.SQLCipherUtils
import net.sqlcipher.database.SupportFactory

@Database(entities = [Item::class], version = 3, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            val key = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val databaseState = SQLCipherUtils.getDatabaseState(context, "item_database")
            if (databaseState == SQLCipherUtils.State.UNENCRYPTED) {
                SQLCipherUtils.encrypt(context, "item_database", key.toString().toByteArray())
            }

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .openHelperFactory(SupportFactory(key.toString().toByteArray()))
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}