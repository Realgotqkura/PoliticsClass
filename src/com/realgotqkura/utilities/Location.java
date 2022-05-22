package com.realgotqkura.utilities;

import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Location {

    private float x;
    private float y;
    private float z;

    public Location(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //Actual methods

    public Vector3f toVector3f(){
        return new Vector3f(this.x,this.y,this.z);
    }

    public static Vector3f transformToVec3(Location loc){
        return new Vector3f(loc.getX(),loc.getY(),loc.getZ());
    }

    public static Location Vec3toLocation(Vector3f vec3){
        return new Location(vec3.x,vec3.y,vec3.z);
    }

    public static Vector3f subtract(Location vec, Location vec2){
        float x = vec2.x - vec.x;
        float y = vec2.y - vec.y;
        float z = vec2.z - vec.z;
        return new Vector3f(x,y,z);
    }


    //Getters and setters

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getZ(){
        return z;
    }

    public void setX(float nX){
        this.x = nX;
    }

    public void setY(float nY){
        this.y = nY;
    }

    public void setZ(float nZ){
        this.z = nZ;
    }

    public String toString(){
        return "Location[" + this.x + ", " + this.y + ", " + this.z + "]";
    }

    public void subtract(Location loc2){
        this.x -= loc2.getX();
        this.y -= loc2.getY();
        this.z -= loc2.getZ();
    }

}
