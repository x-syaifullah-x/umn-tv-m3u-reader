package id.xxx.example.data.repository

import android.content.Context
import id.xxx.example.data.source.local.M3uDB
import id.xxx.example.data.source.local.M3uDao
import id.xxx.example.data.source.local.M3uEntity

class M3uRepository private constructor(private val dao: M3uDao) {

    companion object {

        @Volatile
        private var INSTANCE: M3uRepository? = null

        fun getInstance(context: Context?) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: M3uRepository(
                M3uDB.getInstance(context).m3uDao()
            ).also { INSTANCE = it }
        }
    }

    suspend fun getDataBy(groupTitle: String?): List<M3uEntity> {
        return dao.queryByGroupTitle(groupTitle)
    }

    suspend fun save(items: List<M3uEntity>): List<Long> {
        return dao.insert(items)
    }

    suspend fun clear() {
        dao.nukeTable()
        dao.resetPrimaryKey()
    }
}