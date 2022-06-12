package com.realgotqkura.entities;

import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.particles.Particle;
import com.realgotqkura.particles.ParticleTexture;
import com.realgotqkura.utilities.Location;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Projectile extends Entity{

    //List<Projectile> innactiveList = new ArrayList<>(); //Arraylist of all cached projectile that are not active
    private float projectileDamage = 1;
    private ProjectileType projectileType;
    private Vector2f direction; //First argument is yaw, second is pitch
    private int flyingDuration; //For individuality this insures that not all projectiles are binded to 1 flying duration.
    public Projectile(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, Vector2f direction, int flyingDuration, ProjectileType projectileType) {
        super(model, loc, rotX, rotY, rotZ, scale);
        this.direction = direction;
        this.flyingDuration = flyingDuration;
        this.projectileType = projectileType;
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

    public ProjectileType getProjectileType(){
        return this.projectileType;
    }

    public void setVelocity(Vector2f dir){
        float z = (float) (this.getPosition().getZ() + 0 * Math.cos(Math.toRadians(dir.x - 90)) - 1 * Math.cos(Math.toRadians(dir.x)));
        float x = (float) (this.getPosition().getX() - 0 * Math.sin(Math.toRadians(dir.x - 90)) - 1 * Math.sin(Math.toRadians(dir.x)));
        float y = (float) (this.getPosition().getY() - 0 * Math.sin(Math.toRadians(dir.y - 90)) - 1 * Math.sin(Math.toRadians(dir.y)));
        this.setPosition(new Location(x, y, z));
        switch(projectileType){
            case DEFAULT:
                this.setRotZ(this.getRotZ()+10);
                projectileDamage = Player.playerDamageStat;
                break;
            case SHURIKEN:
                projectileDamage = 5 * Player.playerAbilityDamageStat;
                break;
            case AMATERASU:
                projectileDamage = 10 + (5 * Player.playerAbilityDamageStat);
                    for(int i = 0; i < 5; i++){
                        int randomX = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
                        int randomZ = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
                        int randomY = ThreadLocalRandom.current().nextInt(0, 10 + 1);
                        new Particle(this.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 0,5,10F,0, Main.particleTexture.get("Smoke"));
                    }
                break;
            case BOMB:
                this.setRotZ(this.getRotZ()+10);
                this.setRotX(this.getRotX()+10);
                projectileDamage = 10 + (3 * Player.playerAbilityDamageStat);
                break;
            case GAY:
                for(int i = 0; i < 5; i++){
                    int randomX = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
                    int randomZ = ThreadLocalRandom.current().nextInt(-10, 10 + 1);
                    int randomY = ThreadLocalRandom.current().nextInt(0, 10 + 1);
                    new Particle(this.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 0,5,1F,0, Main.particleTexture.get("Gay"));
                }
                break;
            case THORNS:
                this.setRotZ(this.getRotZ()+10);
                break;
        }

    }

    public static void generateRay(Vector2f dir, int range, Location startingPoint){
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(startingPoint.getX(), startingPoint.getY() - 1, startingPoint.getZ()));
        for(int i = 0; i < range; i++){
            float z = (float) (locations.get(locations.size()-1).getZ() + 0 * Math.cos(Math.toRadians(dir.x - 90)) - 1 * Math.cos(Math.toRadians(dir.x)));
            float x = (float) (locations.get(locations.size()-1).getX() - 0 * Math.sin(Math.toRadians(dir.x - 90)) - 1 * Math.sin(Math.toRadians(dir.x)));
            float y = (float) (locations.get(locations.size()-1).getY() - 0 * Math.sin(Math.toRadians(dir.y - 90)) - 1 * Math.sin(Math.toRadians(dir.y)));
            locations.add(new Location(x,y,z));
        }
        for(Location loc : locations){
            new Particle(loc.toVector3f(), new Vector3f(0,0,0), 0,0.2F,1F,0, Main.particleTexture.get("Gay"));
            for(EnemyEntity entity : Entity.enemies){
                if(MathHelper.isInside(loc,
                        new Location(entity.getPosition().getX() - 2, entity.getPosition().getY() - entity.getScale() - 2, entity.getPosition().getZ() -2),
                        new Location(entity.getPosition().getX() + 2, entity.getPosition().getY() + entity.getScale() + 2, entity.getPosition().getZ() + 2))) {
                    entity.hurtEnemy(0.3F, entity);
                }
            }
            }
        locations.clear();
        }

    public float getProjectileDamage(){
        return this.projectileDamage;
    }

}
