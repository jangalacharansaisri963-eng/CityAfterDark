package com.example.game.missions

import com.example.engine.math.Vector3
import com.example.game.world.District

enum class MissionType {
    TRAVEL_TO,
    MEET_CHARACTER,
    TIMED_DELIVERY,
    VEHICLE_RETRIEVAL,
    TAIL_TARGET,
    INVESTIGATE,
    CHECKPOINT_RACE,
    ESCAPE_PURSUIT,
    SHOWDOWN
}

data class MissionObjective(
    val description: String,
    val targetPosition: Vector3,
    val targetRadius: Float = 14f,
    val timeLimitSeconds: Float = 0f, // 0 = no time limit
    val dialogueOnReach: List<DialogueLine> = emptyList()
)

data class DialogueLine(
    val speakerName: String,
    val speakerTitle: String,
    val text: String
)

data class StoryMission(
    val id: Int,
    val chapter: Int,
    val title: String,
    val chapterTitle: String,
    val synopsis: String,
    val giverName: String,
    val rewardCash: Int,
    val startLocation: Vector3,
    val objectives: List<MissionObjective>
)

class MissionManager {
    var currentMissionIndex: Int = 0
    var currentObjectiveIndex: Int = 0
    var isMissionActive: Boolean = false
    var objectiveTimer: Float = 0f

    val completedMissionIds = HashSet<Int>()

    val allMissions: List<StoryMission> = listOf(
        // CHAPTER 1: ARRIVAL IN THE SHADOWS
        StoryMission(
            id = 1,
            chapter = 1,
            title = "New in Town",
            chapterTitle = "Chapter 1: Arrival in the Shadows",
            synopsis = "You arrived on the midnight train with an empty wallet and a borrowed phone. Head to Marcus's Downtown Diner to meet your only contact.",
            giverName = "Marcus Thorne",
            rewardCash = 500,
            startLocation = Vector3(80f, 0f, 40f),
            objectives = listOf(
                MissionObjective(
                    description = "Travel to Marcus's Diner in Downtown",
                    targetPosition = Vector3(20f, 0f, 20f),
                    dialogueOnReach = listOf(
                        DialogueLine("Marcus", "Fixer & Diner Owner", "Leo! You made it. The city hasn't been kind since you left. Take a seat and have some coffee."),
                        DialogueLine("Leo", "Protagonist", "Good to see you, Marcus. I need work, clean or gray. Rent is already due."),
                        DialogueLine("Marcus", "Fixer & Diner Owner", "Nothing in this town is completely clean after sundown. But I have an urgent delivery run if you're ready.")
                    )
                )
            )
        ),
        StoryMission(
            id = 2,
            chapter = 1,
            title = "Midnight Courier",
            chapterTitle = "Chapter 1: Arrival in the Shadows",
            synopsis = "A high-priority data module must be transported from the Waterfront Docks to Elena's workshop in Old Town before security checkpoints activate.",
            giverName = "Marcus Thorne",
            rewardCash = 1200,
            startLocation = Vector3(20f, 0f, 20f),
            objectives = listOf(
                MissionObjective(
                    description = "Drive to Waterfront Warehouse 14 at the docks",
                    targetPosition = Vector3(-180f, 0f, -20f),
                    dialogueOnReach = listOf(
                        DialogueLine("Dock Worker", "Wharf Hand", "Here's the secure crate. Do not open it, and do not let private security spot you.")
                    )
                ),
                MissionObjective(
                    description = "Deliver the cargo to Elena's Workshop in Old Town",
                    targetPosition = Vector3(-140f, 0f, 160f),
                    timeLimitSeconds = 120f,
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "Fast work, Leo. The seals are intact. Marcus said you were dependable.")
                    )
                )
            )
        ),
        StoryMission(
            id = 3,
            chapter = 1,
            title = "Impound Breakout",
            chapterTitle = "Chapter 1: Arrival in the Shadows",
            synopsis = "Marcus's tuned sports car was seized by a rival logistics outfit in the Industrial District. Infiltrate the depot and reclaim it.",
            giverName = "Marcus Thorne",
            rewardCash = 1800,
            startLocation = Vector3(-140f, 0f, 160f),
            objectives = listOf(
                MissionObjective(
                    description = "Infiltrate the Industrial Depot lot",
                    targetPosition = Vector3(180f, 0f, -180f),
                    dialogueOnReach = listOf(
                        DialogueLine("Leo", "Protagonist", "There it is behind the shipping crates. Ignition override ready.")
                    )
                ),
                MissionObjective(
                    description = "Drive the vehicle back to the Downtown Safehouse",
                    targetPosition = Vector3(80f, 0f, 40f),
                    dialogueOnReach = listOf(
                        DialogueLine("Marcus", "Fixer & Diner Owner", "She's purring like new! You earned your keep tonight, Leo. Take her out whenever you need horsepower.")
                    )
                )
            )
        ),
        StoryMission(
            id = 4,
            chapter = 1,
            title = "Shadow Pursuit",
            chapterTitle = "Chapter 1: Arrival in the Shadows",
            synopsis = "A suspicious courier is transporting encrypted ledgers from the Neon Promenade. Tail him to discover who is funding the private security militia.",
            giverName = "Elena Rostova",
            rewardCash = 2200,
            startLocation = Vector3(80f, 0f, 40f),
            objectives = listOf(
                MissionObjective(
                    description = "Locate the courier at Neon Promenade",
                    targetPosition = Vector3(30f, 0f, -80f),
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "I've picked up his tracker signal. He's moving toward the expressway. Stay close!")
                    )
                ),
                MissionObjective(
                    description = "Follow the courier to the Skyway overpass",
                    targetPosition = Vector3(-160f, 0f, -160f),
                    timeLimitSeconds = 90f,
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "He met with an executive from Apex Corporation. Silas Drake's syndicate is involved!")
                    )
                )
            )
        ),

        // CHAPTER 2: UNDERGROUND NETWORK
        StoryMission(
            id = 5,
            chapter = 2,
            title = "Warehouse Stakeout",
            chapterTitle = "Chapter 2: Underground Network",
            synopsis = "Stake out the maritime container yard where Apex Corporation is offloading contraband military-grade surveillance drones.",
            giverName = "Elena Rostova",
            rewardCash = 2500,
            startLocation = Vector3(-160f, 0f, -160f),
            objectives = listOf(
                MissionObjective(
                    description = "Reach the rooftop overlooking the Harbor Crane",
                    targetPosition = Vector3(-240f, 0f, -80f),
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "Camera feeds intercepted. They are stockpiling surveillance jammers for the whole downtown grid.")
                    )
                ),
                MissionObjective(
                    description = "Escape with the surveillance drive to the Old Town safehouse",
                    targetPosition = Vector3(-140f, 0f, 160f)
                )
            )
        ),
        StoryMission(
            id = 6,
            chapter = 2,
            title = "Skyway Intercept",
            chapterTitle = "Chapter 2: Underground Network",
            synopsis = "A heavy armored transport is hauling the master decryption keys across the highway expressway. Intercept it before it crosses city limits.",
            giverName = "Marcus Thorne",
            rewardCash = 3000,
            startLocation = Vector3(-140f, 0f, 160f),
            objectives = listOf(
                MissionObjective(
                    description = "Speed to the Southern Highway on-ramp",
                    targetPosition = Vector3(-80f, 0f, -240f),
                    timeLimitSeconds = 75f
                ),
                MissionObjective(
                    description = "Box in the transport convoy near the bridge",
                    targetPosition = Vector3(-240f, 0f, -240f),
                    dialogueOnReach = listOf(
                        DialogueLine("Leo", "Protagonist", "Transport immobilized. Keys secured! Let's get out of here before backup arrives.")
                    )
                )
            )
        ),
        StoryMission(
            id = 7,
            chapter = 2,
            title = "The Casino Debt",
            chapterTitle = "Chapter 2: Underground Network",
            synopsis = "A corrupt city councilman indebted to Silas Drake has critical city blueprints. Visit the Velvet Lounge and recover the files.",
            giverName = "Elena Rostova",
            rewardCash = 3500,
            startLocation = Vector3(-240f, 0f, -240f),
            objectives = listOf(
                MissionObjective(
                    description = "Drive to the Velvet Lounge in the Shopping District",
                    targetPosition = Vector3(40f, 0f, -160f),
                    dialogueOnReach = listOf(
                        DialogueLine("Councilman", "City Official", "Take whatever you want! Just don't let Drake's men know I gave you the blueprint!")
                    )
                ),
                MissionObjective(
                    description = "Stash the blueprints at your Midtown Apartment",
                    targetPosition = Vector3(80f, 0f, 40f)
                )
            )
        ),
        StoryMission(
            id = 8,
            chapter = 2,
            title = "Informant Extraction",
            chapterTitle = "Chapter 2: Underground Network",
            synopsis = "Drake's enforcers cornered an investigative journalist in an alley in Old Town. Extract him to safety before he is silenced.",
            giverName = "Marcus Thorne",
            rewardCash = 4000,
            startLocation = Vector3(80f, 0f, 40f),
            objectives = listOf(
                MissionObjective(
                    description = "Rush to the Old Town clocktower plaza",
                    targetPosition = Vector3(-200f, 0f, 200f),
                    timeLimitSeconds = 100f,
                    dialogueOnReach = listOf(
                        DialogueLine("Journalist", "Investigative Reporter", "They had me trapped! Drake is planning to shut down the city's power grid tonight!")
                    )
                ),
                MissionObjective(
                    description = "Evacuate the journalist to Marcus's Diner",
                    targetPosition = Vector3(20f, 0f, 20f)
                )
            )
        ),

        // CHAPTER 3: CORPORATE INTRIGUE
        StoryMission(
            id = 9,
            chapter = 3,
            title = "Substation Sabotage",
            chapterTitle = "Chapter 3: Corporate Intrigue",
            synopsis = "Infiltrate the Industrial sector power station and disable Drake's private override switches to protect city communications.",
            giverName = "Elena Rostova",
            rewardCash = 4500,
            startLocation = Vector3(20f, 0f, 20f),
            objectives = listOf(
                MissionObjective(
                    description = "Travel to the Industrial Power Substation",
                    targetPosition = Vector3(220f, 0f, -120f),
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "Bypassing high-voltage relays... Switches flipped! The city grid is back in municipal hands.")
                    )
                ),
                MissionObjective(
                    description = "Regroup at the Financial District rendezvous",
                    targetPosition = Vector3(0f, 0f, 160f)
                )
            )
        ),
        StoryMission(
            id = 10,
            chapter = 3,
            title = "Undercover Infiltration",
            chapterTitle = "Chapter 3: Corporate Intrigue",
            synopsis = "Don executive attire and infiltrate Apex Tower's executive garage to plant an audio bug on Silas Drake's personal limousine.",
            giverName = "Marcus Thorne",
            rewardCash = 5000,
            startLocation = Vector3(0f, 0f, 160f),
            objectives = listOf(
                MissionObjective(
                    description = "Drive to Apex Tower executive parking",
                    targetPosition = Vector3(20f, 0f, 220f),
                    dialogueOnReach = listOf(
                        DialogueLine("Leo", "Protagonist", "Bug planted underneath the rear chassis. Elena, do you hear them?")
                    )
                ),
                MissionObjective(
                    description = "Escape smoothly without raising alarms to Downtown",
                    targetPosition = Vector3(0f, 0f, 0f),
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "Audio is crystal clear. Drake is planning an offshore transfer from the Southern Aerodrome!")
                    )
                )
            )
        ),
        StoryMission(
            id = 11,
            chapter = 3,
            title = "Runway Intercept",
            chapterTitle = "Chapter 3: Corporate Intrigue",
            synopsis = "Race down the southern perimeter road to the airport and prevent the chartered cargo plane from departing with city bearer bonds.",
            giverName = "Elena Rostova",
            rewardCash = 6000,
            startLocation = Vector3(0f, 0f, 0f),
            objectives = listOf(
                MissionObjective(
                    description = "Sprint to Southern Aerodrome Hangar 3",
                    targetPosition = Vector3(-40f, 0f, -380f),
                    timeLimitSeconds = 110f,
                    dialogueOnReach = listOf(
                        DialogueLine("Leo", "Protagonist", "Blocked the runway tarmac! The transport cannot take off!")
                    )
                ),
                MissionObjective(
                    description = "Recover the financial bond cases and drive to Marina",
                    targetPosition = Vector3(-200f, 0f, 0f)
                )
            )
        ),
        StoryMission(
            id = 12,
            chapter = 3,
            title = "Blackout Protocol",
            chapterTitle = "Chapter 3: Corporate Intrigue",
            synopsis = "Drake triggers emergency sirens and locks down Downtown streets. Navigate the barricades and reach Marcus before hit squads arrive.",
            giverName = "Marcus Thorne",
            rewardCash = 7000,
            startLocation = Vector3(-200f, 0f, 0f),
            objectives = listOf(
                MissionObjective(
                    description = "Speed across city roadblocks to Marcus's Diner",
                    targetPosition = Vector3(20f, 0f, 20f),
                    timeLimitSeconds = 85f,
                    dialogueOnReach = listOf(
                        DialogueLine("Marcus", "Fixer & Diner Owner", "They surrounded the block! It's time we take the fight directly to Drake's penthouse.")
                    )
                )
            )
        ),

        // CHAPTER 4: CITY SOVEREIGN
        StoryMission(
            id = 13,
            chapter = 4,
            title = "The Waterfront Ambush",
            chapterTitle = "Chapter 4: City Sovereign",
            synopsis = "Silas Drake ambushes your supply cache at the Waterfront docks. Turn the tables, secure the perimeter, and seize his command convoy.",
            giverName = "Marcus Thorne",
            rewardCash = 8500,
            startLocation = Vector3(20f, 0f, 20f),
            objectives = listOf(
                MissionObjective(
                    description = "Travel to Waterfront Pier 7",
                    targetPosition = Vector3(-220f, 0f, -40f),
                    dialogueOnReach = listOf(
                        DialogueLine("Leo", "Protagonist", "Ambush neutralized. His enforcers are scattering. We have his personal vehicle keys!")
                    )
                ),
                MissionObjective(
                    description = "Deliver the command convoy to Elena's workshop",
                    targetPosition = Vector3(-140f, 0f, 160f)
                )
            )
        ),
        StoryMission(
            id = 14,
            chapter = 4,
            title = "Skyline Pursuit",
            chapterTitle = "Chapter 4: City Sovereign",
            synopsis = "Drake flees in an armored limousine through the neon night rain. Chase him across all four districts and force him to stop.",
            giverName = "Elena Rostova",
            rewardCash = 10000,
            startLocation = Vector3(-140f, 0f, 160f),
            objectives = listOf(
                MissionObjective(
                    description = "Tail the armored limousine through Neon Promenade",
                    targetPosition = Vector3(20f, 0f, -120f),
                    timeLimitSeconds = 90f
                ),
                MissionObjective(
                    description = "Corner the vehicle at the Financial Plaza",
                    targetPosition = Vector3(10f, 0f, 180f),
                    dialogueOnReach = listOf(
                        DialogueLine("Silas Drake", "Corporate Antagonist", "You think you won, Vance? This entire city belongs to my board of directors!")
                    )
                )
            )
        ),
        StoryMission(
            id = 15,
            chapter = 4,
            title = "Apex Showdown",
            chapterTitle = "Chapter 4: City Sovereign",
            synopsis = "Storm Apex Tower penthouse, extract the master syndicate files, and expose Drake's corruption across the city's broadcast network.",
            giverName = "Marcus Thorne",
            rewardCash = 15000,
            startLocation = Vector3(10f, 0f, 180f),
            objectives = listOf(
                MissionObjective(
                    description = "Breach Apex Corporate Tower Central Entrance",
                    targetPosition = Vector3(0f, 0f, 200f),
                    dialogueOnReach = listOf(
                        DialogueLine("Elena", "Tech Specialist", "Broadcasting the files to every television screen and digital billboard in the city! Drake's empire is finished!")
                    )
                ),
                MissionObjective(
                    description = "Ascend and secure the skyline terrace",
                    targetPosition = Vector3(0f, 0f, 240f)
                )
            )
        ),
        StoryMission(
            id = 16,
            chapter = 4,
            title = "Dawn of an Empire",
            chapterTitle = "Chapter 4: City Sovereign",
            synopsis = "With Drake deposed, the city's future lies in your hands. Celebrate with Marcus and Elena at the Marina safehouse as dawn breaks over the skyline.",
            giverName = "Marcus & Elena",
            rewardCash = 25000,
            startLocation = Vector3(0f, 0f, 240f),
            objectives = listOf(
                MissionObjective(
                    description = "Drive in victory to the Marina Grand Safehouse",
                    targetPosition = Vector3(-180f, 0f, 20f),
                    dialogueOnReach = listOf(
                        DialogueLine("Marcus", "Fixer & Diner Owner", "Look at the sun coming up over those towers. You ran this city after dark, Leo. Now you own it."),
                        DialogueLine("Elena", "Tech Specialist", "The underground respects you, the streets are calm, and the syndicate is gone. Welcome to your new city."),
                        DialogueLine("Leo", "Protagonist", "It's been a long night. But the city is finally ours.")
                    )
                )
            )
        )
    )

    fun getCurrentMission(): StoryMission? {
        return if (currentMissionIndex in allMissions.indices) allMissions[currentMissionIndex] else null
    }

    fun getCurrentObjective(): MissionObjective? {
        val mission = getCurrentMission() ?: return null
        return if (currentObjectiveIndex in mission.objectives.indices) mission.objectives[currentObjectiveIndex] else null
    }

    fun startCurrentMission() {
        isMissionActive = true
        currentObjectiveIndex = 0
        val obj = getCurrentObjective()
        objectiveTimer = obj?.timeLimitSeconds ?: 0f
    }

    fun advanceObjective(): Boolean {
        val mission = getCurrentMission() ?: return false
        currentObjectiveIndex++
        if (currentObjectiveIndex >= mission.objectives.size) {
            // Mission completed!
            completedMissionIds.add(mission.id)
            isMissionActive = false
            currentMissionIndex++
            currentObjectiveIndex = 0
            return true // Mission finished
        } else {
            val obj = getCurrentObjective()
            objectiveTimer = obj?.timeLimitSeconds ?: 0f
            return false // Advanced to next objective
        }
    }
}
