package com.realgotqkura.engine;

import com.realgotqkura.entities.Camera;
import com.realgotqkura.entities.EnemyEntity;
import com.realgotqkura.entities.Entity;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.Location;
import com.realgotqkura.utilities.MathHelper;
import org.lwjgl.system.CallbackI;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.Display;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.lwjglx.util.vector.Vector4f;

import java.util.List;

public class RayCast {

    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 100;

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;

    private List<Terrain> terrains;
    private Vector3f currentTerrainPoint;
    private Vector3f lastTerrainPoint;

    public RayCast(Camera cam, Matrix4f projection, List<Terrain> terrain) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = MathHelper.createViewMatrix(camera);
        this.terrains = terrain;
    }

    public Vector3f getCurrentTerrainPoint() {
        if(currentTerrainPoint == null){
            return lastTerrainPoint;
        }else{
            return currentTerrainPoint;
        }
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = MathHelper.createViewMatrix(camera);
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
            lastTerrainPoint = currentTerrainPoint;
        } else {
            currentTerrainPoint = null;
        }

    }

    private Vector3f calculateMouseRay() {
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(DisplayManager.WIDTH / 2,DisplayManager.HEIGHT / 2);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / DisplayManager.WIDTH - 1f;
        float y = (2.0f * mouseY) / DisplayManager.HEIGHT - 1f;
        return new Vector2f(x, y);
    }

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = Location.transformToVec3(camera.getPosition());
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return Vector3f.add(start, scaledRay, null);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }

        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightAtLocation(testPoint.getX(), testPoint.getZ());
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }

    private Terrain getTerrain(float worldX, float worldZ) {
        for(Terrain terrain : terrains){
            if(terrain.getX() <= worldX) {
                if(terrain.getX() + Terrain.SIZE > worldX) {
                    if(terrain.getZ() <= worldZ) {
                        if(terrain.getZ() + Terrain.SIZE > worldZ) {
                            return terrain;
                        }
                    }
                }
            }
        }
        return null;
    }

}
