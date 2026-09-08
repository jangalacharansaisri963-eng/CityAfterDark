package com.example.game.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_saves")
data class GameSaveEntity(
    @PrimaryKey val id: Int = 1,
    val playerX: Float = 82f,
    val playerY: Float = 0f,
    val playerZ: Float = 42f,
    val playerHeading: Float = 180f,
    val playerHealth: Float = 100f,
    val playerMoney: Int = 1250,
    val currentMissionIndex: Int = 0,
    val completedMissionIdsString: String = "",
    val equippedOutfitId: String = "outfit_stealth",
    val timeOfDay: Float = 20.5f,
    val weatherName: String = "CLEAR",
    val graphicsPreset: String = "MEDIUM",
    val viewDistance: Float = 240f,
    val trafficDensity: Int = 14,
    val audioVolume: Float = 0.8f,
    val controlsSensitivity: Float = 1.0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface GameSaveDao {
    @Query("SELECT * FROM game_saves WHERE id = 1 LIMIT 1")
    fun getSave(): Flow<GameSaveEntity?>

    @Query("SELECT * FROM game_saves WHERE id = 1 LIMIT 1")
    suspend fun getSaveDirect(): GameSaveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(save: GameSaveEntity)
}

@Database(entities = [GameSaveEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun saveDao(): GameSaveDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "city_after_dark.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class SaveRepository(private val dao: GameSaveDao) {
    val saveFlow: Flow<GameSaveEntity?> = dao.getSave()

    suspend fun save(entity: GameSaveEntity) = dao.saveGame(entity)

    suspend fun load(): GameSaveEntity? = dao.getSaveDirect()
}
