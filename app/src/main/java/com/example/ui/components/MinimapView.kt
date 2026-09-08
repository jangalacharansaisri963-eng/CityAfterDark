package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.math.Vector3
import com.example.game.world.District
import com.example.game.world.RoadNetwork
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MinimapView(
    playerPos: Vector3,
    playerYaw: Float,
    roadNetwork: RoadNetwork,
    entityManager: EntityManager,
    currentDistrict: District,
    targetPos: Vector3?,
    onOpenFullMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color(0xE610141C))
            .border(2.dp, Color(currentDistrict.minimapColor), CircleShape)
            .clickable { onOpenFullMap() }
            .testTag("hud_minimap"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val mapScale = 0.45f // Scale factor: pixels per meter

            // Draw roads relative to player position
            for (road in roadNetwork.roads) {
                val relStartX = (road.start.x - playerPos.x) * mapScale
                val relStartZ = (road.start.z - playerPos.z) * mapScale
                val relEndX = (road.end.x - playerPos.x) * mapScale
                val relEndZ = (road.end.z - playerPos.z) * mapScale

                // OpenGL Z is forward/back, map Y is screen down
                drawLine(
                    color = Color(0x6690A4AE),
                    start = Offset(center.x + relStartX, center.y + relStartZ),
                    end = Offset(center.x + relEndX, center.y + relEndZ),
                    strokeWidth = 4f
                )
            }

            // Draw nearby vehicles as small cyan dots
            val vehicles = entityManager.getEntitiesWith2<VehicleComponent, TransformComponent>()
            for (v in vehicles) {
                val trans = v.get<TransformComponent>() ?: continue
                val rx = (trans.position.x - playerPos.x) * mapScale
                val rz = (trans.position.z - playerPos.z) * mapScale
                val distSq = rx * rx + rz * rz
                if (distSq < (size.width / 2f) * (size.width / 2f)) {
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = 2.5f,
                        center = Offset(center.x + rx, center.y + rz)
                    )
                }
            }

            // Draw Mission Objective target marker (yellow pulsing dot)
            if (targetPos != null) {
                val tx = (targetPos.x - playerPos.x) * mapScale
                val tz = (targetPos.z - playerPos.z) * mapScale
                val targetDist = kotlin.math.sqrt(tx * tx + tz * tz)
                val maxRadius = size.width / 2f - 8f

                val drawOffset = if (targetDist > maxRadius) {
                    // Clamp to perimeter of minimap
                    val angle = kotlin.math.atan2(tz, tx)
                    Offset(center.x + cos(angle) * maxRadius, center.y + sin(angle) * maxRadius)
                } else {
                    Offset(center.x + tx, center.y + tz)
                }

                drawCircle(
                    color = Color(0xFFFFD600),
                    radius = 5.5f,
                    center = drawOffset
                )
            }

            // Draw Player Arrow at center
            rotate(degrees = -playerYaw, pivot = center) {
                val triangle = Path().apply {
                    moveTo(center.x, center.y - 8f)
                    lineTo(center.x - 5f, center.y + 7f)
                    lineTo(center.x, center.y + 4f)
                    lineTo(center.x + 5f, center.y + 7f)
                    close()
                }
                drawPath(triangle, Color(0xFF00E676))
            }
        }

        // District title watermark at top of minimap
        Text(
            text = currentDistrict.title.take(10).uppercase(),
            color = Color(currentDistrict.minimapColor),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
