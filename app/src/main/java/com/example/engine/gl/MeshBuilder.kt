package com.example.engine.gl

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MeshBuilder {
    private val vertices = ArrayList<Float>()
    private val normals = ArrayList<Float>()
    private val colors = ArrayList<Float>()
    private val indices = ArrayList<Short>()
    private var currentIndex: Short = 0

    fun clear() {
        vertices.clear()
        normals.clear()
        colors.clear()
        indices.clear()
        currentIndex = 0
    }

    fun addVertex(x: Float, y: Float, z: Float, nx: Float, ny: Float, nz: Float, r: Float, g: Float, b: Float, a: Float = 1f) {
        vertices.add(x)
        vertices.add(y)
        vertices.add(z)
        normals.add(nx)
        normals.add(ny)
        normals.add(nz)
        colors.add(r)
        colors.add(g)
        colors.add(b)
        colors.add(a)
    }

    fun addQuad(
        p0: FloatArray, p1: FloatArray, p2: FloatArray, p3: FloatArray,
        n: FloatArray,
        color: FloatArray
    ) {
        val base = currentIndex
        addVertex(p0[0], p0[1], p0[2], n[0], n[1], n[2], color[0], color[1], color[2], color[3])
        addVertex(p1[0], p1[1], p1[2], n[0], n[1], n[2], color[0], color[1], color[2], color[3])
        addVertex(p2[0], p2[1], p2[2], n[0], n[1], n[2], color[0], color[1], color[2], color[3])
        addVertex(p3[0], p3[1], p3[2], n[0], n[1], n[2], color[0], color[1], color[2], color[3])

        indices.add(base)
        indices.add((base + 1).toShort())
        indices.add((base + 2).toShort())

        indices.add(base)
        indices.add((base + 2).toShort())
        indices.add((base + 3).toShort())

        currentIndex = (currentIndex + 4).toShort()
    }

    fun addBox(
        cx: Float, cy: Float, cz: Float,
        w: Float, h: Float, d: Float,
        color: FloatArray
    ) {
        val hw = w * 0.5f
        val hh = h * 0.5f
        val hd = d * 0.5f

        // Front face (+Z)
        addQuad(
            floatArrayOf(cx - hw, cy - hh, cz + hd),
            floatArrayOf(cx + hw, cy - hh, cz + hd),
            floatArrayOf(cx + hw, cy + hh, cz + hd),
            floatArrayOf(cx - hw, cy + hh, cz + hd),
            floatArrayOf(0f, 0f, 1f),
            color
        )
        // Back face (-Z)
        addQuad(
            floatArrayOf(cx + hw, cy - hh, cz - hd),
            floatArrayOf(cx - hw, cy - hh, cz - hd),
            floatArrayOf(cx - hw, cy + hh, cz - hd),
            floatArrayOf(cx + hw, cy + hh, cz - hd),
            floatArrayOf(0f, 0f, -1f),
            color
        )
        // Top face (+Y)
        addQuad(
            floatArrayOf(cx - hw, cy + hh, cz + hd),
            floatArrayOf(cx + hw, cy + hh, cz + hd),
            floatArrayOf(cx + hw, cy + hh, cz - hd),
            floatArrayOf(cx - hw, cy + hh, cz - hd),
            floatArrayOf(0f, 1f, 0f),
            floatArrayOf(color[0] * 1.15f, color[1] * 1.15f, color[2] * 1.15f, color[3])
        )
        // Bottom face (-Y)
        addQuad(
            floatArrayOf(cx - hw, cy - hh, cz - hd),
            floatArrayOf(cx + hw, cy - hh, cz - hd),
            floatArrayOf(cx + hw, cy - hh, cz + hd),
            floatArrayOf(cx - hw, cy - hh, cz + hd),
            floatArrayOf(0f, -1f, 0f),
            floatArrayOf(color[0] * 0.7f, color[1] * 0.7f, color[2] * 0.7f, color[3])
        )
        // Right face (+X)
        addQuad(
            floatArrayOf(cx + hw, cy - hh, cz + hd),
            floatArrayOf(cx + hw, cy - hh, cz - hd),
            floatArrayOf(cx + hw, cy + hh, cz - hd),
            floatArrayOf(cx + hw, cy + hh, cz + hd),
            floatArrayOf(1f, 0f, 0f),
            floatArrayOf(color[0] * 0.9f, color[1] * 0.9f, color[2] * 0.9f, color[3])
        )
        // Left face (-X)
        addQuad(
            floatArrayOf(cx - hw, cy - hh, cz - hd),
            floatArrayOf(cx - hw, cy - hh, cz + hd),
            floatArrayOf(cx - hw, cy + hh, cz + hd),
            floatArrayOf(cx - hw, cy + hh, cz - hd),
            floatArrayOf(-1f, 0f, 0f),
            floatArrayOf(color[0] * 0.85f, color[1] * 0.85f, color[2] * 0.85f, color[3])
        )
    }

    fun addCylinder(
        cx: Float, cy: Float, cz: Float,
        radius: Float, height: Float, segments: Int = 8,
        color: FloatArray
    ) {
        val halfH = height * 0.5f
        val step = (2.0 * PI / segments).toFloat()

        for (i in 0 until segments) {
            val a0 = i * step
            val a1 = (i + 1) * step

            val x0 = cx + radius * cos(a0)
            val z0 = cz + radius * sin(a0)
            val x1 = cx + radius * cos(a1)
            val z1 = cz + radius * sin(a1)

            val nx0 = cos(a0)
            val nz0 = sin(a0)
            val nx1 = cos(a1)
            val nz1 = sin(a1)

            val base = currentIndex
            addVertex(x0, cy - halfH, z0, nx0, 0f, nz0, color[0], color[1], color[2], color[3])
            addVertex(x1, cy - halfH, z1, nx1, 0f, nz1, color[0], color[1], color[2], color[3])
            addVertex(x1, cy + halfH, z1, nx1, 0f, nz1, color[0], color[1], color[2], color[3])
            addVertex(x0, cy + halfH, z0, nx0, 0f, nz0, color[0], color[1], color[2], color[3])

            indices.add(base)
            indices.add((base + 1).toShort())
            indices.add((base + 2).toShort())
            indices.add(base)
            indices.add((base + 2).toShort())
            indices.add((base + 3).toShort())
            currentIndex = (currentIndex + 4).toShort()

            // Top cap
            val topBase = currentIndex
            addVertex(cx, cy + halfH, cz, 0f, 1f, 0f, color[0], color[1], color[2], color[3])
            addVertex(x0, cy + halfH, z0, 0f, 1f, 0f, color[0], color[1], color[2], color[3])
            addVertex(x1, cy + halfH, z1, 0f, 1f, 0f, color[0], color[1], color[2], color[3])
            indices.add(topBase)
            indices.add((topBase + 1).toShort())
            indices.add((topBase + 2).toShort())
            currentIndex = (currentIndex + 3).toShort()
        }
    }

    fun build(): Mesh {
        val vArray = vertices.toFloatArray()
        val nArray = normals.toFloatArray()
        val cArray = colors.toFloatArray()
        val iArray = indices.toShortArray()

        return Mesh(
            vertexCount = vertices.size / 3,
            vertexBuffer = Mesh.createFloatBuffer(vArray),
            normalBuffer = Mesh.createFloatBuffer(nArray),
            colorBuffer = Mesh.createFloatBuffer(cArray),
            indexBuffer = Mesh.createShortBuffer(iArray),
            indexCount = indices.size
        )
    }

    companion object {
        // Colors palette helper
        val COLOR_ROAD = floatArrayOf(0.18f, 0.19f, 0.21f, 1f)
        val COLOR_ROAD_MARKING = floatArrayOf(0.95f, 0.92f, 0.35f, 1f)
        val COLOR_CROSSWALK = floatArrayOf(0.95f, 0.95f, 0.98f, 1f)
        val COLOR_SIDEWALK = floatArrayOf(0.52f, 0.54f, 0.56f, 1f)
        val COLOR_GRASS = floatArrayOf(0.24f, 0.45f, 0.22f, 1f)
        val COLOR_WATER = floatArrayOf(0.08f, 0.28f, 0.46f, 0.9f)
        val COLOR_ASPHALT_WET = floatArrayOf(0.12f, 0.13f, 0.15f, 1f)

        fun createRoadTile(length: Float, width: Float, hasCrosswalk: Boolean = false): Mesh {
            val builder = MeshBuilder()
            val halfW = width * 0.5f
            val halfL = length * 0.5f

            // Asphalt surface
            builder.addBox(0f, 0f, 0f, width, 0.1f, length, COLOR_ROAD)

            // Sidewalk left & right
            val swWidth = 2.4f
            builder.addBox(-halfW - swWidth * 0.5f, 0.12f, 0f, swWidth, 0.24f, length, COLOR_SIDEWALK)
            builder.addBox(halfW + swWidth * 0.5f, 0.12f, 0f, swWidth, 0.24f, length, COLOR_SIDEWALK)

            // Center road dashed divider line
            val dashLength = 2.5f
            val gap = 2.0f
            var z = -halfL + dashLength * 0.5f
            while (z < halfL - dashLength * 0.5f) {
                builder.addBox(0f, 0.06f, z, 0.25f, 0.03f, dashLength, COLOR_ROAD_MARKING)
                z += dashLength + gap
            }

            // Crosswalk stripes if intersection
            if (hasCrosswalk) {
                val stripeW = 0.6f
                val stripeL = 3.0f
                for (i in -4..4) {
                    builder.addBox(i * 1.4f, 0.07f, halfL - 3.5f, stripeW, 0.03f, stripeL, COLOR_CROSSWALK)
                    builder.addBox(i * 1.4f, 0.07f, -halfL + 3.5f, stripeW, 0.03f, stripeL, COLOR_CROSSWALK)
                }
            }

            return builder.build()
        }

        fun createStreetLamp(): Mesh {
            val b = MeshBuilder()
            // Pole
            b.addCylinder(0f, 3.5f, 0f, 0.12f, 7.0f, 6, floatArrayOf(0.2f, 0.22f, 0.25f, 1f))
            // Arm
            b.addBox(0.8f, 7.0f, 0f, 1.6f, 0.15f, 0.15f, floatArrayOf(0.2f, 0.22f, 0.25f, 1f))
            // Lamp fixture & glowing light emitter
            b.addBox(1.5f, 6.9f, 0f, 0.5f, 0.2f, 0.35f, floatArrayOf(0.15f, 0.15f, 0.15f, 1f))
            b.addBox(1.5f, 6.75f, 0f, 0.35f, 0.1f, 0.25f, floatArrayOf(1.0f, 0.95f, 0.6f, 1f)) // Warm yellow emitter
            return b.build()
        }

        fun createTrafficLight(): Mesh {
            val b = MeshBuilder()
            // Vertical pole
            b.addCylinder(0f, 3.0f, 0f, 0.14f, 6.0f, 6, floatArrayOf(0.25f, 0.25f, 0.28f, 1f))
            // Signal housing
            b.addBox(0f, 5.2f, 0f, 0.6f, 1.6f, 0.5f, floatArrayOf(0.1f, 0.1f, 0.12f, 1f))
            // 3 lights: Red, Yellow, Green
            b.addBox(0f, 5.7f, 0.26f, 0.3f, 0.3f, 0.08f, floatArrayOf(0.95f, 0.15f, 0.15f, 1f))
            b.addBox(0f, 5.2f, 0.26f, 0.3f, 0.3f, 0.08f, floatArrayOf(0.95f, 0.85f, 0.15f, 1f))
            b.addBox(0f, 4.7f, 0.26f, 0.3f, 0.3f, 0.08f, floatArrayOf(0.15f, 0.95f, 0.35f, 1f))
            return b.build()
        }

        fun createTree(): Mesh {
            val b = MeshBuilder()
            // Trunk
            b.addCylinder(0f, 1.8f, 0f, 0.25f, 3.6f, 6, floatArrayOf(0.38f, 0.25f, 0.15f, 1f))
            // Foliage layers
            b.addCylinder(0f, 4.2f, 0f, 1.6f, 2.2f, 7, floatArrayOf(0.18f, 0.42f, 0.18f, 1f))
            b.addCylinder(0f, 5.6f, 0f, 1.1f, 1.8f, 6, floatArrayOf(0.24f, 0.52f, 0.22f, 1f))
            b.addCylinder(0f, 6.8f, 0f, 0.6f, 1.2f, 5, floatArrayOf(0.28f, 0.58f, 0.26f, 1f))
            return b.build()
        }
    }
}
