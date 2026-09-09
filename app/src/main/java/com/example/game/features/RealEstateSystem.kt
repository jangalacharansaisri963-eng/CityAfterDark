package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.example.engine.math.Vector3
import com.example.game.world.District

data class RealEstateProperty(
    val id: String,
    val name: String,
    val district: District,
    val price: Int,
    val dailyRevenue: Int,
    val location: Vector3,
    val description: String
)

class RealEstateSystem {
    val properties = listOf(
        RealEstateProperty(
            id = "prop_safehouse",
            name = "Vance's Starter Safehouse",
            district = District.DOWNTOWN,
            price = 0, // Already owned
            dailyRevenue = 150,
            location = Vector3(82f, 0f, 42f),
            description = "Quiet loft with weapon storage and secure garage bay."
        ),
        RealEstateProperty(
            id = "prop_chop_shop",
            name = "Ironworks Chop Shop",
            district = District.INDUSTRIAL,
            price = 6500,
            dailyRevenue = 450,
            location = Vector3(-160f, 0f, 220f),
            description = "Automotive modification hub generating steady illicit scrap profits."
        ),
        RealEstateProperty(
            id = "prop_chinatown_lounge",
            name = "Golden Dragon Tea Lounge",
            district = District.OLD_TOWN,
            price = 12000,
            dailyRevenue = 850,
            location = Vector3(140f, 0f, -90f),
            description = "High-class cultural lounge frequented by city brokers."
        ),
        RealEstateProperty(
            id = "prop_waterfront_dock",
            name = "Marina Harbor Berth",
            district = District.WATERFRONT,
            price = 22000,
            dailyRevenue = 1600,
            location = Vector3(260f, 0f, 150f),
            description = "Deep-water private slip with luxury yacht mooring."
        ),
        RealEstateProperty(
            id = "prop_sky_penthouse",
            name = "Apex Diamond Penthouse",
            district = District.FINANCIAL,
            price = 45000,
            dailyRevenue = 3200,
            location = Vector3(40f, 0f, 180f),
            description = "Panoramic 70th-floor sky residence overlooking the entire metropolis."
        )
    )

    val ownedPropertyIds = mutableStateListOf("prop_safehouse")
    var uncollectedRevenue by mutableIntStateOf(0)
    var revenueTimer by mutableFloatStateOf(0f)

    fun update(deltaTime: Float) {
        revenueTimer += deltaTime
        // Every 45 seconds of game time, accumulate passive income
        if (revenueTimer >= 45f) {
            revenueTimer = 0f
            var totalDaily = 0
            for (p in properties) {
                if (ownedPropertyIds.contains(p.id)) {
                    totalDaily += p.dailyRevenue
                }
            }
            uncollectedRevenue += totalDaily
        }
    }

    fun buyProperty(property: RealEstateProperty, playerCash: Int): Boolean {
        if (playerCash >= property.price && !ownedPropertyIds.contains(property.id)) {
            ownedPropertyIds.add(property.id)
            return true
        }
        return false
    }

    fun collectRevenue(): Int {
        val collected = uncollectedRevenue
        uncollectedRevenue = 0
        return collected
    }
}
