package com.realgotqkura.textures;

public class ModelTexture {

    private final int textureID;
    private float shinyDamper = 1;
    private float reflectivity = 0;

    private boolean transparent = false;
    private boolean useFakeLight = false;

    public boolean isUsingFakeLight(){
        return useFakeLight;
    }

    public void setFakeLight(boolean value){
        useFakeLight = value;
    }

    public boolean isTransparent(){
        return transparent;
    }

    public void setTransparency(boolean value){
        transparent = value;
    }

    public ModelTexture(int textureID){
        this.textureID = textureID;
    }

    public int getTextureID(){
        return this.textureID;
    }

    public float getShinyDamper() {
        return shinyDamper;
    }

    public void setShinyDamper(float shinyDamper) {
        this.shinyDamper = shinyDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(int reflectivity) {
        this.reflectivity = reflectivity;
    }
}
