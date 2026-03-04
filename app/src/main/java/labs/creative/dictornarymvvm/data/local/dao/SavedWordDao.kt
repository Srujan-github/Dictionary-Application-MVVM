package labs.creative.dictornarymvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import labs.creative.dictornarymvvm.data.local.entity.SavedWordEntity

@Dao
interface SavedWordDao {

    @Query("SELECT * FROM saved_words ORDER BY savedAt DESC")
    fun getAllSavedWords(): Flow<List<SavedWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWord(word: SavedWordEntity)

    @Delete
    suspend fun deleteWord(word: SavedWordEntity)

    @Query("DELETE FROM saved_words WHERE word = :word")
    suspend fun deleteWordByName(word: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_words WHERE word = :word)")
    suspend fun isWordSaved(word: String): Boolean
}
