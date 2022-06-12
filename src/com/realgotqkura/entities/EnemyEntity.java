package com.realgotqkura.entities;

import com.realgotqkura.engine.MasterRenderer;
import com.realgotqkura.engine.ModelData;
import com.realgotqkura.engine.OBJLoader;
import com.realgotqkura.engine.testing.TexturedModels;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontRendering.TextMaster;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.particles.Particle;
import com.realgotqkura.particles.ParticleTexture;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.textures.ModelTexture;
import com.realgotqkura.utilities.Location;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyEntity extends Entity{


    public boolean stunned = false;
    private float seperateSpeed;
    public static float Speed = 0.11F;
    private TexturedModel model;
    private Location loc;
    private float rX, rY, rZ;
    private float scale;
    private float health;
    private Projectile collidedProjectile;

    public EnemyEntity(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, int health, float speed) {
        super(model, loc, rotX, rotY, rotZ, scale);
        model = this.model;
        loc = this.loc;
        rX = rotX;
        rY = rotY;
        rZ = rotZ;
        this.health = health;
        this.seperateSpeed = speed;
        scale = this.scale;
        stunned = false;
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

    public TexturedModel loadProjectile(){
        ModelTexture table = new ModelTexture(Main.loader.loadTexture("CraftingTableTex"));
        table.setTransparency(true);
        table.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("CraftingTable");
        return new TexturedModel(Main.loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), table, "CraftingTable");
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
                new Particle(enemy.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 1,5,1F,0, Main.particleTexture.get("Fire"));
            }
            hurtEnemy(collidedProjectile.getProjectileDamage(), (EnemyEntity) enemy);
            if(collidedProjectile.getProjectileType() == ProjectileType.BOMB){
                for(EnemyEntity enemyEntity : Entity.enemies){
                    if(MathHelper.isInside(enemyEntity.getPosition(),
                            new Location(collidedProjectile.getPosition().getX() - 25, collidedProjectile.getPosition().getY() + (collidedProjectile.getScale() * 5), collidedProjectile.getPosition().getZ()  - 25),
                            new Location(enemy.getPosition().getX() + 25, collidedProjectile.getPosition().getY() - (collidedProjectile.getScale() * 5), collidedProjectile.getPosition().getZ() + 25))){
                        Entity.deleteEntityCache.add(enemyEntity);
                    }
                }
            }
        }
        if(checkPlayerCollision(enemy)){
            Entity.deleteEntityCache.add(enemy);
            Player.health--;
            if(Player.abilityOnCooldown && !Player.abilityInUse){
                Player.cooldownKills++;
                GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown " + (Player.abilityCooldownKills - Player.playerCooldownStat - Player.cooldownKills) + " kills left)");
            }
            GUIText.replaceText("Health: ", "Health: " + Player.health);
            if(Player.health == 0){
                Player.health = Player.MAX_HEALTH;
                GUIText.replaceText("Wave: ", "Wave: " + 0);
                Player.waveTest = 0;
                GUIText.replaceText("Health: ", "Health: " + Player.MAX_HEALTH);
            }
        }
    }

    private void cecoAbility(){
        int randomS = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        int randomN = ThreadLocalRandom.current().nextInt(0,1 + 1);
        switch(randomS){
            case 0:
                if(randomN == 0){
                    Player.health++;
                    GUIText.replaceText("Last stat", "Last stat UP: +1HP");
                }else{
                    Player.health--;
                    GUIText.replaceText("Last stat", "Last stat UP: -1HP");
                }
                GUIText.replaceText("Health:", "Health: " + Player.health);
                break;
            case 1:
                if(randomN == 0){
                    Player.WALK_SPEED += 0.5F;
                    GUIText.replaceText("Last stat", "Last stat UP: +0.5 Speed");
                }else{
                    Player.WALK_SPEED -= 0.3F;
                    GUIText.replaceText("Last stat", "Last stat UP: -0.3 Speed");
                }
                break;
            case 2:
                if(randomN == 0){
                    Player.playerDamageStat += 0.3F;
                    GUIText.replaceText("Last stat", "Last stat UP: +0.3 Dmg");
                }else{
                    Player.playerDamageStat -= 0.2F;
                    GUIText.replaceText("Last stat", "Last stat UP: -0.2 Dmg");
                }
                break;
            case 3:
                if(randomN == 0){
                    Player.playerCoinGainStat += 0.3F;
                    GUIText.replaceText("Last stat", "Last stat UP: +0.3 Coins per Kill");
                }else{
                    Player.playerCoinGainStat -= 0.2F;
                    GUIText.replaceText("Last stat", "Last stat UP: -0.2 Coins per Kill");
                }
                break;
        }
    }

    public void pathFindertick(Entity target, EnemyEntity enemy, boolean reversed, float speed){
        float deltaX = speed, deltaZ = speed;
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
        //Rotation
        Vector3f difference = Location.subtract(targetLoc, enemyLoc);
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
           if(!reversed){
               x = enemyLoc.getX() + deltaX;
               z = enemyLoc.getZ() + deltaZ;
               enemy.setRotY(degrees);
           }else{
               x = enemyLoc.getX() - deltaX;
               z = enemyLoc.getZ() - deltaZ;
               enemy.setRotY(degrees - 90);
           }
           enemyLoc.setY(findTerrainY(enemy));
           enemyLoc.setX(x);
           enemyLoc.setZ(z);
        //Prepare for another run
        deltaX = 0.11F;
        deltaZ = 0.11F;

    }

    public void hurtEnemy(float damage, EnemyEntity enemy){
        if(Player.playableCharacter.equalsIgnoreCase("emo") && Player.waveTest % 3 == 0) {
          this.health -= damage * 2;
        }else {
            this.health -= damage;
        }
        if(!enemy.stunned){
            this.seperateSpeed = Speed - (Speed / 10);
            enemy.stunned = true;
        }
        if(Player.currentMonth.equalsIgnoreCase("June")){
            for(int i = 0; i < 10; i++){
                int randomX = ThreadLocalRandom.current().nextInt(-50,  50 + 1);
                int randomZ = ThreadLocalRandom.current().nextInt(-50, 50 + 1);
                int randomY = ThreadLocalRandom.current().nextInt(-50,50 + 1);
                new Particle(this.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 1,5,2F,0, Main.particleTexture.get("Fire"));
            }
            return;
        }
        for(int i = 0; i < 10; i++){
            int randomX = ThreadLocalRandom.current().nextInt(-50,  50 + 1);
            int randomZ = ThreadLocalRandom.current().nextInt(-50, 50 + 1);
            int randomY = ThreadLocalRandom.current().nextInt(-50,50 + 1);
            new Particle(this.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 1,5,5F,0, Main.particleTexture.get("Fire"));
        }
        if(this.health <= 0){
            onDeath(enemy);
        }
    }

    public void onDeath(EnemyEntity enemy){
        if(Player.playableCharacter.equalsIgnoreCase("ceco")){
            cecoAbility();
        }
        int cooldown = Player.abilityCooldownKills - Player.playerCooldownStat;
        Player.addCoin(Player.playerCoinGainStat + 1);
        GUIText.replaceText(":Coins", Math.round(Math.floor(Player.playerCoins)) + " :Coins");
        for(int i = 0; i < 10; i++){
            int randomX = ThreadLocalRandom.current().nextInt(-50,  50 + 1);
            int randomZ = ThreadLocalRandom.current().nextInt(-50, 50 + 1);
            int randomY = ThreadLocalRandom.current().nextInt(-50,50 + 1);
            new Particle(enemy.getPosition().toVector3f(), new Vector3f(randomX,randomY,randomZ), 1,5,1F,0, Main.particleTexture.get("Fire"));
        }
        Entity.deleteEntityCache.add(enemy);
        if(Player.abilityInUse){
            Player.playerKills++;
        }
        if(Player.abilityOnCooldown && !Player.abilityInUse){
            Player.cooldownKills++;
            if(Player.playableCharacter.equalsIgnoreCase("mitko")){
                int random = ThreadLocalRandom.current().nextInt(0,4+1);
                if(random == 3){
                    Player.cooldownKills++;
                }
            }
            GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown " + (cooldown - Player.cooldownKills) + " kills left)");
        }
        switch(Player.playableCharacter){
            case "Nino":
                if(Player.playerKills >= 2){
                    Player.abilityInUse = false;
                    Player.playerKills = 0;
                }else if(Player.cooldownKills >= cooldown){
                    Player.cooldownKills = 0;
                    GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                    Player.abilityOnCooldown = false;
                }
                break;
            case "Vasko":
            case "Emo":
            case "Mitko":
            case "Gosho":
            case "Magi":
            case "Nad.T":
                if(Player.cooldownKills >= cooldown){
                    Player.cooldownKills = 0;
                    GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                    Player.abilityOnCooldown = false;
                }
                break;
            case "Lora":
            case "Vladi":
                if(Player.playerKills >= 3){
                    Player.abilityInUse = false;
                    Player.playerKills = 0;
                    GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown " + Player.abilityCooldownKills + " kills left)");
                    Player.abilityOnCooldown = true;
                }else if(Player.cooldownKills >= cooldown){
                    Player.cooldownKills = 0;
                    GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Ready)");
                    Player.abilityOnCooldown = false;
                }
                break;
        }
    }

    public float getSeperateSpeed(){
        return seperateSpeed;
    }







}
