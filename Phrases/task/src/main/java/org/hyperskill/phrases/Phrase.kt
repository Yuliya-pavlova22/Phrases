package org.hyperskill.phrases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class Phrase(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "phrase")
    var phrase: String,
)
