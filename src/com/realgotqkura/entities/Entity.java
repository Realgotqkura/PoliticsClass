package com.realgotqkura.entities;

import com.realgotqkura.engine.MasterRenderer;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.utilities.Location;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private TexturedModel model;
    private Location position;
    private float rotX,rotY,rotZ;
    private float scale;
    public static List<Entity> entities = new ArrayList<>();
    public static List<EnemyEntity> enemies = new ArrayList<>();
    public static List<Entity> deleteEntityCache = new ArrayList<>();
    public static List<Projectile> projectiles = new ArrayList<>();


    public Entity(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = loc;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        entities.add(this);
    }

    public void increasePosition(float x, float y, float z){
        this.position.setX(this.position.getX() + x);
        this.position.setY(this.position.getY() + y);
        this.position.setZ(this.position.getZ() + z);
    }

    public void increaseRotation(float rX, float rY, float rZ){
        this.rotX += rX;
        this.rotY += rY;
        this.rotZ += rZ;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public TexturedModel getModel() {
        return model;
    }

    public Location getPosition() {
        return position;
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

    public float getScale() {
        return scale;
    }

    public static void removeEntity(Entity entity){
        entities.remove(entity);
    }

}
