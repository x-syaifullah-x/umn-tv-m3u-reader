package id.xxx.example.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface M3uDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: List<M3uEntity>): List<Long>

    @Query("SELECT * FROM ${M3uEntity.TABLE} WHERE ${M3uEntity.GROUP_TITLE}=:groupTitle")
    suspend fun queryByGroupTitle(groupTitle: String?): List<M3uEntity>

    /**
     * Returns count table
     */
    @Query("DELETE FROM ${M3uEntity.TABLE}")
    suspend fun nukeTable(): Int

    /**
     * Returns 0 or 1
     * 0 if primary key not reset
     * 1 if primary key reset
     */
    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = name")
    suspend fun resetPrimaryKey(): Int
}