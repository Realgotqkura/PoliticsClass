package com.realgotqkura.guis;

import com.realgotqkura.utilities.RotationVector;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class GuiTexture {

    private int texture;
    private Vector2f position;
    private Vector2f scale;
    private RotationVector rotation;
    private String name;

    public GuiTexture(int texture, Vector2f pos, Vector2f scale, RotationVector rotation, String name){
        this.texture = texture;
        this.position = pos;
        this.scale = scale;
        this.rotation = rotation;
        this.name = name;
    }

    public int getTexture(){
        return texture;
    }

    public Vector2f getPosition(){
        return position;
    }

    public Vector2f getScale(){
        return scale;
    }

    public RotationVector getRotation(){
        return rotation;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void nullifyTexture(){
        this.texture = 0;
    }

    public void setTexture(int textureId){
        this.texture = textureId;
    }

    public void setPosition(Vector2f pos){
        this.position = pos;
    }
}
