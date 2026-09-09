package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class SkillPerk(
    val id: String,
    val name: String,
    val description: String,
    val costSkillPoints: Int,
    val iconCategory: String
)

class SkillTreeSystem {
    var playerXP by mutableIntStateOf(350)
    var playerLevel by mutableIntStateOf(1)
    var skillPointsAvailable by mutableIntStateOf(2)

    val unlockedPerkIds = mutableStateListOf<String>()

    val availablePerks = listOf(
        SkillPerk(
            id = "perk_nitro",
            name = "Nitro Overdrive",
            description = "NOS turbo recharges 50% faster and lasts 25% longer.",
            costSkillPoints = 1,
            iconCategory = "nitro"
        ),
        SkillPerk(
            id = "perk_armor",
            name = "Kevlar Reinforcement",
            description = "Increases maximum armor capacity to 150 points.",
            costSkillPoints = 1,
            iconCategory = "shield"
        ),
        SkillPerk(
            id = "perk_stamina",
            name = "Iron Lungs",
            description = "Stamina regenerates twice as fast while walking or resting.",
            costSkillPoints = 1,
            iconCategory = "stamina"
        ),
        SkillPerk(
            id = "perk_drift",
            name = "Tokyo Stunt Master",
            description = "Doubles all cash rewards earned from drifting and street stunts.",
            costSkillPoints = 2,
            iconCategory = "drift"
        ),
        SkillPerk(
            id = "perk_fixer",
            name = "Syndicate Negotiator",
            description = "Increases all campaign and side mission cash payouts by 25%.",
            costSkillPoints = 2,
            iconCategory = "cash"
        )
    )

    val rankTitle: String
        get() = when (playerLevel) {
            1 -> "Street Rookie"
            2 -> "Alley Scout"
            3 -> "Night Runner"
            4 -> "Wheelman"
            5 -> "Syndicate Enforcer"
            6 -> "District Lieutenant"
            7 -> "Underboss"
            8 -> "Cartel Overseer"
            9 -> "Metropolis Mastermind"
            else -> "Kingpin of City After Dark"
        }

    fun addXP(amount: Int): Boolean {
        playerXP += amount
        val xpNeeded = playerLevel * 500
        if (playerXP >= xpNeeded) {
            playerLevel++
            playerXP -= xpNeeded
            skillPointsAvailable++
            return true // Leveled up
        }
        return false
    }

    fun unlockPerk(perk: SkillPerk): Boolean {
        if (skillPointsAvailable >= perk.costSkillPoints && !unlockedPerkIds.contains(perk.id)) {
            skillPointsAvailable -= perk.costSkillPoints
            unlockedPerkIds.add(perk.id)
            return true
        }
        return false
    }

    fun hasPerk(perkId: String): Boolean = unlockedPerkIds.contains(perkId)
}
