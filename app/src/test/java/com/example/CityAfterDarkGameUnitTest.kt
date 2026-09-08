package com.example

import com.example.engine.math.Vector3
import com.example.game.character.Player
import com.example.game.missions.MissionManager
import com.example.game.simulation.DayNightSystem
import com.example.game.simulation.WeatherType
import com.example.game.vehicle.VehicleType
import com.example.game.world.District
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CityAfterDarkGameUnitTest {

    @Test
    fun testVectorMath() {
        val v1 = Vector3(3f, 4f, 0f)
        assertEquals(5f, v1.length(), 0.001f)

        val v2 = Vector3(10f, 0f, 0f)
        val v3 = Vector3(10f, 0f, 10f)
        assertEquals(10f, v2.distanceToXZ(v3), 0.001f)
    }

    @Test
    fun testAllTenDistrictsConfigured() {
        val districts = District.values()
        assertEquals(10, districts.size)
        assertTrue(districts.any { it == District.DOWNTOWN })
        assertTrue(districts.any { it == District.FINANCIAL })
        assertTrue(districts.any { it == District.WATERFRONT })
        assertTrue(districts.any { it == District.SHOPPING })
        assertTrue(districts.any { it == District.INDUSTRIAL })
        assertTrue(districts.any { it == District.RESIDENTIAL })
        assertTrue(districts.any { it == District.SUBURBAN })
        assertTrue(districts.any { it == District.OLD_TOWN })
        assertTrue(districts.any { it == District.HIGHWAY })
        assertTrue(districts.any { it == District.OUTSKIRTS })
    }

    @Test
    fun testAllNineVehicleTypesConfigured() {
        val vehicles = VehicleType.values()
        assertEquals(9, vehicles.size)
        assertTrue(vehicles.any { it == VehicleType.METRO_SWIFT })
        assertTrue(vehicles.any { it == VehicleType.VANGUARD_SEDAN })
        assertTrue(vehicles.any { it == VehicleType.APEX_GT })
        assertTrue(vehicles.any { it == VehicleType.TITAN_SUV })
        assertTrue(vehicles.any { it == VehicleType.HAULER_PICKUP })
        assertTrue(vehicles.any { it == VehicleType.CARGO_VAN })
        assertTrue(vehicles.any { it == VehicleType.CITY_CAB })
        assertTrue(vehicles.any { it == VehicleType.SHADOW_CRUISER })
        assertTrue(vehicles.any { it == VehicleType.STREET_GHOST })
    }

    @Test
    fun testStoryCampaignMissions() {
        val missionManager = MissionManager()
        assertEquals(16, missionManager.allMissions.size)

        // Verify all 4 chapters represented
        val chapters = missionManager.allMissions.map { it.chapter }.distinct()
        assertEquals(listOf(1, 2, 3, 4), chapters)

        val firstMission = missionManager.getCurrentMission()
        assertNotNull(firstMission)
        assertEquals("New in Town", firstMission?.title)
        assertEquals(500, firstMission?.rewardCash)
    }

    @Test
    fun testDayNightCycle() {
        val dn = DayNightSystem()
        dn.timeOfDay = 20.5f // 8:30 PM
        dn.update(0.016f)
        assertTrue(dn.areCityLightsOn)
        assertTrue(dn.emissiveMultiplier > 0.5f)

        dn.weather = WeatherType.RAIN
        dn.update(0.016f)
        assertTrue(dn.wetness > 0.5f)
    }

    @Test
    fun testPlayerWardrobeOutfits() {
        val outfits = Player.OUTFITS
        assertTrue(outfits.size >= 5)
        assertEquals(0, outfits[0].price) // Default starting outfit is free
    }
}
