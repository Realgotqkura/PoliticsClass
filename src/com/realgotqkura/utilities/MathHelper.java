package com.realgotqkura.utilities;

import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.entities.Camera;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class MathHelper {

    public static Matrix4f createTransformMatrix(Vector3f loc, float rotX, float rotY, float rotZ, float scale){
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(loc, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotX), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotY), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotZ), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getTilt()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition().toVector3f();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public static Matrix4f create2DTransformationMatrix(Vector2f translation, Vector2f scale, RotationVector rotation) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.getRotX()), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.getRotY()), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.getRotZ()), new Vector3f(0,0,1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }

    public static float distanceBetweenObjects(Location pos1, Location pos2){
        float x;
        float z;

        if(pos1.getZ() > 0 && pos2.getZ() > 0){
            z = pos1.getZ() - pos2.getZ();
        }else if(pos1.getZ() < 0 && pos2.getZ() < 0) {
            z = pos1.getZ() - (pos2.getZ() / -1);
        }else{
            if(pos1.getZ() < pos2.getZ()){
                z = pos1.getZ() + pos2.getZ();
            }else {
                z = pos1.getZ() - pos2.getZ();
            }
        }
        if(pos1.getX() > 0 && pos2.getX() > 0){
            x = pos1.getX() - pos2.getX();
        }else if(pos1.getX() < 0 && pos2.getX() < 0) {
            x = pos1.getX() - (pos2.getX() / -1);
        }else{
            if(pos1.getX() < pos2.getX()){
                x = pos1.getX() + pos2.getX();
            }else {
                x = pos1.getX() - pos2.getX();
            }
        }

        return (float) Math.sqrt(Math.pow(x,2) + Math.pow(z,2));
    }

    public static float[] distanceBetweenObjectsVars(Location pos1, Location pos2){
        float x;
        float z;

        if(pos1.getZ() > 0 && pos2.getZ() > 0){
            z = pos1.getZ() - pos2.getZ();
        }else if(pos1.getZ() < 0 && pos2.getZ() < 0) {
            z = pos1.getZ() - (pos2.getZ() / -1);
        }else{
            if(pos1.getZ() < pos2.getZ()){
                z = pos1.getZ() + pos2.getZ();
            }else {
                z = pos1.getZ() - pos2.getZ();
            }
        }
        if(pos1.getX() > 0 && pos2.getX() > 0){
            x = pos1.getX() - pos2.getX();
        }else if(pos1.getX() < 0 && pos2.getX() < 0) {
            x = pos1.getX() - (pos2.getX() / -1);
        }else{
            if(pos1.getX() < pos2.getX()){
                x = pos1.getX() + pos2.getX();
            }else {
                x = pos1.getX() - pos2.getX();
            }
        }

        return new float[]{x,z};
    }


    public static int floor(float var0) {
        int var1 = (int)var0;
        return var0 < (float)var1 ? var1 - 1 : var1;
    }

    public static boolean isEven(int num){
        if(num % 2 == 0){
            return true;
        }
        return false;
    }

    public static float trigonometryMinClamp(float min, float bigNum, float smallNum){
        if((bigNum - smallNum) < min){
            float result = bigNum - smallNum;
            return 360 + result;
        }
        return bigNum - smallNum;
    }

    public static float trigonometryMaxClamp(float max, float num1, float num2){
        if((num1 + num2) > max){
            float result = num1 + num2;
            return max - result;
        }

        return num1 + num2;
    }

    public static float clamp(float min, float max, float clampNum){
        if(clampNum > max){
            return 1;
        }else if(clampNum < min){
            return 2;
        }
        return 0;
    }


    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public static boolean isInside(Location loc, Location l1, Location l2) {
        float x1 = Math.min(l1.getX(), l2.getX());
        float y1 = Math.min(l1.getY(), l2.getY());
        float z1 = Math.min(l1.getZ(), l2.getZ());
        float x2 = Math.max(l1.getX(), l2.getX());
        float y2 = Math.max(l1.getY(), l2.getY());
        float z2 = Math.max(l1.getZ(), l2.getZ());
        float x = loc.getX();
        float y = loc.getY();
        float z = loc.getZ();

        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public static boolean isInside2D(Vector2f loc, Vector2f l1, Vector2f l2) {
        float x1 = Math.min(l1.getX(), l2.getX());
        float y1 = Math.min(l1.getY(), l2.getY());
        float x2 = Math.max(l1.getX(), l2.getX());
        float y2 = Math.max(l1.getY(), l2.getY());
        float x = loc.getX();
        float y = loc.getY();

        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }



    //For transforming Mouse X and Y positions to GUITexture X and Y positions we must define in which quadrant they are
    //in.



    public static float findPercantage(float part, float whole){
        return (part / whole) * 100;
    }
}
