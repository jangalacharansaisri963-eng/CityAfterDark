package com.example.game.character

import com.example.engine.ecs.Entity
import com.example.engine.gl.CharacterMeshFactory
import com.example.engine.math.Vector3
import com.example.game.features.ConsumableType
import com.example.game.features.InventorySystem
import com.example.game.features.WeaponInventory
import com.example.game.world.StaticWorldObject
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

enum class PlayerState {
    IDLE,
    WALKING,
    RUNNING,
    SPRINTING,
    JUMPING,
    ROLLING,
    DRIVING
}

data class Outfit(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val jacketColor: FloatArray,
    val pantsColor: FloatArray,
    val shoesColor: FloatArray
)

class Player {
    var position: Vector3 = Vector3(82f, 0f, 42f) // Start right outside Safehouse
    var velocity: Vector3 = Vector3()
    var yaw: Float = 180f
    var state: PlayerState = PlayerState.IDLE

    var health: Float = 100f
    var maxHealth: Float = 100f
    var armor: Float = 100f
    var maxArmor: Float = 100f
    var stamina: Float = 100f
    var maxStamina: Float = 100f
    var focus: Float = 100f
    var maxFocus: Float = 100f
    var isFocusActive: Boolean = false

    var money: Int = 1250 // Initial starting cash

    var currentVehicle: Entity? = null
    var isInsideVehicle: Boolean = false

    var isSprinting: Boolean = false
    var isGrounded: Boolean = true
    var isRolling: Boolean = false
    var rollTimer: Float = 0f

    // Combat & Inventory
    val weapons = WeaponInventory()
    val inventory = InventorySystem()

    // Animation state
    var animTimer: Float = 0f
    var limbSwingAngle: Float = 0f

    // Wardrobe & meshes
    var currentOutfit: Outfit = OUTFITS[0]
    var characterParts: CharacterMeshFactory.CharacterParts = CharacterMeshFactory.createCharacterParts(
        jacketColor = currentOutfit.jacketColor,
        pantsColor = currentOutfit.pantsColor,
        shoesColor = currentOutfit.shoesColor
    )

    fun update(deltaTime: Float, inputX: Float, inputY: Float, cameraYaw: Float, obstacles: List<StaticWorldObject>) {
        if (isInsideVehicle) {
            state = PlayerState.DRIVING
            return
        }

        // Handle stamina recovery or consumption
        if (isSprinting && (inputX != 0f || inputY != 0f)) {
            stamina = (stamina - 18f * deltaTime).coerceAtLeast(0f)
            if (stamina <= 0f) isSprinting = false
        } else {
            stamina = (stamina + 22f * deltaTime).coerceAtMost(maxStamina)
        }

        // Determine speed based on input magnitude and sprint
        val inputMagnitude = kotlin.math.sqrt(inputX * inputX + inputY * inputY).coerceIn(0f, 1f)

        if (inputMagnitude > 0.08f) {
            // Camera relative movement vector
            val camRad = (cameraYaw) * (PI.toFloat() / 180f)
            // Forward is -sin(camRad), -cos(camRad) in OpenGL coordinate system
            val forwardX = sin(camRad)
            val forwardZ = -cos(camRad)
            val rightX = cos(camRad)
            val rightZ = sin(camRad)

            val moveX = rightX * inputX - forwardX * inputY
            val moveZ = rightZ * inputX - forwardZ * inputY

            val speed = when {
                !isGrounded -> 5.5f
                isSprinting -> 9.5f
                inputMagnitude > 0.6f -> 5.5f
                else -> 2.6f
            }

            val targetYaw = atan2(moveX, moveZ) * (180f / PI.toFloat())
            // Smoothly rotate character toward movement heading
            yaw = lerpAngle(yaw, targetYaw, 12f * deltaTime)

            // Predict next position
            val nextX = position.x + moveX * speed * deltaTime
            val nextZ = position.z + moveZ * speed * deltaTime

            // Simple collision resolution against nearby solid building bounds
            var canMoveX = true
            var canMoveZ = true
            val playerRadius = 0.6f

            for (obs in obstacles) {
                if (!obs.isCollidable) continue
                val dist = position.distanceToXZ(obs.position)
                if (dist > obs.boundsRadius + 3f) continue

                // Check X axis
                val testPosX = Vector3(nextX, 0f, position.z)
                if (testPosX.distanceToXZ(obs.position) < obs.boundsRadius + playerRadius) {
                    canMoveX = false
                }
                // Check Z axis
                val testPosZ = Vector3(position.x, 0f, nextZ)
                if (testPosZ.distanceToXZ(obs.position) < obs.boundsRadius + playerRadius) {
                    canMoveZ = false
                }
            }

            if (canMoveX) position.x = nextX
            if (canMoveZ) position.z = nextZ

            state = when {
                isRolling -> PlayerState.ROLLING
                !isGrounded -> PlayerState.JUMPING
                isSprinting -> PlayerState.SPRINTING
                inputMagnitude > 0.6f -> PlayerState.RUNNING
                else -> PlayerState.WALKING
            }

            // Animate limb stride
            val animFreq = if (isSprinting) 14f else 8f
            animTimer += deltaTime * animFreq
            val maxSwing = if (isSprinting) 45f else 28f
            limbSwingAngle = sin(animTimer) * maxSwing
        } else {
            if (isGrounded && !isRolling) {
                state = PlayerState.IDLE
                limbSwingAngle = 0f
            }
        }

        // Handle Dodge Roll Timer
        if (isRolling) {
            rollTimer -= deltaTime
            val camRad = yaw * (PI.toFloat() / 180f)
            position.x += sin(camRad) * 8f * deltaTime
            position.z += cos(camRad) * 8f * deltaTime
            if (rollTimer <= 0f) {
                isRolling = false
            }
        }

        // Focus meter regeneration or depletion
        if (isFocusActive) {
            focus = (focus - 30f * deltaTime).coerceAtLeast(0f)
            if (focus <= 0f) isFocusActive = false
        } else {
            focus = (focus + 12f * deltaTime).coerceAtMost(maxFocus)
        }

        // Handle Jump & Gravity
        if (!isGrounded) {
            velocity.y -= 22f * deltaTime // Gravity
            position.y += velocity.y * deltaTime
            if (position.y <= 0f) {
                position.y = 0f
                velocity.y = 0f
                isGrounded = true
                if (inputMagnitude > 0.08f) state = PlayerState.WALKING else state = PlayerState.IDLE
            }
        }
    }

    fun jump() {
        if (isGrounded && !isInsideVehicle && !isRolling) {
            velocity.y = 7.5f
            isGrounded = false
            state = PlayerState.JUMPING
        }
    }

    fun roll() {
        if (isGrounded && !isInsideVehicle && !isRolling && stamina >= 20f) {
            isRolling = true
            rollTimer = 0.45f
            stamina = (stamina - 20f).coerceAtLeast(0f)
            state = PlayerState.ROLLING
        }
    }

    fun toggleFocus() {
        if (focus > 15f || isFocusActive) {
            isFocusActive = !isFocusActive
        }
    }

    fun takeDamage(amount: Float) {
        if (isRolling) return // Invulnerable during combat roll!
        var remaining = amount
        if (armor > 0f) {
            if (armor >= remaining) {
                armor -= remaining
                remaining = 0f
            } else {
                remaining -= armor
                armor = 0f
            }
        }
        if (remaining > 0f) {
            health = (health - remaining).coerceAtLeast(0f)
        }
    }

    fun heal(amount: Float) {
        health = (health + amount).coerceAtMost(maxHealth)
    }

    fun addArmor(amount: Float) {
        armor = (armor + amount).coerceAtMost(maxArmor)
    }

    fun useConsumable(type: ConsumableType): Boolean {
        if (inventory.useItem(type)) {
            when (type) {
                ConsumableType.DAN_COLA -> {
                    stamina = (stamina + 45f).coerceAtMost(maxStamina)
                }
                ConsumableType.FIRST_AID -> {
                    heal(60f)
                }
                ConsumableType.ARMOR_PLATE -> {
                    addArmor(100f)
                }
                ConsumableType.ADRENALINE -> {
                    focus = maxFocus
                }
            }
            return true
        }
        return false
    }

    fun useConsumable(id: String): Boolean {
        val type = ConsumableType.values().find { it.id == id } ?: return false
        return useConsumable(type)
    }

    fun setOutfit(outfit: Outfit) {
        currentOutfit = outfit
        characterParts = CharacterMeshFactory.createCharacterParts(
            jacketColor = outfit.jacketColor,
            pantsColor = outfit.pantsColor,
            shoesColor = outfit.shoesColor
        )
    }

    private fun lerpAngle(from: Float, to: Float, step: Float): Float {
        var diff = (to - from) % 360f
        if (diff < -180f) diff += 360f
        if (diff > 180f) diff -= 360f
        return from + diff * step.coerceIn(0f, 1f)
    }

    companion object {
        val OUTFITS = listOf(
            Outfit(
                id = "outfit_stealth",
                name = "Midnight Runner",
                description = "Dark tactical hoodie with reinforced denim pants.",
                price = 0, // Default starting outfit
                jacketColor = floatArrayOf(0.12f, 0.14f, 0.18f, 1f),
                pantsColor = floatArrayOf(0.1f, 0.1f, 0.12f, 1f),
                shoesColor = floatArrayOf(0.2f, 0.2f, 0.22f, 1f)
            ),
            Outfit(
                id = "outfit_neon",
                name = "Cyber Syndicate",
                description = "High-contrast electric cyan jacket with metallic accents.",
                price = 650,
                jacketColor = floatArrayOf(0.08f, 0.85f, 0.95f, 1f),
                pantsColor = floatArrayOf(0.12f, 0.12f, 0.16f, 1f),
                shoesColor = floatArrayOf(0.08f, 0.85f, 0.95f, 1f)
            ),
            Outfit(
                id = "outfit_executive",
                name = "Downtown Executive",
                description = "Tailored charcoal business suit with pristine white collar.",
                price = 1800,
                jacketColor = floatArrayOf(0.22f, 0.24f, 0.28f, 1f),
                pantsColor = floatArrayOf(0.22f, 0.24f, 0.28f, 1f),
                shoesColor = floatArrayOf(0.1f, 0.08f, 0.06f, 1f)
            ),
            Outfit(
                id = "outfit_brawler",
                name = "Street Racer Crimson",
                description = "Crimson leather biker jacket with padded shoulders.",
                price = 1200,
                jacketColor = floatArrayOf(0.85f, 0.15f, 0.18f, 1f),
                pantsColor = floatArrayOf(0.14f, 0.14f, 0.14f, 1f),
                shoesColor = floatArrayOf(0.9f, 0.9f, 0.9f, 1f)
            ),
            Outfit(
                id = "outfit_golden",
                name = "Sunset Sovereign",
                description = "Exclusive golden silk bomber jacket reserved for city royalty.",
                price = 4500,
                jacketColor = floatArrayOf(0.95f, 0.78f, 0.15f, 1f),
                pantsColor = floatArrayOf(0.08f, 0.08f, 0.1f, 1f),
                shoesColor = floatArrayOf(0.95f, 0.78f, 0.15f, 1f)
            ),
            Outfit(
                id = "outfit_ghost_recon",
                name = "Ghost Recon Spec-Ops",
                description = "Urban tactical camouflage combat armor with night-vision harness.",
                price = 3200,
                jacketColor = floatArrayOf(0.18f, 0.22f, 0.20f, 1f),
                pantsColor = floatArrayOf(0.15f, 0.18f, 0.16f, 1f),
                shoesColor = floatArrayOf(0.08f, 0.09f, 0.08f, 1f)
            ),
            Outfit(
                id = "outfit_hyper_ronin",
                name = "Hyper Neon Ronin",
                description = "Vibrant magenta and violet synthetic weave with holographic trims.",
                price = 6000,
                jacketColor = floatArrayOf(0.92f, 0.10f, 0.55f, 1f),
                pantsColor = floatArrayOf(0.25f, 0.05f, 0.35f, 1f),
                shoesColor = floatArrayOf(0.92f, 0.10f, 0.55f, 1f)
            )
        )
    }
}
