package com.example.game.features

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector

enum class WeaponType(
    val displayName: String,
    val damage: Float,
    val fireRate: Float, // shots per second
    val maxAmmo: Int,
    val magazineSize: Int,
    val range: Float,
    val iconName: String
) {
    FISTS("Combat Fists", damage = 25f, fireRate = 2f, maxAmmo = 0, magazineSize = 0, range = 2.5f, iconName = "fists"),
    PISTOL("9mm Tactical Pistol", damage = 35f, fireRate = 3.5f, maxAmmo = 120, magazineSize = 15, range = 35f, iconName = "pistol"),
    SMG("Viper .45 SMG", damage = 24f, fireRate = 9f, maxAmmo = 240, magazineSize = 30, range = 28f, iconName = "smg"),
    SHOTGUN("Bulldog Heavy Shotgun", damage = 85f, fireRate = 1.2f, maxAmmo = 48, magazineSize = 8, range = 16f, iconName = "shotgun"),
    ASSAULT_RIFLE("Apex Carbon Carbine", damage = 42f, fireRate = 6.5f, maxAmmo = 180, magazineSize = 30, range = 55f, iconName = "rifle")
}

data class WeaponSlot(
    val type: WeaponType,
    var currentAmmoInMag: Int = type.magazineSize,
    var reserveAmmo: Int = type.maxAmmo
) {
    val name: String get() = type.displayName
    var ammoInMag: Int
        get() = currentAmmoInMag
        set(v) { currentAmmoInMag = v }
    var ammoReserve: Int
        get() = reserveAmmo
        set(v) { reserveAmmo = v }
}

class WeaponInventory {
    val weapons = mutableListOf(
        WeaponSlot(WeaponType.FISTS),
        WeaponSlot(WeaponType.PISTOL),
        WeaponSlot(WeaponType.SMG),
        WeaponSlot(WeaponType.SHOTGUN),
        WeaponSlot(WeaponType.ASSAULT_RIFLE)
    )

    var currentWeaponIndex: Int = 0

    val currentWeapon: WeaponSlot
        get() = weapons[currentWeaponIndex]

    fun nextWeapon() {
        currentWeaponIndex = (currentWeaponIndex + 1) % weapons.size
    }

    fun previousWeapon() {
        currentWeaponIndex = if (currentWeaponIndex - 1 < 0) weapons.size - 1 else currentWeaponIndex - 1
    }

    fun fire(): Boolean {
        val slot = currentWeapon
        if (slot.type == WeaponType.FISTS) {
            return true // Fists never run out
        }
        if (slot.currentAmmoInMag > 0) {
            slot.currentAmmoInMag--
            return true
        }
        // Auto reload if empty mag
        if (slot.reserveAmmo > 0) {
            reload()
        }
        return false
    }

    fun reload() {
        val slot = currentWeapon
        if (slot.type == WeaponType.FISTS) return
        val needed = slot.type.magazineSize - slot.currentAmmoInMag
        val toLoad = needed.coerceAtMost(slot.reserveAmmo)
        slot.currentAmmoInMag += toLoad
        slot.reserveAmmo -= toLoad
    }

    fun addAmmo(type: WeaponType, amount: Int) {
        val slot = weapons.find { it.type == type } ?: return
        slot.reserveAmmo = (slot.reserveAmmo + amount).coerceAtMost(type.maxAmmo)
    }
}
