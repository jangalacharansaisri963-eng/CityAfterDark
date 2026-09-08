package com.example.game.world

import com.example.engine.math.Vector3

data class RoadSegment(
    val id: Int,
    val start: Vector3,
    val end: Vector3,
    val width: Float = 14f,
    val lanes: Int = 2,
    val isHighway: Boolean = false
) {
    val length: Float = start.distanceToXZ(end)
    val direction: Vector3 = end.sub(start).normalized()
}

enum class TrafficLightState {
    GREEN,
    YELLOW,
    RED
}

class Intersection(
    val id: Int,
    val center: Vector3,
    val radius: Float = 12f
) {
    var stateNorthSouth: TrafficLightState = TrafficLightState.GREEN
    var stateEastWest: TrafficLightState = TrafficLightState.RED
    var timer: Float = 0f

    fun update(deltaTime: Float) {
        timer += deltaTime
        when {
            timer < 8.0f -> {
                stateNorthSouth = TrafficLightState.GREEN
                stateEastWest = TrafficLightState.RED
            }
            timer < 10.0f -> {
                stateNorthSouth = TrafficLightState.YELLOW
                stateEastWest = TrafficLightState.RED
            }
            timer < 18.0f -> {
                stateNorthSouth = TrafficLightState.RED
                stateEastWest = TrafficLightState.GREEN
            }
            timer < 20.0f -> {
                stateNorthSouth = TrafficLightState.RED
                stateEastWest = TrafficLightState.YELLOW
            }
            else -> {
                timer = 0f
            }
        }
    }
}

class RoadNetwork {
    val roads = ArrayList<RoadSegment>()
    val intersections = ArrayList<Intersection>()

    fun getClosestRoad(point: Vector3): RoadSegment? {
        if (roads.isEmpty()) return null
        return roads.minByOrNull { road ->
            // Approximate distance to line segment
            point.distanceToXZ(road.start.lerp(road.end, 0.5f))
        }
    }

    fun update(deltaTime: Float) {
        for (i in intersections) {
            i.update(deltaTime)
        }
    }
}
