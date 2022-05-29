package com.realgotqkura.particles;


import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.entities.Player;
import org.lwjglx.util.vector.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float size;
    private float rotation;

    private float timeElapsed = 0;

    public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float size, float rotation) {
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.size = size;
        this.rotation = rotation;
        ParticleMaster.addParticle(this);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public float getGravityEffect() {
        return gravityEffect;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public float getSize() {
        return size;
    }

    public float getRotation() {
        return rotation;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    protected boolean update(){
        velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getDelta();
        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDelta());
        Vector3f.add(change,position,position);
        timeElapsed += DisplayManager.getDelta();
        return timeElapsed < lifeLength;
    }
}
