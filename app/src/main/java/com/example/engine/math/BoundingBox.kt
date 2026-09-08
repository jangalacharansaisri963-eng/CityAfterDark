package com.example.engine.math

data class BoundingBox(
    val min: Vector3 = Vector3(),
    val max: Vector3 = Vector3()
) {
    fun contains(point: Vector3): Boolean {
        return point.x in min.x..max.x &&
                point.y in min.y..max.y &&
                point.z in min.z..max.z
    }

    fun intersects(other: BoundingBox): Boolean {
        return (min.x <= other.max.x && max.x >= other.min.x) &&
                (min.y <= other.max.y && max.y >= other.min.y) &&
                (min.z <= other.max.z && max.z >= other.min.z)
    }

    fun intersectsSphere(center: Vector3, radius: Float): Boolean {
        var dmin = 0f
        if (center.x < min.x) dmin += (center.x - min.x) * (center.x - min.x)
        else if (center.x > max.x) dmin += (center.x - max.x) * (center.x - max.x)

        if (center.y < min.y) dmin += (center.y - min.y) * (center.y - min.y)
        else if (center.y > max.y) dmin += (center.y - max.y) * (center.y - max.y)

        if (center.z < min.z) dmin += (center.z - min.z) * (center.z - min.z)
        else if (center.z > max.z) dmin += (center.z - max.z) * (center.z - max.z)

        return dmin <= radius * radius
    }

    companion object {
        fun fromCenterAndExtents(center: Vector3, extents: Vector3): BoundingBox {
            return BoundingBox(
                min = Vector3(center.x - extents.x, center.y - extents.y, center.z - extents.z),
                max = Vector3(center.x + extents.x, center.y + extents.y, center.z + extents.z)
            )
        }
    }
}
