package com.example.engine.gl

object CharacterMeshFactory {

    data class CharacterParts(
        val headMesh: Mesh,
        val torsoMesh: Mesh,
        val leftArmMesh: Mesh,
        val rightArmMesh: Mesh,
        val leftLegMesh: Mesh,
        val rightLegMesh: Mesh
    )

    fun createCharacterParts(
        skinColor: FloatArray = floatArrayOf(0.92f, 0.76f, 0.64f, 1f),
        hairColor: FloatArray = floatArrayOf(0.15f, 0.12f, 0.1f, 1f),
        jacketColor: FloatArray = floatArrayOf(0.18f, 0.22f, 0.28f, 1f),
        pantsColor: FloatArray = floatArrayOf(0.12f, 0.14f, 0.18f, 1f),
        shoesColor: FloatArray = floatArrayOf(0.85f, 0.85f, 0.88f, 1f)
    ): CharacterParts {

        // Head + Hair
        val headBuilder = MeshBuilder()
        headBuilder.addBox(0f, 0.18f, 0f, 0.28f, 0.32f, 0.28f, skinColor)
        // Hair cap
        headBuilder.addBox(0f, 0.32f, -0.02f, 0.3f, 0.12f, 0.3f, hairColor)
        // Eyes
        headBuilder.addBox(-0.06f, 0.2f, 0.145f, 0.05f, 0.04f, 0.02f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))
        headBuilder.addBox(0.06f, 0.2f, 0.145f, 0.05f, 0.04f, 0.02f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))
        val headMesh = headBuilder.build()

        // Torso + Jacket / Shirt
        val torsoBuilder = MeshBuilder()
        torsoBuilder.addBox(0f, 0.35f, 0f, 0.44f, 0.65f, 0.26f, jacketColor)
        // Belt
        torsoBuilder.addBox(0f, 0.04f, 0f, 0.45f, 0.08f, 0.27f, floatArrayOf(0.1f, 0.1f, 0.1f, 1f))
        // Collar / Undershirt
        torsoBuilder.addBox(0f, 0.6f, 0.12f, 0.16f, 0.15f, 0.05f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
        val torsoMesh = torsoBuilder.build()

        // Left Arm (pivot at shoulder (0, 0, 0), hangs down to -0.55)
        val leftArmBuilder = MeshBuilder()
        leftArmBuilder.addBox(0f, -0.28f, 0f, 0.14f, 0.55f, 0.14f, jacketColor)
        // Hand
        leftArmBuilder.addBox(0f, -0.6f, 0f, 0.11f, 0.15f, 0.11f, skinColor)
        val leftArmMesh = leftArmBuilder.build()

        // Right Arm
        val rightArmBuilder = MeshBuilder()
        rightArmBuilder.addBox(0f, -0.28f, 0f, 0.14f, 0.55f, 0.14f, jacketColor)
        rightArmBuilder.addBox(0f, -0.6f, 0f, 0.11f, 0.15f, 0.11f, skinColor)
        val rightArmMesh = rightArmBuilder.build()

        // Left Leg (pivot at hip (0, 0, 0), hangs down to -0.75)
        val leftLegBuilder = MeshBuilder()
        leftLegBuilder.addBox(0f, -0.35f, 0f, 0.18f, 0.68f, 0.18f, pantsColor)
        // Shoe
        leftLegBuilder.addBox(0f, -0.74f, 0.04f, 0.19f, 0.12f, 0.28f, shoesColor)
        val leftLegMesh = leftLegBuilder.build()

        // Right Leg
        val rightLegBuilder = MeshBuilder()
        rightLegBuilder.addBox(0f, -0.35f, 0f, 0.18f, 0.68f, 0.18f, pantsColor)
        rightLegBuilder.addBox(0f, -0.74f, 0.04f, 0.19f, 0.12f, 0.28f, shoesColor)
        val rightLegMesh = rightLegBuilder.build()

        return CharacterParts(
            headMesh = headMesh,
            torsoMesh = torsoMesh,
            leftArmMesh = leftArmMesh,
            rightArmMesh = rightArmMesh,
            leftLegMesh = leftLegMesh,
            rightLegMesh = rightLegMesh
        )
    }
}
