package com.realgotqkura.entities;

import com.realgotqkura.engine.MasterRenderer;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontRendering.TextMaster;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.Location;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EnemyEntity extends Entity{


    private TexturedModel model;
    private Location loc;
    private float rX, rY, rZ;
    private float scale;

    public EnemyEntity(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale) {
        super(model, loc, rotX, rotY, rotZ, scale);
        model = this.model;
        loc = this.loc;
        rX = rotX;
        rY = rotY;
        rZ = rotZ;
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
        //Moving enemy
        if(checkPlayerCollision(enemy)){
            Entity.deleteEntityCache.add(enemy);
            Player.health--;
            for(GUIText text : TextMaster.texts.get(Main.primaryFont)){
                if(text.getText().contains("Health:")){
                    text.replaceText(text, "Health: " + Player.health);
                    break;
                }
            }
            if(Player.health == 0){
                Player.health = Player.MAX_HEALTH;
                for(GUIText text : TextMaster.texts.get(Main.primaryFont)){
                    if(text.getText().contains("Wave:")){
                        text.replaceText(text, "Wave: " + 0);
                        Player.waveTest = 0;
                        break;
                    }
                }
                for(GUIText text : TextMaster.texts.get(Main.primaryFont)){
                    if(text.getText().contains("Health:")){
                        text.replaceText(text, "Health: " + Player.MAX_HEALTH);
                        break;
                    }
                }
            }
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
