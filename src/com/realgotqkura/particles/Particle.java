package com.realgotqkura.particles;


import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.entities.Camera;
import com.realgotqkura.entities.Player;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float size;
    private float rotation;

    private ParticleTexture texture;
    private Vector2f texOffset1 = new Vector2f();
    private Vector2f texOffset2 = new Vector2f();
    private float blend;

    private float timeElapsed = 0;
    private float distance;

    public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float size, float rotation, ParticleTexture texture) {
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.size = size;
        this.rotation = rotation;
        this.texture = texture;
        ParticleMaster.addParticle(this);
    }

    public ParticleTexture getTexture(){
        return texture;
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

    protected boolean update(Camera camera){
        velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getDelta();
        Vector3f change = new Vector3f(velocity);
        change.scale(DisplayManager.getDelta());
        Vector3f.add(change,position,position);
        distance = Vector3f.sub(camera.getPosition().toVector3f(), position,null).lengthSquared();
        updatetextureCoordsInfo();
        timeElapsed += DisplayManager.getDelta();
        return timeElapsed < lifeLength;
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlend() {
        return blend;
    }

    public float getDistance() {
        return distance;
    }

    private void updatetextureCoordsInfo(){
        float lifeFactor = timeElapsed / lifeLength;
        float stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        this.blend = atlasProgression % 1;
        setTextureOffset(texOffset1, index1);
        setTextureOffset(texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index){
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }
}
