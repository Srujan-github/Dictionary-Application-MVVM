package labs.creative.dictornarymvvm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import labs.creative.dictornarymvvm.data.local.dao.SavedWordDao
import labs.creative.dictornarymvvm.data.local.entity.SavedWordEntity

@Database(
    entities = [SavedWordEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedWordDao(): SavedWordDao

    companion object {
        const val DATABASE_NAME = "lexicon_db"
    }
}
