package com.example.game.vehicle

enum class VehicleType(
    val displayName: String,
    val description: String,
    val maxSpeed: Float,         // in game units / second
    val acceleration: Float,
    val handling: Float,
    val brakePower: Float,
    val price: Int,
    val primaryColor: FloatArray,
    val isTwoWheeler: Boolean = false
) {
    METRO_SWIFT(
        displayName = "Metro Swift",
        description = "Nimble and compact city hatchback. Perfect for tight alleys.",
        maxSpeed = 22f,
        acceleration = 12f,
        handling = 3.2f,
        brakePower = 20f,
        price = 4500,
        primaryColor = floatArrayOf(0.15f, 0.55f, 0.85f, 1f)
    ),
    VANGUARD_SEDAN(
        displayName = "Vanguard Sedan",
        description = "Reliable modern 4-door sedan with balanced performance.",
        maxSpeed = 26f,
        acceleration = 13f,
        handling = 2.8f,
        brakePower = 22f,
        price = 8500,
        primaryColor = floatArrayOf(0.25f, 0.28f, 0.32f, 1f)
    ),
    APEX_GT(
        displayName = "Apex GT",
        description = "Sleek high-performance sports car with roaring acceleration.",
        maxSpeed = 38f,
        acceleration = 24f,
        handling = 3.4f,
        brakePower = 32f,
        price = 42000,
        primaryColor = floatArrayOf(0.92f, 0.15f, 0.15f, 1f)
    ),
    TITAN_SUV(
        displayName = "Titan 4x4",
        description = "Heavy duty SUV built to endure curbs and rough city obstacles.",
        maxSpeed = 24f,
        acceleration = 11f,
        handling = 2.2f,
        brakePower = 24f,
        price = 14500,
        primaryColor = floatArrayOf(0.18f, 0.25f, 0.22f, 1f)
    ),
    HAULER_PICKUP(
        displayName = "Hauler Heavy",
        description = "Rugged utility pickup truck with raw low-end torque.",
        maxSpeed = 23f,
        acceleration = 12f,
        handling = 2.3f,
        brakePower = 22f,
        price = 11000,
        primaryColor = floatArrayOf(0.68f, 0.35f, 0.15f, 1f)
    ),
    CARGO_VAN(
        displayName = "Cargo Express",
        description = "Spacious courier transport van for high-volume jobs.",
        maxSpeed = 20f,
        acceleration = 10f,
        handling = 2.0f,
        brakePower = 18f,
        price = 9000,
        primaryColor = floatArrayOf(0.85f, 0.85f, 0.88f, 1f)
    ),
    CITY_CAB(
        displayName = "City Cab",
        description = "Iconic yellow metropolitan taxi. The backbone of city transit.",
        maxSpeed = 25f,
        acceleration = 13f,
        handling = 2.7f,
        brakePower = 21f,
        price = 7500,
        primaryColor = floatArrayOf(0.96f, 0.78f, 0.12f, 1f)
    ),
    SHADOW_CRUISER(
        displayName = "Shadow Cruiser",
        description = "Law enforcement interceptor with tuned engine and siren strobes.",
        maxSpeed = 34f,
        acceleration = 20f,
        handling = 3.0f,
        brakePower = 28f,
        price = 28000,
        primaryColor = floatArrayOf(0.08f, 0.08f, 0.1f, 1f)
    ),
    STREET_GHOST(
        displayName = "Street Ghost",
        description = "Agile sport motorcycle capable of weaving between heavy traffic.",
        maxSpeed = 36f,
        acceleration = 26f,
        handling = 4.2f,
        brakePower = 26f,
        price = 16000,
        primaryColor = floatArrayOf(0.08f, 0.85f, 0.75f, 1f),
        isTwoWheeler = true
    )
}
