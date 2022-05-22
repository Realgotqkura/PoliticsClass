package com.realgotqkura.entities;

import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.utilities.Location;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Projectile extends Entity{

    private Vector2f direction; //First argument is yaw, second is pitch
    private int flyingDuration; //For individuality this insures that not all projectiles are binded to 1 flying duration.
    public Projectile(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, Vector2f direction, int flyingDuration) {
        super(model, loc, rotX, rotY, rotZ, scale);
        this.direction = direction;
        this.flyingDuration = flyingDuration;
        Entity.projectiles.add(this);
    }

    public Vector2f getDirection(){
        return direction;
    }

    public int getFlyingDuration(){
        return flyingDuration;
    }

    public void setDirection(Vector2f dir){
        this.direction = dir;
    }

    public void setFlyingDuration(int duration){
        this.flyingDuration = duration;
    }
}
