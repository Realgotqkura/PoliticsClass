package com.realgotqkura.entities;

import com.realgotqkura.engine.MasterRenderer;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontRendering.TextMaster;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.particles.Particle;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.Location;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyEntity extends Entity{


    private TexturedModel model;
    private Location loc;
    private float rX, rY, rZ;
    private float scale;
    private int health;
    private Projectile collidedProjectile;

    public EnemyEntity(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, int health) {
        super(model, loc, rotX, rotY, rotZ, scale);
        model = this.model;
        loc = this.loc;
        rX = rotX;
        rY = rotY;
        rZ = rotZ;
        this.health = health;
        scale = this.scale;
        enemies.add(this); //Might cause problems
    }

    public float findTerrainY(Entity enemy){
        for(Terrain terrain : Main.terrains){
            if(terrain.getX() <= enemy.getPosition().getX()) {
                if(terrain.getX() + Terrain.SIZE > enemy.getPosition().getX()) {
                    if(terrain.getZ() <= enemy.getPosition().getZ()) {
                        if(terrain.getZ() + Terrain.SIZE > enemy.getPosition().getZ()) {
                            return terrain.getHeightAtLocation(enemy.getPosition().getX(), enemy.getPosition().getZ());

                        }
                    }
                }
            }
        }
        return 0;
    }

    public void multiplyEnemy(Location loc){
        enemies.add(this);
        entities.add(this);
        Main.renderer.addEntity(this);
    }

    public boolean collisionCheck(Entity entity){
        //if true: collided
        //if false: didn't collide
        List<EnemyEntity> enemyEntities = new ArrayList<>(); //List excluding the entity
        for(EnemyEntity entity2 : Entity.enemies){
            if(!(entity2.equals(entity))){
                enemyEntities.add(entity2);
            }
        }
        for(EnemyEntity entity1 : enemyEntities){
            if(MathHelper.isInside(entity1.getPosition(),
                    new Location(entity.getPosition().getX() - 2, entity.getPosition().getY() - entity.getScale() - 2, entity.getPosition().getZ() -2),
                    new Location(entity.getPosition().getX() + 2, entity.getPosition().getY() + entity.getScale() + 2, entity.getPosition().getZ() + 2))){
                enemyEntities.clear();
                return true;
            }
        }
        enemyEntities.clear();
        return false;
    }

    private boolean checkPlayerCollision(Entity entity){
        if(MathHelper.isInside(Player.player.getPosition(),
                new Location(entity.getPosition().getX() - 2, entity.getPosition().getY() - entity.getScale() - 2, entity.getPosition().getZ() -2),
                new Location(entity.getPosition().getX() + 2, entity.getPosition().getY() + entity.getScale() + 2, entity.getPosition().getZ() + 2))){
            return true;
        }
        return false;
    }

    private boolean checkProjectileCollision(Entity entity){
        for(Projectile projectile : Entity.projectiles){
            if(MathHelper.isInside(projectile.getPosition(),
                    new Location(entity.getPosition().getX() - 2, entity.getPosition().getY() + (entity.getScale() * 2), entity.getPosition().getZ()  - 2),
                    new Location(entity.getPosition().getX() + 2, entity.getPosition().getY() - (entity.getScale() * 2), entity.getPosition().getZ() + 2))){
                collidedProjectile = projectile;
                Main.renderer.entities.get(projectile.getModel()).remove(projectile);
                Entity.entities.remove(projectile);
                Entity.projectiles.remove(projectile);
                return true;
            }
        }
        return false;
    }


    public void tick(Entity enemy){
        if(checkProjectileCollision(enemy)){
            for(int i = 0; i < 10; i++){
                int randomX = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
                int randomZ = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
                int randomY = ThreadLocalRandom.current().nextInt(-100,100 + 1);
                new Particle(enemy.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 1,5,1,0);
            }
            this.health -= collidedProjectile.getProjectileDamage();
            if(this.health <= 0){
                Entity.deleteEntityCache.add(enemy);
                if(Player.abilityInUse){
                    Player.playerKills++;
                }
                if(Player.abilityOnCooldown && !Player.abilityInUse){
                    Player.cooldownKills++;
                    GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown " + (Player.abilityCooldownKills - Player.cooldownKills) + " kills left)");
                }
                switch(Player.playableCharacter){
                    case "Nino":
                        if(Player.playerKills >= 2){
                            Player.abilityInUse = false;
                            Player.playerKills = 0;
                        }else if(Player.cooldownKills >= Player.abilityCooldownKills){
                            Player.cooldownKills = 0;
                            GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                            Player.abilityOnCooldown = false;
                        }
                        break;
                    case "Mitko":
                         if(Player.cooldownKills >= Player.abilityCooldownKills){
                            Player.cooldownKills = 0;
                            GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                            Player.abilityOnCooldown = false;
                        }
                        break;
                    case "Vladi":
                        if(Player.playerKills >= 3){
                            Player.abilityInUse = false;
                            Player.playerKills = 0;
                            GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown)");
                            Player.abilityOnCooldown = true;
                        }else if(Player.cooldownKills >= Player.abilityCooldownKills){
                            Player.cooldownKills = 0;
                            GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                            Player.abilityOnCooldown = false;
                        }
                        break;
                }
            }
        }
        if(checkPlayerCollision(enemy)){
            Entity.deleteEntityCache.add(enemy);
            Player.health--;
            GUIText.replaceText("Health: ", "Health: " + Player.health);
            if(Player.health == 0){
                Player.health = Player.MAX_HEALTH;
                GUIText.replaceText("Wave: ", "Wave: " + 0);
                Player.waveTest = 0;
                GUIText.replaceText("Health: ", "Health: " + Player.MAX_HEALTH);
            }
        }
    }

    public void pathFindertick(Entity target, Entity enemy){
        float deltaX = 0.11F, deltaZ = 0.11F;
        Location enemyLoc = enemy.getPosition();
        Location targetLoc = target.getPosition();
        float x = enemyLoc.getX(),z = enemyLoc.getZ();
        if(enemyLoc.getX() > targetLoc.getX()){
            deltaX /= -1; //divide by -1
        }
        if(enemyLoc.getZ() > targetLoc.getZ()){
            deltaZ /= -1; //divide by -1
        }
        if(MathHelper.distanceBetweenObjects(enemyLoc, targetLoc) < 0.2){
            return;
        }

       else if(!collisionCheck(enemy)){
           x = enemyLoc.getX() + deltaX;
           z = enemyLoc.getZ() + deltaZ;
           enemyLoc.setY(findTerrainY(enemy));
           enemyLoc.setX(x);
           enemyLoc.setZ(z);
       }else{
           enemyLoc.setY(findTerrainY(enemy));
           enemyLoc.setX(x);
           enemyLoc.setZ(z);
       }
       //Rotation
        Vector3f difference = Location.subtract(targetLoc, enemyLoc);
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
        enemy.setRotY(degrees);
        //Prepare for another run
        deltaX = 0.11F;
        deltaZ = 0.11F;

    }




}
