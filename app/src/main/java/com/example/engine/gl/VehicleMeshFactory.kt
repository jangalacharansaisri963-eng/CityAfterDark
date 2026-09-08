package com.example.engine.gl

import com.example.game.vehicle.VehicleType

object VehicleMeshFactory {

    fun createVehicleMesh(type: VehicleType): Mesh {
        val b = MeshBuilder()

        val bodyColor = type.primaryColor
        val windowColor = floatArrayOf(0.12f, 0.16f, 0.22f, 0.9f)
        val wheelColor = floatArrayOf(0.12f, 0.12f, 0.12f, 1f)
        val rimColor = floatArrayOf(0.75f, 0.75f, 0.78f, 1f)
        val headlightColor = floatArrayOf(1.0f, 0.98f, 0.85f, 1f)
        val taillightColor = floatArrayOf(0.95f, 0.12f, 0.12f, 1f)

        when (type) {
            VehicleType.STREET_GHOST -> {
                // Motorcycle / sport bike
                // Main frame & engine
                b.addBox(0f, 0.65f, 0f, 0.4f, 0.5f, 1.8f, bodyColor)
                // Fuel tank & seat
                b.addBox(0f, 0.9f, -0.1f, 0.35f, 0.25f, 0.9f, floatArrayOf(0.15f, 0.15f, 0.15f, 1f))
                // Handlebars
                b.addBox(0f, 1.05f, 0.5f, 0.9f, 0.08f, 0.08f, floatArrayOf(0.3f, 0.3f, 0.35f, 1f))
                // Front fork
                b.addCylinder(0f, 0.6f, 0.8f, 0.06f, 0.8f, 6, floatArrayOf(0.6f, 0.6f, 0.65f, 1f))
                // Front wheel
                b.addCylinder(0f, 0.35f, 0.9f, 0.35f, 0.16f, 8, wheelColor)
                // Rear wheel
                b.addCylinder(0f, 0.35f, -0.8f, 0.35f, 0.2f, 8, wheelColor)
                // Headlight
                b.addBox(0f, 0.95f, 0.92f, 0.22f, 0.2f, 0.05f, headlightColor)
                // Taillight
                b.addBox(0f, 0.85f, -0.92f, 0.2f, 0.15f, 0.05f, taillightColor)
            }

            VehicleType.APEX_GT -> {
                // Sleek low-slung supercar
                val l = 4.4f
                val w = 2.0f
                val h = 0.55f

                // Lower aerodynamic chassis
                b.addBox(0f, 0.45f, 0f, w, h, l, bodyColor)
                // Low cabin greenhouse
                b.addBox(0f, 0.85f, -0.2f, w * 0.78f, 0.45f, l * 0.45f, windowColor)
                // Rear aero spoiler
                b.addBox(0f, 0.95f, -l * 0.45f, w * 0.85f, 0.08f, 0.35f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))
                b.addBox(-w * 0.35f, 0.75f, -l * 0.45f, 0.08f, 0.35f, 0.1f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))
                b.addBox(w * 0.35f, 0.75f, -l * 0.45f, 0.08f, 0.35f, 0.1f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))

                // Aggressive headlights
                b.addBox(-0.65f, 0.5f, l * 0.48f, 0.45f, 0.14f, 0.08f, headlightColor)
                b.addBox(0.65f, 0.5f, l * 0.48f, 0.45f, 0.14f, 0.08f, headlightColor)
                // Neon taillight bar
                b.addBox(0f, 0.55f, -l * 0.49f, w * 0.8f, 0.12f, 0.06f, taillightColor)

                // 4 Wheels
                addFourWheels(b, w * 0.52f, l * 0.32f, 0.32f, 0.34f, wheelColor, rimColor)
            }

            VehicleType.VANGUARD_SEDAN -> {
                // Classic modern 4-door sedan
                val l = 4.6f
                val w = 1.9f
                val h = 0.65f

                // Lower chassis
                b.addBox(0f, 0.5f, 0f, w, h, l, bodyColor)
                // Cabin & glass
                b.addBox(0f, 1.05f, -0.1f, w * 0.82f, 0.55f, l * 0.52f, windowColor)
                // Roof top
                b.addBox(0f, 1.34f, -0.1f, w * 0.76f, 0.06f, l * 0.42f, bodyColor)

                // Headlights & taillights
                b.addBox(-0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(-0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)
                b.addBox(0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.52f, l * 0.33f, 0.34f, 0.32f, wheelColor, rimColor)
            }

            VehicleType.TITAN_SUV -> {
                // Rugged lifted SUV
                val l = 4.8f
                val w = 2.1f
                val h = 0.8f

                // Chassis
                b.addBox(0f, 0.7f, 0f, w, h, l, bodyColor)
                // Extended cabin
                b.addBox(0f, 1.35f, -0.3f, w * 0.85f, 0.7f, l * 0.62f, windowColor)
                b.addBox(0f, 1.72f, -0.3f, w * 0.8f, 0.08f, l * 0.58f, bodyColor)

                // Roof rack
                b.addBox(0f, 1.82f, -0.3f, w * 0.7f, 0.08f, l * 0.5f, floatArrayOf(0.2f, 0.2f, 0.2f, 1f))

                // Headlights & taillights
                b.addBox(-0.7f, 0.78f, l * 0.49f, 0.45f, 0.25f, 0.06f, headlightColor)
                b.addBox(0.7f, 0.78f, l * 0.49f, 0.45f, 0.25f, 0.06f, headlightColor)
                b.addBox(-0.7f, 0.78f, -l * 0.49f, 0.35f, 0.35f, 0.06f, taillightColor)
                b.addBox(0.7f, 0.78f, -l * 0.49f, 0.35f, 0.35f, 0.06f, taillightColor)

                // Big rugged wheels
                addFourWheels(b, w * 0.54f, l * 0.32f, 0.44f, 0.36f, wheelColor, rimColor)
            }

            VehicleType.METRO_SWIFT -> {
                // Compact city hatchback
                val l = 3.6f
                val w = 1.75f
                val h = 0.62f

                b.addBox(0f, 0.5f, 0f, w, h, l, bodyColor)
                b.addBox(0f, 1.05f, -0.2f, w * 0.82f, 0.55f, l * 0.55f, windowColor)
                b.addBox(0f, 1.34f, -0.2f, w * 0.78f, 0.06f, l * 0.48f, bodyColor)

                b.addBox(-0.55f, 0.58f, l * 0.49f, 0.38f, 0.18f, 0.06f, headlightColor)
                b.addBox(0.55f, 0.58f, l * 0.49f, 0.38f, 0.18f, 0.06f, headlightColor)
                b.addBox(-0.55f, 0.62f, -l * 0.49f, 0.32f, 0.3f, 0.06f, taillightColor)
                b.addBox(0.55f, 0.62f, -l * 0.49f, 0.32f, 0.3f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.52f, l * 0.32f, 0.32f, 0.28f, wheelColor, rimColor)
            }

            VehicleType.HAULER_PICKUP -> {
                // Heavy duty pickup truck
                val l = 5.2f
                val w = 2.1f
                val h = 0.75f

                // Cab
                b.addBox(0f, 0.65f, 0.3f, w, h, l * 0.55f, bodyColor)
                b.addBox(0f, 1.3f, 0.2f, w * 0.85f, 0.65f, 1.8f, windowColor)
                // Flatbed rear
                b.addBox(0f, 0.65f, -l * 0.26f, w, 0.45f, l * 0.45f, bodyColor)
                b.addBox(0f, 0.95f, -l * 0.48f, w, 0.4f, 0.1f, bodyColor) // Tailgate

                b.addBox(-0.7f, 0.72f, l * 0.5f, 0.45f, 0.22f, 0.06f, headlightColor)
                b.addBox(0.7f, 0.72f, l * 0.5f, 0.45f, 0.22f, 0.06f, headlightColor)
                b.addBox(-0.7f, 0.72f, -l * 0.49f, 0.3f, 0.3f, 0.06f, taillightColor)
                b.addBox(0.7f, 0.72f, -l * 0.49f, 0.3f, 0.3f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.53f, l * 0.32f, 0.42f, 0.35f, wheelColor, rimColor)
            }

            VehicleType.CARGO_VAN -> {
                // Delivery box van
                val l = 5.2f
                val w = 2.0f
                val h = 1.4f

                b.addBox(0f, 1.0f, 0f, w, h, l, bodyColor)
                b.addBox(0f, 1.25f, 1.5f, w * 0.88f, 0.6f, 1.2f, windowColor)

                b.addBox(-0.65f, 0.65f, l * 0.49f, 0.42f, 0.25f, 0.06f, headlightColor)
                b.addBox(0.65f, 0.65f, l * 0.49f, 0.42f, 0.25f, 0.06f, headlightColor)
                b.addBox(-0.65f, 0.85f, -l * 0.49f, 0.32f, 0.5f, 0.06f, taillightColor)
                b.addBox(0.65f, 0.85f, -l * 0.49f, 0.32f, 0.5f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.52f, l * 0.32f, 0.38f, 0.32f, wheelColor, rimColor)
            }

            VehicleType.CITY_CAB -> {
                // Yellow taxi with rooftop illuminated TAXI fixture
                val l = 4.6f
                val w = 1.9f
                val h = 0.65f

                b.addBox(0f, 0.5f, 0f, w, h, l, floatArrayOf(0.96f, 0.78f, 0.12f, 1f))
                b.addBox(0f, 1.05f, -0.1f, w * 0.82f, 0.55f, l * 0.52f, windowColor)
                b.addBox(0f, 1.34f, -0.1f, w * 0.76f, 0.06f, l * 0.42f, floatArrayOf(0.96f, 0.78f, 0.12f, 1f))

                // Illuminated Taxi sign on roof
                b.addBox(0f, 1.48f, -0.1f, 0.65f, 0.22f, 0.35f, floatArrayOf(1.0f, 0.95f, 0.7f, 1f))

                b.addBox(-0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(-0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)
                b.addBox(0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.52f, l * 0.33f, 0.34f, 0.32f, wheelColor, rimColor)
            }

            VehicleType.SHADOW_CRUISER -> {
                // High-spec police interceptor with lightbar
                val l = 4.7f
                val w = 1.95f
                val h = 0.65f

                // Black and white livery
                b.addBox(0f, 0.5f, 0f, w, h, l, floatArrayOf(0.1f, 0.1f, 0.12f, 1f))
                b.addBox(0f, 0.5f, 0f, w + 0.02f, h * 0.95f, l * 0.4f, floatArrayOf(0.95f, 0.95f, 0.98f, 1f)) // White doors
                b.addBox(0f, 1.05f, -0.1f, w * 0.82f, 0.55f, l * 0.52f, windowColor)
                b.addBox(0f, 1.34f, -0.1f, w * 0.76f, 0.06f, l * 0.42f, floatArrayOf(0.1f, 0.1f, 0.12f, 1f))

                // Police roof lightbar (Blue & Red)
                b.addBox(-0.35f, 1.44f, -0.1f, 0.45f, 0.15f, 0.25f, floatArrayOf(0.15f, 0.35f, 0.98f, 1f)) // Blue beacon
                b.addBox(0.35f, 1.44f, -0.1f, 0.45f, 0.15f, 0.25f, floatArrayOf(0.98f, 0.15f, 0.15f, 1f)) // Red beacon
                b.addBox(0f, 1.44f, -0.1f, 0.25f, 0.14f, 0.22f, floatArrayOf(0.95f, 0.95f, 0.98f, 1f)) // White center strobe

                b.addBox(-0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(0.62f, 0.58f, l * 0.49f, 0.42f, 0.2f, 0.06f, headlightColor)
                b.addBox(-0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)
                b.addBox(0.62f, 0.58f, -l * 0.49f, 0.42f, 0.2f, 0.06f, taillightColor)

                addFourWheels(b, w * 0.52f, l * 0.33f, 0.34f, 0.32f, wheelColor, rimColor)
            }
        }

        return b.build()
    }

    private fun addFourWheels(
        b: MeshBuilder,
        trackWidth: Float,
        wheelbase: Float,
        radius: Float,
        width: Float,
        tireColor: FloatArray,
        rimColor: FloatArray
    ) {
        // Front left, front right, rear left, rear right
        val xOffsets = floatArrayOf(-trackWidth, trackWidth, -trackWidth, trackWidth)
        val zOffsets = floatArrayOf(wheelbase, wheelbase, -wheelbase, -wheelbase)

        for (i in 0 until 4) {
            val x = xOffsets[i]
            val z = zOffsets[i]
            // Tire
            b.addBox(x, radius, z, width, radius * 2f, radius * 2f, tireColor)
            // Rim hubcap
            val rimOffset = if (x > 0) 0.02f else -0.02f
            b.addBox(x + rimOffset, radius, z, 0.05f, radius * 1.2f, radius * 1.2f, rimColor)
        }
    }
}
