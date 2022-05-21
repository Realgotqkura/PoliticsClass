package com.realgotqkura.utilities;

public class RotationVector {

    private float rotX;
    private float rotY;
    private float rotZ;

    public RotationVector(float rotX, float rotY, float rotZ){
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotX(int rotX) {
        this.rotX = rotX;
    }

    public void setRotY(int rotY) {
        this.rotY = rotY;
    }

    public void setRotZ(int rotZ) {
        this.rotZ = rotZ;
    }

    public String toString(){
        return "Rotation[" + getRotX() + ", " + getRotY() + ", " + getRotZ() + "]";
    }
}
