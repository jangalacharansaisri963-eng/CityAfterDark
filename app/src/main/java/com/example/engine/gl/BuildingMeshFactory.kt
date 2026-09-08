package com.example.engine.gl

import kotlin.random.Random

object BuildingMeshFactory {

    enum class BuildingStyle {
        DOWNTOWN_SKYSCRAPER,
        FINANCIAL_TOWER,
        WATERFRONT_WAREHOUSE,
        SHOPPING_STOREFRONT,
        INDUSTRIAL_FACTORY,
        RESIDENTIAL_APARTMENTS,
        SUBURBAN_HOUSE,
        OLD_TOWN_BRICK,
        AIRPORT_HANGAR,
        HIGHWAY_STRUCTURE
    }

    fun createBuilding(
        style: BuildingStyle,
        width: Float,
        depth: Float,
        height: Float,
        seed: Long = 0L
    ): Mesh {
        val random = Random(seed)
        val b = MeshBuilder()

        when (style) {
            BuildingStyle.DOWNTOWN_SKYSCRAPER -> {
                // Sleek modern skyscraper with glass windows and lit rooftop spire
                val baseColor = floatArrayOf(0.12f, 0.16f, 0.22f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, baseColor)

                // Illuminated window facade bands
                val bands = 5
                val bandHeight = height / (bands * 2f)
                for (i in 0 until bands) {
                    val y = (i * 2 + 1) * bandHeight
                    val winColor = if (i % 2 == 0) floatArrayOf(0.95f, 0.9f, 0.6f, 0.9f) else floatArrayOf(0.3f, 0.65f, 0.9f, 0.9f)
                    b.addBox(0f, y, depth * 0.5f + 0.05f, width * 0.85f, bandHeight * 0.75f, 0.1f, winColor)
                    b.addBox(0f, y, -depth * 0.5f - 0.05f, width * 0.85f, bandHeight * 0.75f, 0.1f, winColor)
                }

                // Rooftop AC chiller units & antenna spire
                b.addBox(0f, height + 1.2f, 0f, width * 0.5f, 2.4f, depth * 0.5f, floatArrayOf(0.2f, 0.22f, 0.25f, 1f))
                b.addCylinder(0f, height + 6.0f, 0f, 0.3f, 8.0f, 6, floatArrayOf(0.85f, 0.85f, 0.9f, 1f))
                // Red warning beacon on top
                b.addBox(0f, height + 10.0f, 0f, 0.5f, 0.5f, 0.5f, floatArrayOf(0.95f, 0.1f, 0.1f, 1f))
            }

            BuildingStyle.FINANCIAL_TOWER -> {
                // High-rise glass & steel corporate building with neon accents
                val towerColor = floatArrayOf(0.18f, 0.26f, 0.38f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, towerColor)

                // Stepped crown
                val crownH = 5.0f
                b.addBox(0f, height + crownH * 0.5f, 0f, width * 0.75f, crownH, depth * 0.75f, floatArrayOf(0.25f, 0.35f, 0.48f, 1f))

                // Vertical glowing cyan accent pillars
                val accentColor = floatArrayOf(0.1f, 0.85f, 0.95f, 1f)
                b.addBox(-width * 0.5f, height * 0.5f, depth * 0.5f + 0.06f, 0.6f, height, 0.2f, accentColor)
                b.addBox(width * 0.5f, height * 0.5f, depth * 0.5f + 0.06f, 0.6f, height, 0.2f, accentColor)

                // Horizontal accent band
                b.addBox(0f, height * 0.7f, depth * 0.5f + 0.05f, width * 0.9f, 1.5f, 0.1f, floatArrayOf(0.9f, 0.95f, 1.0f, 0.9f))
            }

            BuildingStyle.SHOPPING_STOREFRONT -> {
                // Commercial building with storefront glass and bright billboard sign
                val shopColor = floatArrayOf(0.35f, 0.32f, 0.36f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, shopColor)

                // Large ground floor display windows
                b.addBox(0f, 2.2f, depth * 0.5f + 0.08f, width * 0.8f, 3.2f, 0.1f, floatArrayOf(0.85f, 0.92f, 0.98f, 0.8f))
                // Entrance canopy awning
                b.addBox(0f, 4.0f, depth * 0.5f + 1.2f, width * 0.85f, 0.25f, 2.2f, floatArrayOf(0.8f, 0.15f, 0.22f, 1f))

                // Neon rooftop advertising sign
                val neonColors = arrayOf(
                    floatArrayOf(0.95f, 0.2f, 0.65f, 1f), // Neon pink
                    floatArrayOf(0.2f, 0.95f, 0.75f, 1f), // Neon cyan
                    floatArrayOf(0.95f, 0.75f, 0.15f, 1f)  // Neon amber
                )
                val signColor = neonColors[random.nextInt(neonColors.size)]
                b.addBox(0f, height + 2.0f, depth * 0.45f, width * 0.7f, 2.8f, 0.3f, signColor)
            }

            BuildingStyle.INDUSTRIAL_FACTORY -> {
                // Brick and corrugated metal warehouse with smokestacks
                val brickColor = floatArrayOf(0.42f, 0.25f, 0.18f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, brickColor)

                // Metal loading bay shutter doors
                b.addBox(-width * 0.25f, 2.5f, depth * 0.5f + 0.08f, 4.5f, 4.0f, 0.15f, floatArrayOf(0.48f, 0.5f, 0.52f, 1f))
                b.addBox(width * 0.25f, 2.5f, depth * 0.5f + 0.08f, 4.5f, 4.0f, 0.15f, floatArrayOf(0.48f, 0.5f, 0.52f, 1f))

                // Tall industrial smokestacks
                b.addCylinder(-width * 0.35f, height + 6.0f, -depth * 0.25f, 1.2f, 12.0f, 6, floatArrayOf(0.35f, 0.34f, 0.36f, 1f))
                b.addCylinder(width * 0.35f, height + 6.0f, -depth * 0.25f, 1.2f, 12.0f, 6, floatArrayOf(0.35f, 0.34f, 0.36f, 1f))
            }

            BuildingStyle.RESIDENTIAL_APARTMENTS -> {
                // Classic city apartment building with fire escape balconies and windows
                val wallColor = floatArrayOf(0.48f, 0.35f, 0.28f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, wallColor)

                // Balconies and window bands
                val bands = 3
                val bandHeight = height / (bands * 2f)
                for (f in 0 until bands) {
                    val y = (f * 2 + 1) * bandHeight
                    b.addBox(0f, y, depth * 0.5f + 0.5f, width * 0.75f, 0.3f, 1.0f, floatArrayOf(0.2f, 0.2f, 0.2f, 1f))
                    b.addBox(0f, y + 1.0f, depth * 0.5f + 0.05f, width * 0.7f, 1.4f, 0.1f, floatArrayOf(0.95f, 0.9f, 0.55f, 0.9f))
                }

                // Rooftop water tower
                b.addCylinder(0f, height + 2.5f, 0f, 1.8f, 3.5f, 6, floatArrayOf(0.35f, 0.24f, 0.15f, 1f))
            }

            BuildingStyle.SUBURBAN_HOUSE -> {
                // Cozy residential house with pitched roof and garage
                val houseColor = floatArrayOf(0.72f, 0.68f, 0.6f, 1f)
                b.addBox(0f, height * 0.4f, 0f, width, height * 0.8f, depth, houseColor)

                // Sloped roof
                b.addBox(0f, height * 0.85f, 0f, width * 1.08f, height * 0.3f, depth * 1.08f, floatArrayOf(0.35f, 0.15f, 0.12f, 1f))

                // Front door and garage door
                b.addBox(-width * 0.22f, 1.4f, depth * 0.5f + 0.05f, 1.2f, 2.4f, 0.1f, floatArrayOf(0.3f, 0.15f, 0.1f, 1f))
                b.addBox(width * 0.22f, 1.6f, depth * 0.5f + 0.05f, 3.2f, 2.6f, 0.1f, floatArrayOf(0.85f, 0.85f, 0.88f, 1f))
            }

            BuildingStyle.OLD_TOWN_BRICK -> {
                // Historic architecture with stone trims and clock/ornamental parapet
                val stoneColor = floatArrayOf(0.55f, 0.48f, 0.42f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, stoneColor)

                // Arched ground floor colonnade
                b.addBox(0f, 2.0f, depth * 0.5f + 0.4f, width * 0.9f, 3.8f, 0.6f, floatArrayOf(0.42f, 0.38f, 0.34f, 1f))

                // Historic clock / peak ornament
                b.addBox(0f, height + 1.8f, depth * 0.45f, 4.0f, 3.2f, 1.0f, floatArrayOf(0.48f, 0.42f, 0.38f, 1f))
                b.addBox(0f, height + 1.8f, depth * 0.45f + 0.55f, 1.8f, 1.8f, 0.1f, floatArrayOf(0.95f, 0.95f, 0.9f, 1f)) // Clock face
            }

            BuildingStyle.WATERFRONT_WAREHOUSE -> {
                // Corrugated metal pier building with dock bollards
                val wharfColor = floatArrayOf(0.24f, 0.32f, 0.36f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, wharfColor)

                // Roll-up container bay
                b.addBox(0f, 3.0f, depth * 0.5f + 0.1f, width * 0.6f, 5.0f, 0.2f, floatArrayOf(0.72f, 0.42f, 0.15f, 1f))
                // Harbor crane fixture
                b.addBox(width * 0.4f, height + 3.0f, depth * 0.4f, 1.0f, 6.0f, 1.0f, floatArrayOf(0.9f, 0.75f, 0.1f, 1f))
            }

            BuildingStyle.AIRPORT_HANGAR -> {
                // Wide arched hangar structure
                val hangarColor = floatArrayOf(0.55f, 0.58f, 0.62f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, hangarColor)

                // Giant aircraft door
                b.addBox(0f, height * 0.45f, depth * 0.5f + 0.1f, width * 0.85f, height * 0.8f, 0.2f, floatArrayOf(0.3f, 0.34f, 0.38f, 1f))
            }

            BuildingStyle.HIGHWAY_STRUCTURE -> {
                // Elevated expressway support pylons and flyover deck
                val concreteColor = floatArrayOf(0.58f, 0.6f, 0.62f, 1f)
                b.addBox(0f, height * 0.5f, 0f, width, height, depth, concreteColor)
            }
        }

        return b.build()
    }
}
