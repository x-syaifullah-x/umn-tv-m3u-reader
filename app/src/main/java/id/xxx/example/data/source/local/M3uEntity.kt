package id.xxx.example.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = M3uEntity.TABLE,
)
data class M3uEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PRIMARY_KEY)
    val id: Long? = null,
    val tvgId: String?,
    val tvgName: String?,
    val tvgLogo: String?,
    @ColumnInfo(name = M3uEntity.GROUP_TITLE)
    val groupTitle: String?,
    val channelName: String?,
    val url: String?
) : Serializable {
    companion object {
        const val TABLE = "m3u"
        const val PRIMARY_KEY = "primary_key"
        const val GROUP_TITLE = "group_title"
    }
}