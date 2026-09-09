package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

enum class ConsumableType(
    val id: String,
    val displayName: String,
    val description: String,
    val cost: Int,
    val iconName: String
) {
    DAN_COLA("item_cola", "DanCola Energy", "Instant +40 Stamina boost & sprint recovery", 25, "cola"),
    FIRST_AID("item_medkit", "Emergency Medkit", "Restores +60 Player Health immediately", 150, "medkit"),
    ARMOR_PLATE("item_armor", "Ceramic Armor Plate", "Repairs +100 Body Armor protection", 250, "armor"),
    ADRENALINE("item_adrenaline", "Adrenaline Syringe", "Instantly fills Bullet-Time Focus meter", 400, "syringe")
}

data class InventoryItem(
    val type: ConsumableType,
    var quantity: Int
)

class InventorySystem {
    val items = mutableListOf(
        InventoryItem(ConsumableType.DAN_COLA, 2),
        InventoryItem(ConsumableType.FIRST_AID, 1),
        InventoryItem(ConsumableType.ARMOR_PLATE, 1),
        InventoryItem(ConsumableType.ADRENALINE, 1)
    )

    fun getQuantity(type: ConsumableType): Int {
        return items.find { it.type == type }?.quantity ?: 0
    }

    fun getCount(type: ConsumableType): Int = getQuantity(type)

    fun getCount(id: String): Int = items.find { it.type.id == id }?.quantity ?: 0

    fun addItem(type: ConsumableType, count: Int = 1) {
        val existing = items.find { it.type == type }
        if (existing != null) {
            existing.quantity += count
        } else {
            items.add(InventoryItem(type, count))
        }
    }

    fun useItem(type: ConsumableType): Boolean {
        val existing = items.find { it.type == type }
        if (existing != null && existing.quantity > 0) {
            existing.quantity--
            return true
        }
        return false
    }
}
