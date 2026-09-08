package com.example.game.world

import com.example.engine.ecs.DistrictComponent
import com.example.engine.ecs.EntityManager
import com.example.engine.ecs.InteractiveComponent
import com.example.engine.ecs.InteractionType
import com.example.engine.ecs.PhysicsComponent
import com.example.engine.ecs.TransformComponent
import com.example.engine.ecs.VehicleComponent
import com.example.engine.gl.BuildingMeshFactory
import com.example.engine.gl.Mesh
import com.example.engine.gl.MeshBuilder
import com.example.engine.math.Vector3
import com.example.game.vehicle.VehicleType
import kotlin.random.Random

data class StaticWorldObject(
    val mesh: Mesh,
    val position: Vector3,
    val rotationY: Float = 0f,
    val scale: Vector3 = Vector3(1f, 1f, 1f),
    val district: District = District.DOWNTOWN,
    val isCollidable: Boolean = true,
    val boundsRadius: Float = 10f
)

class CityData(
    val roadNetwork: RoadNetwork,
    val staticObjects: List<StaticWorldObject>,
    val roadMeshes: List<StaticWorldObject>,
    val propMeshes: List<StaticWorldObject>
)

object CityBuilder {

    fun buildCity(entityManager: EntityManager): CityData {
        val roadNetwork = RoadNetwork()
        val staticObjects = ArrayList<StaticWorldObject>()
        val roadMeshes = ArrayList<StaticWorldObject>()
        val propMeshes = ArrayList<StaticWorldObject>()

        val streetLampMesh = MeshBuilder.createStreetLamp()
        val trafficLightMesh = MeshBuilder.createTrafficLight()
        val treeMesh = MeshBuilder.createTree()

        // Generate Grid Road Network (E-W roads and N-S roads)
        // Main grid lines at intervals of 80m from -400 to +400
        val gridCoords = intArrayOf(-320, -240, -160, -80, 0, 80, 160, 240, 320)
        var roadId = 1
        var intersectionId = 1

        // Intersections
        for (gx in gridCoords) {
            for (gz in gridCoords) {
                roadNetwork.intersections.add(
                    Intersection(
                        id = intersectionId++,
                        center = Vector3(gx.toFloat(), 0f, gz.toFloat())
                    )
                )

                // Traffic lights at intersections
                propMeshes.add(
                    StaticWorldObject(
                        mesh = trafficLightMesh,
                        position = Vector3(gx - 7f, 0f, gz + 7f),
                        rotationY = 0f,
                        district = getDistrictAt(gx.toFloat(), gz.toFloat()),
                        isCollidable = false,
                        boundsRadius = 2f
                    )
                )
            }
        }

        // Horizontal road segments (East-West)
        val roadTileMesh = MeshBuilder.createRoadTile(80f, 14f, true)
        for (gz in gridCoords) {
            for (i in 0 until gridCoords.size - 1) {
                val x0 = gridCoords[i].toFloat()
                val x1 = gridCoords[i + 1].toFloat()
                val midX = (x0 + x1) * 0.5f

                roadNetwork.roads.add(
                    RoadSegment(
                        id = roadId++,
                        start = Vector3(x0, 0f, gz.toFloat()),
                        end = Vector3(x1, 0f, gz.toFloat())
                    )
                )

                roadMeshes.add(
                    StaticWorldObject(
                        mesh = roadTileMesh,
                        position = Vector3(midX, 0f, gz.toFloat()),
                        rotationY = 90f,
                        district = getDistrictAt(midX, gz.toFloat()),
                        isCollidable = false,
                        boundsRadius = 40f
                    )
                )

                // Street lamps along road
                propMeshes.add(
                    StaticWorldObject(
                        mesh = streetLampMesh,
                        position = Vector3(midX, 0f, gz.toFloat() + 8.5f),
                        rotationY = 180f,
                        isCollidable = false,
                        boundsRadius = 2f
                    )
                )
                propMeshes.add(
                    StaticWorldObject(
                        mesh = streetLampMesh,
                        position = Vector3(midX, 0f, gz.toFloat() - 8.5f),
                        rotationY = 0f,
                        isCollidable = false,
                        boundsRadius = 2f
                    )
                )
            }
        }

        // Vertical road segments (North-South)
        for (gx in gridCoords) {
            for (i in 0 until gridCoords.size - 1) {
                val z0 = gridCoords[i].toFloat()
                val z1 = gridCoords[i + 1].toFloat()
                val midZ = (z0 + z1) * 0.5f

                roadNetwork.roads.add(
                    RoadSegment(
                        id = roadId++,
                        start = Vector3(gx.toFloat(), 0f, z0),
                        end = Vector3(gx.toFloat(), 0f, z1)
                    )
                )

                roadMeshes.add(
                    StaticWorldObject(
                        mesh = roadTileMesh,
                        position = Vector3(gx.toFloat(), 0f, midZ),
                        rotationY = 0f,
                        district = getDistrictAt(gx.toFloat(), midZ),
                        isCollidable = false,
                        boundsRadius = 40f
                    )
                )

                // Trees along sidewalk
                propMeshes.add(
                    StaticWorldObject(
                        mesh = treeMesh,
                        position = Vector3(gx.toFloat() + 8.5f, 0f, midZ),
                        isCollidable = false,
                        boundsRadius = 2f
                    )
                )
            }
        }

        // Cache archetype building meshes to minimize memory and load instantly
        val buildingMeshCache = HashMap<String, Mesh>()

        // Generate City Blocks & District Buildings
        val random = Random(42L)
        for (i in 0 until gridCoords.size - 1) {
            for (j in 0 until gridCoords.size - 1) {
                val minX = gridCoords[i].toFloat() + 10f
                val maxX = gridCoords[i + 1].toFloat() - 10f
                val minZ = gridCoords[j].toFloat() + 10f
                val maxZ = gridCoords[j + 1].toFloat() - 10f

                val blockCenterX = (minX + maxX) * 0.5f
                val blockCenterZ = (minZ + maxZ) * 0.5f
                val district = getDistrictAt(blockCenterX, blockCenterZ)

                // Determine building style and dimensions according to district
                val (style, minH, maxH, count) = when (district) {
                    District.DOWNTOWN -> Quadruple(BuildingMeshFactory.BuildingStyle.DOWNTOWN_SKYSCRAPER, 45f, 90f, 2)
                    District.FINANCIAL -> Quadruple(BuildingMeshFactory.BuildingStyle.FINANCIAL_TOWER, 55f, 110f, 2)
                    District.WATERFRONT -> Quadruple(BuildingMeshFactory.BuildingStyle.WATERFRONT_WAREHOUSE, 14f, 28f, 3)
                    District.SHOPPING -> Quadruple(BuildingMeshFactory.BuildingStyle.SHOPPING_STOREFRONT, 20f, 40f, 4)
                    District.INDUSTRIAL -> Quadruple(BuildingMeshFactory.BuildingStyle.INDUSTRIAL_FACTORY, 16f, 32f, 3)
                    District.RESIDENTIAL -> Quadruple(BuildingMeshFactory.BuildingStyle.RESIDENTIAL_APARTMENTS, 22f, 45f, 4)
                    District.SUBURBAN -> Quadruple(BuildingMeshFactory.BuildingStyle.SUBURBAN_HOUSE, 10f, 16f, 4)
                    District.OLD_TOWN -> Quadruple(BuildingMeshFactory.BuildingStyle.OLD_TOWN_BRICK, 18f, 35f, 4)
                    District.HIGHWAY -> Quadruple(BuildingMeshFactory.BuildingStyle.HIGHWAY_STRUCTURE, 18f, 36f, 2)
                    District.OUTSKIRTS -> Quadruple(BuildingMeshFactory.BuildingStyle.AIRPORT_HANGAR, 12f, 24f, 2)
                }

                // Subdivide block into buildings
                val stepX = (maxX - minX) / 2f
                val stepZ = (maxZ - minZ) / 2f

                for (bx in 0 until 2) {
                    for (bz in 0 until 2) {
                        if (bx * 2 + bz >= count) continue

                        val bX = minX + stepX * (bx + 0.5f)
                        val bZ = minZ + stepZ * (bz + 0.5f)
                        val bW = stepX * 0.88f
                        val bD = stepZ * 0.88f
                        val bH = minH + ((bx + bz) % 2) * (maxH - minH) * 0.5f

                        val cacheKey = "${style.name}_${bx}_${bz}"
                        val buildingMesh = buildingMeshCache.getOrPut(cacheKey) {
                            BuildingMeshFactory.createBuilding(
                                style = style,
                                width = bW,
                                depth = bD,
                                height = bH,
                                seed = (bx * 31 + bz).toLong()
                            )
                        }

                        staticObjects.add(
                            StaticWorldObject(
                                mesh = buildingMesh,
                                position = Vector3(bX, 0f, bZ),
                                district = district,
                                isCollidable = true,
                                boundsRadius = maxOf(bW, bD) * 0.6f
                            )
                        )
                    }
                }
            }
        }

        // Spawn Interactive Story & Service Landmarks as ECS Entities
        spawnLandmarks(entityManager)

        // Spawn Initial Parked and World Vehicles across districts
        spawnInitialVehicles(entityManager)

        return CityData(roadNetwork, staticObjects, roadMeshes, propMeshes)
    }

    private fun spawnLandmarks(em: EntityManager) {
        // Safehouse (Player start / save / wardrobe) in Midtown Residential
        em.createEntity("Safehouse").apply {
            add(TransformComponent(position = Vector3(80f, 0f, 40f)))
            add(InteractiveComponent(InteractionType.SAFEHOUSE, "ENTER SAFEHOUSE (REST / SAVE)"))
            add(DistrictComponent(District.RESIDENTIAL))
        }

        // Marcus's Diner (Story Chapter 1 Hub) in Downtown
        em.createEntity("MarcusDiner").apply {
            add(TransformComponent(position = Vector3(20f, 0f, 20f)))
            add(InteractiveComponent(InteractionType.MISSION_GIVER, "MEET MARCUS (STORY MISSION)", payloadId = "mission_hub"))
            add(DistrictComponent(District.DOWNTOWN))
        }

        // Waterfront Warehouse 14 (Docks Cargo Hub)
        em.createEntity("WaterfrontWarehouse").apply {
            add(TransformComponent(position = Vector3(-180f, 0f, -20f)))
            add(InteractiveComponent(InteractionType.MISSION_GIVER, "WATERFRONT DISPATCH", payloadId = "dock_hub"))
            add(DistrictComponent(District.WATERFRONT))
        }

        // Urban Thread Clothing Store in Neon Promenade
        em.createEntity("ClothingStore").apply {
            add(TransformComponent(position = Vector3(30f, 0f, -80f)))
            add(InteractiveComponent(InteractionType.CLOTHING_STORE, "BROWSE WARDROBE & CLOTHING"))
            add(DistrictComponent(District.SHOPPING))
        }

        // Veloce Motors Dealership & Garage in Downtown
        em.createEntity("VehicleGarage").apply {
            add(TransformComponent(position = Vector3(-30f, 0f, -40f)))
            add(InteractiveComponent(InteractionType.VEHICLE_GARAGE, "ACCESS VEHICLE GARAGE"))
            add(DistrictComponent(District.DOWNTOWN))
        }

        // Street Racing Circuit Marker in Expressway district
        em.createEntity("StreetRace").apply {
            add(TransformComponent(position = Vector3(-160f, 0f, -160f)))
            add(InteractiveComponent(InteractionType.STREET_RACE_MARKER, "START STREET CIRCUIT RACE ($2,500 PRIZE)"))
            add(DistrictComponent(District.HIGHWAY))
        }

        // Taxi Depot Side Job Marker
        em.createEntity("TaxiHub").apply {
            add(TransformComponent(position = Vector3(0f, 0f, 80f)))
            add(InteractiveComponent(InteractionType.TAXI_PASSENGER, "START TAXI DISPATCH FARE"))
            add(DistrictComponent(District.DOWNTOWN))
        }
    }

    private fun spawnInitialVehicles(em: EntityManager) {
        // Player's starting personal car parked outside Safehouse
        em.createEntity("PlayerPersonalCar").apply {
            add(TransformComponent(position = Vector3(72f, 0f, 40f), yaw = 90f))
            add(PhysicsComponent(mass = 1400f, collisionRadius = 2.4f))
            add(VehicleComponent(type = VehicleType.VANGUARD_SEDAN))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER VANGUARD SEDAN"))
        }

        // Supercar near Financial Core
        em.createEntity("ParkedApexGT").apply {
            add(TransformComponent(position = Vector3(8f, 0f, 168f), yaw = 0f))
            add(PhysicsComponent(mass = 1200f, collisionRadius = 2.3f))
            add(VehicleComponent(type = VehicleType.APEX_GT))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER APEX GT"))
        }

        // Motorcycle in Old Town alley
        em.createEntity("OldTownMotorcycle").apply {
            add(TransformComponent(position = Vector3(-152f, 0f, 168f), yaw = 45f))
            add(PhysicsComponent(mass = 220f, collisionRadius = 1.2f))
            add(VehicleComponent(type = VehicleType.STREET_GHOST))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "MOUNT STREET GHOST"))
        }

        // Yellow City Cab on Downtown Main St
        em.createEntity("DowntownCab").apply {
            add(TransformComponent(position = Vector3(0f, 0f, -72f), yaw = 180f))
            add(PhysicsComponent(mass = 1500f, collisionRadius = 2.4f))
            add(VehicleComponent(type = VehicleType.CITY_CAB))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER CITY CAB"))
        }

        // Police Cruiser parked near Precinct
        em.createEntity("PoliceCruiser").apply {
            add(TransformComponent(position = Vector3(88f, 0f, -152f), yaw = 90f))
            add(PhysicsComponent(mass = 1650f, collisionRadius = 2.4f))
            add(VehicleComponent(type = VehicleType.SHADOW_CRUISER))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER POLICE CRUISER"))
        }

        // Titan SUV in Suburbs
        em.createEntity("SuburbanSUV").apply {
            add(TransformComponent(position = Vector3(168f, 0f, 168f), yaw = 0f))
            add(PhysicsComponent(mass = 2100f, collisionRadius = 2.5f))
            add(VehicleComponent(type = VehicleType.TITAN_SUV))
            add(InteractiveComponent(InteractionType.VEHICLE_DOOR, "ENTER TITAN SUV"))
        }
    }

    fun getDistrictAt(x: Float, z: Float): District {
        return when {
            z < -340f -> District.OUTSKIRTS
            x > 140f && z > 140f -> District.SUBURBAN
            x < -140f && z > 140f -> District.OLD_TOWN
            x > 140f && z < -140f -> District.INDUSTRIAL
            x < -140f && z < -140f -> District.HIGHWAY
            x < -120f -> District.WATERFRONT
            x > 120f -> District.RESIDENTIAL
            z > 120f -> District.FINANCIAL
            z < -120f -> District.SHOPPING
            else -> District.DOWNTOWN
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
