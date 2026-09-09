package com.example.engine.ecs

import com.example.engine.math.Vector3
import com.example.game.vehicle.VehicleType
import com.example.game.world.District
import java.util.concurrent.atomic.AtomicInteger

interface Component

data class TransformComponent(
    val position: Vector3 = Vector3(),
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var roll: Float = 0f,
    val scale: Vector3 = Vector3(1f, 1f, 1f)
) : Component

data class PhysicsComponent(
    val velocity: Vector3 = Vector3(),
    var angularVelocity: Float = 0f,
    var isGrounded: Boolean = true,
    var mass: Float = 1.0f,
    var collisionRadius: Float = 1.0f
) : Component

data class VehicleComponent(
    val type: VehicleType,
    var currentSpeed: Float = 0f,
    var steeringAngle: Float = 0f,
    var throttle: Float = 0f,
    var brake: Float = 0f,
    var handbrake: Boolean = false,
    var isPlayerDriven: Boolean = false,
    var engineRunning: Boolean = true,
    var headlightsOn: Boolean = false,
    var health: Float = 100f,
    var maxHealth: Float = 100f,
    var nitro: Float = 100f,
    var maxNitro: Float = 100f,
    var isNitroActive: Boolean = false,
    var underglowColor: Long = 0xFF00E5FF,
    var sirenActive: Boolean = false
) : Component {
    var nitroAmount: Float
        get() = nitro
        set(v) { nitro = v }
}

enum class PedestrianState {
    IDLE,
    WALKING,
    WAITING_CROSSING,
    FLEEING
}

data class PedestrianComponent(
    var state: PedestrianState = PedestrianState.WALKING,
    var walkSpeed: Float = 1.8f,
    var waypoints: List<Vector3> = emptyList(),
    var currentWaypointIndex: Int = 0,
    var waitTimer: Float = 0f,
    var outfitColor: FloatArray = floatArrayOf(0.2f, 0.4f, 0.6f, 1f)
) : Component

data class TrafficVehicleComponent(
    var roadIndex: Int = 0,
    var lane: Int = 0,
    var desiredSpeed: Float = 14f,
    var stopForRedLight: Boolean = false,
    var waitTimer: Float = 0f
) : Component

enum class InteractionType {
    VEHICLE_DOOR,
    MISSION_GIVER,
    CLOTHING_STORE,
    VEHICLE_GARAGE,
    SAFEHOUSE,
    NPC_TALK,
    TAXI_PASSENGER,
    STREET_RACE_MARKER,
    ATM_TERMINAL,
    REAL_ESTATE_SIGN
}

data class InteractiveComponent(
    val type: InteractionType,
    val promptText: String,
    val radius: Float = 3.5f,
    val payloadId: String = ""
) : Component

data class DistrictComponent(
    val district: District
) : Component

class Entity(val id: Int, var name: String = "Entity") {
    var isActive: Boolean = true
    val components = HashMap<Class<out Component>, Component>()

    inline fun <reified T : Component> get(): T? {
        return components[T::class.java] as? T
    }

    inline fun <reified T : Component> has(): Boolean {
        return components.containsKey(T::class.java)
    }

    fun add(component: Component): Entity {
        components[component.javaClass] = component
        return this
    }

    fun remove(clazz: Class<out Component>): Entity {
        components.remove(clazz)
        return this
    }
}

class EntityManager {
    private val nextId = AtomicInteger(1)
    val entities = ArrayList<Entity>()

    fun createEntity(name: String = "Entity"): Entity {
        val entity = Entity(nextId.getAndIncrement(), name)
        entities.add(entity)
        return entity
    }

    fun removeEntity(entity: Entity) {
        entity.isActive = false
        entities.remove(entity)
    }

    fun getAllEntities(): List<Entity> = entities

    inline fun <reified T : Component> getEntitiesWith(): List<Entity> {
        return entities.filter { it.isActive && it.has<T>() }
    }

    inline fun <reified T1 : Component, reified T2 : Component> getEntitiesWith2(): List<Entity> {
        return entities.filter { it.isActive && it.has<T1>() && it.has<T2>() }
    }

    fun clear() {
        entities.clear()
    }
}
