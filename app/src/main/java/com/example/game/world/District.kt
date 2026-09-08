package com.example.game.world

enum class District(
    val id: String,
    val title: String,
    val description: String,
    val ambientMood: String,
    val minimapColor: Long
) {
    DOWNTOWN(
        id = "downtown",
        title = "Downtown Center",
        description = "Gleaming corporate spires, neon billboards, and central transit arteries.",
        ambientMood = "Metropolitan roar and flashing neon reflections.",
        minimapColor = 0xFF00E5FF
    ),
    FINANCIAL(
        id = "financial",
        title = "Financial Core",
        description = "Monolithic glass headquarters, banking plazas, and executive security.",
        ambientMood = "Humming HVAC chillers and distant police sirens.",
        minimapColor = 0xFF3D5AFE
    ),
    WATERFRONT(
        id = "waterfront",
        title = "Waterfront Marina & Docks",
        description = "Shipping piers, container yards, maritime cranes, and coastal highways.",
        ambientMood = "Ocean breeze, creaking wooden moorings, and fog horns.",
        minimapColor = 0xFF00B0FF
    ),
    SHOPPING(
        id = "shopping",
        title = "Neon Promenade",
        description = "High-end fashion boutiques, nightlife clubs, dining plazas, and valet parking.",
        ambientMood = "Pulsing basslines, chatter, and vibrant storefront canopies.",
        minimapColor = 0xFFFF4081
    ),
    INDUSTRIAL(
        id = "industrial",
        title = "Steelworks Industrial",
        description = "Brick warehouses, smokestacks, freight depots, and underground workshops.",
        ambientMood = "Metallic clanging, rumbling diesel generators, and heavy trucks.",
        minimapColor = 0xFFFF9100
    ),
    RESIDENTIAL(
        id = "residential",
        title = "Midtown Residential",
        description = "Classic brownstones, fire escapes, rooftop gardens, and corner delis.",
        ambientMood = "Lively neighborhood chatter and passing city cabs.",
        minimapColor = 0xFF76FF03
    ),
    SUBURBAN(
        id = "suburban",
        title = "Maple Heights Suburb",
        description = "Tree-lined residential avenues, driveway garages, and quiet family cul-de-sacs.",
        ambientMood = "Whispering foliage, sprinkler murmurs, and calm evening air.",
        minimapColor = 0xFFC6FF00
    ),
    OLD_TOWN(
        id = "old_town",
        title = "Old Town Heritage Quarter",
        description = "Cobblestone streets, historic brick facades, arched alleys, and clock tower square.",
        ambientMood = "Echoing footsteps and chimes from the central clock tower.",
        minimapColor = 0xFFFFD740
    ),
    HIGHWAY(
        id = "highway",
        title = "Skyway Expressway",
        description = "Elevated multi-lane speedway cutting across the metropolitan skyline.",
        ambientMood = "High-speed tire whoosh and rushing headwinds.",
        minimapColor = 0xFFFF5252
    ),
    OUTSKIRTS(
        id = "outskirts",
        title = "Southern Aerodrome & Outskirts",
        description = "Aviation hangars, cargo runways, transmission towers, and open perimeter roads.",
        ambientMood = "Wailing jet turbine whine and desolate highway winds.",
        minimapColor = 0xFFB388FF
    )
}
