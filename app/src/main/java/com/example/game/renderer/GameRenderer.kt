package com.example.game.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.PedestrianComponent
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.gl.Mesh
import com.example.engine.gl.MeshBuilder
import com.example.engine.gl.Shader
import com.example.engine.gl.VehicleMeshFactory
import com.example.engine.math.Matrix4
import com.example.engine.math.Vector3
import com.example.game.camera.ThirdPersonCamera
import com.example.game.character.Player
import com.example.game.character.PlayerState
import com.example.game.missions.MissionManager
import com.example.game.simulation.DayNightSystem
import com.example.game.vehicle.VehicleType
import com.example.game.world.CityData
import com.example.game.world.StaticWorldObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

class GameRenderer(
    val player: Player,
    val camera: ThirdPersonCamera,
    val entityManager: EntityManager,
    val cityData: CityData,
    val dayNightSystem: DayNightSystem,
    val missionManager: MissionManager
) : GLSurfaceView.Renderer {

    private var shader: Shader? = null
    private val vehicleMeshMap = HashMap<VehicleType, Mesh>()
    private var waypointMarkerMesh: Mesh? = null
    private var markerRotation = 0f

    // Graphics Settings
    var viewDistance: Float = 260f
    var renderShadows: Boolean = true

    // Temporary transformation matrices to avoid allocations per frame
    private val modelMatrix = Matrix4()
    private val mvpMatrix = Matrix4()
    private val normalMatrix = Matrix4()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // Compile standard world shader
        shader = Shader(Shader.STANDARD_VERTEX_SHADER, Shader.STANDARD_FRAGMENT_SHADER)

        // Pre-build 3D vehicle models for all 9 vehicle types
        for (vType in VehicleType.values()) {
            vehicleMeshMap[vType] = VehicleMeshFactory.createVehicleMesh(vType)
        }

        // Floating 3D mission waypoint marker (diamond beacon)
        val mb = MeshBuilder()
        val yellowBeacon = floatArrayOf(0.98f, 0.85f, 0.15f, 0.95f)
        mb.addBox(0f, 0f, 0f, 1.8f, 1.8f, 1.8f, yellowBeacon)
        mb.addCylinder(0f, -4f, 0f, 0.15f, 8f, 6, floatArrayOf(1f, 0.9f, 0.2f, 0.6f))
        waypointMarkerMesh = mb.build()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        camera.onResize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val sh = shader ?: return

        // Sky / Clear color from DayNight system
        val fog = dayNightSystem.fogColor
        GLES20.glClearColor(fog[0], fog[1], fog[2], 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        sh.use()

        // Set global lighting uniforms
        val sunDir = dayNightSystem.sunDirection
        GLES20.glUniform3f(sh.uLightDir, sunDir.x, sunDir.y, sunDir.z)
        GLES20.glUniform3fv(sh.uLightColor, 1, dayNightSystem.sunColor, 0)
        GLES20.glUniform3fv(sh.uAmbientColor, 1, dayNightSystem.ambientColor, 0)
        GLES20.glUniform3fv(sh.uFogColor, 1, dayNightSystem.fogColor, 0)

        GLES20.glUniform1f(sh.uFogDensity, dayNightSystem.fogDensity)
        GLES20.glUniform1f(sh.uFogNear, dayNightSystem.fogNear)
        GLES20.glUniform1f(sh.uFogFar, viewDistance)
        GLES20.glUniform1f(sh.uWetness, dayNightSystem.wetness)
        GLES20.glUniform1f(sh.uEmissiveFactor, if (dayNightSystem.areCityLightsOn) dayNightSystem.emissiveMultiplier else 0.1f)

        val camPos = camera.currentPosition
        GLES20.glUniform3f(sh.uViewPos, camPos.x, camPos.y, camPos.z)

        val camTarget = camera.targetPosition

        // 1. Render Road Tiles
        for (road in cityData.roadMeshes) {
            val dist = camTarget.distanceToXZ(road.position)
            if (dist > viewDistance) continue
            drawStaticObject(sh, road)
        }

        // 2. Render Static Buildings & District Architecture
        for (obj in cityData.staticObjects) {
            val dist = camTarget.distanceToXZ(obj.position)
            if (dist > viewDistance + obj.boundsRadius) continue
            drawStaticObject(sh, obj)
        }

        // 3. Render Street Furniture (Lamps, Traffic lights, Trees)
        for (prop in cityData.propMeshes) {
            val dist = camTarget.distanceToXZ(prop.position)
            if (dist > viewDistance * 0.65f) continue
            drawStaticObject(sh, prop)
        }

        // 4. Render All Vehicles (Player vehicle, Traffic, Parked)
        val vehicles = entityManager.getEntitiesWith2<VehicleComponent, TransformComponent>()
        for (vEntity in vehicles) {
            val trans = vEntity.get<TransformComponent>() ?: continue
            val veh = vEntity.get<VehicleComponent>() ?: continue

            val dist = camTarget.distanceToXZ(trans.position)
            if (dist > viewDistance * 0.85f) continue

            val mesh = vehicleMeshMap[veh.type] ?: continue
            drawTransformedMesh(sh, mesh, trans.position, trans.yaw, 0f, 0f, trans.scale)
        }

        // 5. Render Pedestrians
        val pedestrians = entityManager.getEntitiesWith2<PedestrianComponent, TransformComponent>()
        for (pEntity in pedestrians) {
            val trans = pEntity.get<TransformComponent>() ?: continue
            val ped = pEntity.get<PedestrianComponent>() ?: continue

            val dist = camTarget.distanceToXZ(trans.position)
            if (dist > viewDistance * 0.45f) continue

            // Render humanoid character
            renderHumanoid(sh, trans.position, trans.yaw, 0f, false)
        }

        // 6. Render Player Character (when on foot)
        if (!player.isInsideVehicle) {
            val isSprinting = player.state == PlayerState.SPRINTING
            val isMoving = player.state != PlayerState.IDLE
            renderPlayer(sh, player.position, player.yaw, player.limbSwingAngle, isMoving, isSprinting)
        }

        // 7. Render 3D Mission Waypoint Marker
        markerRotation = (markerRotation + 1.2f) % 360f
        val currentObj = missionManager.getCurrentObjective()
        if (currentObj != null && waypointMarkerMesh != null) {
            val targetPos = currentObj.targetPosition
            val bobbingY = 3.5f + sin(markerRotation * 0.05f) * 0.6f
            val markerPos = Vector3(targetPos.x, bobbingY, targetPos.z)
            GLES20.glUniform1f(sh.uEmissiveFactor, 2.5f) // Glowing beacon
            drawTransformedMesh(
                sh,
                waypointMarkerMesh!!,
                markerPos,
                markerRotation,
                45f,
                markerRotation * 0.5f,
                Vector3(1f, 1f, 1f)
            )
        }
    }

    private fun drawStaticObject(sh: Shader, obj: StaticWorldObject) {
        modelMatrix.identity()
            .translate(obj.position)
            .rotate(obj.rotationY, 0f, 1f, 0f)
            .scale(obj.scale.x, obj.scale.y, obj.scale.z)

        mvpMatrix.set(camera.projectionMatrix)
            .multiply(camera.viewMatrix)
            .multiply(modelMatrix)

        GLES20.glUniformMatrix4fv(sh.uModelMatrix, 1, false, modelMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uMVPMatrix, 1, false, mvpMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uNormalMatrix, 1, false, modelMatrix.values, 0)

        obj.mesh.render(sh)
    }

    private fun drawTransformedMesh(
        sh: Shader,
        mesh: Mesh,
        pos: Vector3,
        yaw: Float,
        pitch: Float = 0f,
        roll: Float = 0f,
        scale: Vector3 = Vector3(1f, 1f, 1f)
    ) {
        modelMatrix.identity()
            .translate(pos)
            .rotate(yaw, 0f, 1f, 0f)
            .rotate(pitch, 1f, 0f, 0f)
            .rotate(roll, 0f, 0f, 1f)
            .scale(scale.x, scale.y, scale.z)

        mvpMatrix.set(camera.projectionMatrix)
            .multiply(camera.viewMatrix)
            .multiply(modelMatrix)

        GLES20.glUniformMatrix4fv(sh.uModelMatrix, 1, false, modelMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uMVPMatrix, 1, false, mvpMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uNormalMatrix, 1, false, modelMatrix.values, 0)

        mesh.render(sh)
    }

    private fun renderPlayer(
        sh: Shader,
        pos: Vector3,
        yaw: Float,
        swingAngle: Float,
        isMoving: Boolean,
        isSprinting: Boolean
    ) {
        val parts = player.characterParts

        // Base root transform (Y=0.74 places feet on ground)
        val rootPos = Vector3(pos.x, pos.y + 0.74f, pos.z)
        val leanAngle = if (isSprinting) 12f else 0f

        // Torso
        drawTransformedMesh(sh, parts.torsoMesh, rootPos, yaw, leanAngle)

        // Head
        val headPos = Vector3(pos.x, pos.y + 1.42f, pos.z)
        drawTransformedMesh(sh, parts.headMesh, headPos, yaw, leanAngle)

        // Left Arm (swings opposite to left leg)
        val armSwing = if (isMoving) -swingAngle else 0f
        val leftShoulder = Vector3(pos.x, pos.y + 1.35f, pos.z)
        drawHierarchicalPart(sh, parts.leftArmMesh, leftShoulder, yaw, armSwing, -0.28f)

        // Right Arm
        val rightShoulder = Vector3(pos.x, pos.y + 1.35f, pos.z)
        drawHierarchicalPart(sh, parts.rightArmMesh, rightShoulder, yaw, -armSwing, 0.28f)

        // Left Leg
        val legSwing = if (isMoving) swingAngle else 0f
        val leftHip = Vector3(pos.x, pos.y + 0.75f, pos.z)
        drawHierarchicalPart(sh, parts.leftLegMesh, leftHip, yaw, legSwing, -0.14f)

        // Right Leg
        val rightHip = Vector3(pos.x, pos.y + 0.75f, pos.z)
        drawHierarchicalPart(sh, parts.rightLegMesh, rightHip, yaw, -legSwing, 0.14f)
    }

    private fun renderHumanoid(
        sh: Shader,
        pos: Vector3,
        yaw: Float,
        swingAngle: Float,
        isMoving: Boolean
    ) {
        val parts = player.characterParts
        val rootPos = Vector3(pos.x, pos.y + 0.74f, pos.z)
        drawTransformedMesh(sh, parts.torsoMesh, rootPos, yaw)
        val headPos = Vector3(pos.x, pos.y + 1.42f, pos.z)
        drawTransformedMesh(sh, parts.headMesh, headPos, yaw)
        val leftShoulder = Vector3(pos.x, pos.y + 1.35f, pos.z)
        drawHierarchicalPart(sh, parts.leftArmMesh, leftShoulder, yaw, 0f, -0.28f)
        val rightShoulder = Vector3(pos.x, pos.y + 1.35f, pos.z)
        drawHierarchicalPart(sh, parts.rightArmMesh, rightShoulder, yaw, 0f, 0.28f)
        val leftHip = Vector3(pos.x, pos.y + 0.75f, pos.z)
        drawHierarchicalPart(sh, parts.leftLegMesh, leftHip, yaw, 0f, -0.14f)
        val rightHip = Vector3(pos.x, pos.y + 0.75f, pos.z)
        drawHierarchicalPart(sh, parts.rightLegMesh, rightHip, yaw, 0f, 0.14f)
    }

    private fun drawHierarchicalPart(
        sh: Shader,
        mesh: Mesh,
        pivot: Vector3,
        rootYaw: Float,
        swingPitch: Float,
        lateralOffset: Float
    ) {
        modelMatrix.identity()
            .translate(pivot)
            .rotate(rootYaw, 0f, 1f, 0f)
            .translate(lateralOffset, 0f, 0f)
            .rotate(swingPitch, 1f, 0f, 0f)

        mvpMatrix.set(camera.projectionMatrix)
            .multiply(camera.viewMatrix)
            .multiply(modelMatrix)

        GLES20.glUniformMatrix4fv(sh.uModelMatrix, 1, false, modelMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uMVPMatrix, 1, false, mvpMatrix.values, 0)
        GLES20.glUniformMatrix4fv(sh.uNormalMatrix, 1, false, modelMatrix.values, 0)

        mesh.render(sh)
    }
}
