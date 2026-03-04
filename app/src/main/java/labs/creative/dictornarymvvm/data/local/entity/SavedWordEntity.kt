package labs.creative.dictornarymvvm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_words")
data class SavedWordEntity(
    @PrimaryKey val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val shortDefinition: String,
    val savedAt: Long = System.currentTimeMillis(),
)
