package com.example.game.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class RadioTrack(
    val title: String,
    val artist: String,
    val genre: String
)

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val genre: String,
    val playlist: List<RadioTrack>
)

class RadioSystem {
    val stations = listOf(
        RadioStation(
            id = "synth",
            name = "Waveform 84 FM",
            frequency = "98.4 MHz",
            genre = "Retrowave / Synth",
            playlist = listOf(
                RadioTrack("Midnight Drive", "Kavinsky Neon", "Synthwave"),
                RadioTrack("Turbo Pacific", "Vector Seven", "Cyber Electro"),
                RadioTrack("Chrome Sunrise", "Lazerpunk", "Darksynth")
            )
        ),
        RadioStation(
            id = "electro",
            name = "Nightfall Club FM",
            frequency = "104.2 MHz",
            genre = "Dark Techno / Industrial",
            playlist = listOf(
                RadioTrack("Warehouse Riot", "Gesaffelstein Pulse", "Dark Techno"),
                RadioTrack("Subwoofer Overdrive", "Cyberpunk Syndicate", "Industrial Bass"),
                RadioTrack("Strobe Reflex", "Boy Harsher Edit", "EBM")
            )
        ),
        RadioStation(
            id = "hiphop",
            name = "Block Sound 101",
            frequency = "101.5 MHz",
            genre = "Boom Bap / Phonk",
            playlist = listOf(
                RadioTrack("Concrete Jungle Flow", "Vance & The Crew", "East Coast Rap"),
                RadioTrack("Drift Cartel Phonk", "LXST Century", "Drift Phonk"),
                RadioTrack("Lowrider Hydraulic", "Southside Collective", "West Coast G-Funk")
            )
        ),
        RadioStation(
            id = "jazz",
            name = "Velvet Noir Lounge",
            frequency = "89.1 MHz",
            genre = "Neo-Noir Ambient Jazz",
            playlist = listOf(
                RadioTrack("Raindrops on Broadway", "Miles Quintet", "Cool Jazz"),
                RadioTrack("Smoky Alleyway Sax", "Harbor City Trio", "Noir Jazz"),
                RadioTrack("Midnight Espresso", "Blue Note Express", "Bebop")
            )
        ),
        RadioStation(
            id = "off",
            name = "Radio OFF",
            frequency = "--.- MHz",
            genre = "Silent Engine",
            playlist = emptyList()
        )
    )

    var currentStationIndex by mutableIntStateOf(0)
    var trackIndex by mutableIntStateOf(0)
    var isMuted by mutableStateOf(false)
    var bannerToast by mutableStateOf<String?>(null)

    val currentStation: RadioStation
        get() = stations[currentStationIndex]

    val currentTrack: RadioTrack?
        get() = currentStation.playlist.getOrNull(trackIndex % (currentStation.playlist.size.coerceAtLeast(1)))

    fun nextStation() {
        currentStationIndex = (currentStationIndex + 1) % stations.size
        trackIndex = 0
        bannerToast = "${currentStation.name} • ${currentStation.genre}"
    }

    fun previousStation() {
        currentStationIndex = if (currentStationIndex - 1 < 0) stations.size - 1 else currentStationIndex - 1
        trackIndex = 0
        bannerToast = "${currentStation.name} • ${currentStation.genre}"
    }

    fun toggleMute() {
        isMuted = !isMuted
        bannerToast = if (isMuted) "Radio Muted" else "Radio Unmuted: ${currentStation.name}"
    }
}
