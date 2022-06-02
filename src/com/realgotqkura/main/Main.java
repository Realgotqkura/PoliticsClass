package com.realgotqkura.main;

import com.realgotqkura.data.DataMethods;
import com.realgotqkura.data.SaveableData;
import com.realgotqkura.engine.*;
import com.realgotqkura.engine.testing.TerrainPresets;
import com.realgotqkura.engine.testing.TexturedModels;
import com.realgotqkura.entities.*;
import com.realgotqkura.fontMeshCreator.FontType;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontRendering.TextMaster;
import com.realgotqkura.guis.GUIRenderer;
import com.realgotqkura.guis.GUIS;
import com.realgotqkura.guis.GuiTexture;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.particles.Particle;
import com.realgotqkura.particles.ParticleMaster;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.textures.ModelTexture;
import com.realgotqkura.utilities.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MathUtil;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import java.awt.*;
import java.io.File;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static MasterRenderer renderer;
    public static Loader loader;
    private Camera camera;
    private GLFWKeyCallback keyCallback;
    private Light light;
    private Player player;
    private TerrainPresets presets;
    private GUIRenderer guiRenderer;
    public static RayCast ray;
    public static List<Terrain> terrains = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    public static Entity holdedEntity;
    private TexturedModels models;
    public static FontType primaryFont;
    public static Terrain currentTerrain; //The current terrain that the player is standing on


    public Main(){
        DataMethods.createFile();
        DataMethods.SaveUponExit();
        DataMethods.load();
        DisplayManager.createDisplay();
        loader = new Loader();
        renderer = new MasterRenderer();
        camera = new Camera();
        TextMaster.init(loader);
        primaryFont = new FontType(loader.loadTexture("newFont"), new File("res/newFont.fnt"));
        GUIText text = new GUIText("Wave:",3 , primaryFont, new Vector2f(0,0), 0.5F, false);
        GUIText textHP = new GUIText("Health: " + Player.MAX_HEALTH,3, primaryFont, new Vector2f(0F,0.1F), 0.5F, false);
        GUIText goldTxt = new GUIText(SaveableData.GOLD + " :Gold",3, primaryFont, new Vector2f(0F,0.3F), 0.5F, false);
        GUIText Shop = new GUIText("Shop: R",3, primaryFont, new Vector2f(0.82F,0.0F), 0.5F, false);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());

        presets = new TerrainPresets(loader);
        guiRenderer = new GUIRenderer(loader);
        light = new Light(new Vector3f(0,1000,0), new Vector3f(1,1,1));
        models = new TexturedModels(loader);
        terrains.add(new Terrain(0,0, loader, new ModelTexture(loader.loadTexture("MC_Grass")), "heightmap"));
        terrains.add(new Terrain(-1,0, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        terrains.add(new Terrain(-1,-1, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        terrains.add(new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        terrains.add(new Terrain(0,1, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        //terrains.add(new Terrain(1,1, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        //terrains.add(new Terrain(1,0, loader, new ModelTexture(loader.loadTexture("MC_Grass")),"heightmap"));
        for(int i = 0 ; i < terrains.size(); i++){
            renderer.addTerrain(terrains.get(i));
        }
        //Tsukuyomi: light.setColor(new Vector3f(0.7F,0.7F,0.7F));
        light.setColor(new Vector3f(0.7F, 0.78F, 0.7F));
        ray = new RayCast(camera, renderer.getProjectionMatrix(), terrains);
        ModelData data = OBJLoader.loadOBJModel("ball");
        RawModel playerModel = loader.loadtoVao(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("metal"));
        player = new Player(new TexturedModel(playerModel, playerTexture, "player"), new Location(1,5,1),1,1,1,1, loader);
        //Entity craftingTable = new Entity(models.craftingTable(), player.getPosition(), 180,0,180, 2);
        EnemyEntity trumpEnemy = new EnemyEntity(models.trumpEnemy(), new Location(player.getPosition().getX() + 10, player.getPosition().getY(), player.getPosition().getZ() + 10), 180,0,180, 4, 10);
        for(Entity entity : presets.randomPreset("heightmap")){
            entities.add(entity);
        }
        for(Entity entity : Entity.entities){
            renderer.addEntity(entity);
        }
        //holdedEntity = craftingTable;
        GUIS.initGUIs();
        System.out.println(Entity.enemies);
        GLFW.glfwSetKeyCallback(DisplayManager.window,keyCallback = new Input());
        run();
    }


    public void run(){
        Player.insideAGUI = true;
        GUIS.loadPlayerInventory();
        //GUIS.loadShopGUI();
        GUIS.guis.get(0).setPosition(new Vector2f(0,0));
        while(!DisplayManager.shouldClose()){
            //game loop and rendering
            ray.update();
            ParticleMaster.update();
            for(Terrain terrain : terrains){
                if(terrain.getX() <= player.getPosition().getX()) {
                    if(terrain.getX() + Terrain.SIZE > player.getPosition().getX()) {
                        if(terrain.getZ() <= player.getPosition().getZ()) {
                            if(terrain.getZ() + Terrain.SIZE > player.getPosition().getZ()) {
                                currentTerrain = terrain;
                                player.move(terrain);
                                camera.move(player);

                            }
                        }
                    }
                }
            }
            //System.out.println(ray.getCurrentRay());
            if(!player.isInsideAGUI() && !(Player.abilityInUse && Player.playableCharacter.contains("Nino"))){
                for(EnemyEntity enemy : Entity.enemies){
                    if(Player.playableCharacter.equalsIgnoreCase("magi") && Player.abilityInUse && Player.magiAbilityTicks < 100){
                        enemy.pathFindertick(player, enemy, true, EnemyEntity.Speed);
                    }else{
                        enemy.pathFindertick(player, enemy, false, EnemyEntity.Speed);
                    }
                }
                if(Player.playableCharacter.equalsIgnoreCase("magi") && Player.abilityInUse && Player.magiAbilityTicks < 100){
                    Player.magiAbilityTicks++;
                }
            }
            if(Player.magiAbilityTicks == 100 && Player.abilityInUse){
                Player.abilityOnCooldown = true;
                Player.abilityInUse = false;
                Player.magiAbilityTicks = 0;
                GUIText.replaceText("Ability", "Ability (E): " + Player.ability + " (Cooldown " + (Player.abilityCooldownKills - Player.playerCooldownStat) + " kills)");
            }
            for(EnemyEntity enemy : Entity.enemies){
                enemy.tick(enemy);
            }
            List<Projectile> deleteCache = new ArrayList<>();
            for(Projectile projectile : Entity.projectiles){
                //float yaw2 = (float) Math.atan2(ray.getCurrentRay().getX(), -ray.getCurrentRay().z);
                Vector2f dir = projectile.getDirection();
                projectile.setVelocity(dir);
                //projectile.setPosition(new Location(projectile.getPosition().getX() / (float) (Math.sin(ray.getCurrentRay().x) + (index / 7F)), projectile.getPosition().getY(), projectile.getPosition().getZ() * (float)(Math.cos(ray.getCurrentRay().z) + (index / 6F))));

                if(projectile.getFlyingDuration() <= 0){
                    renderer.entities.get(projectile.getModel()).remove(projectile);
                    Entity.entities.remove(projectile);
                    deleteCache.add(projectile);
                }else{
                    projectile.setFlyingDuration(projectile.getFlyingDuration() - 1);
                }
            }
            for(Projectile e : deleteCache){
                Entity.projectiles.remove(e);
            }
            deleteCache.clear();
            for(Entity entity : Entity.deleteEntityCache){
                renderer.entities.get(entity.getModel()).remove(entity);
                Entity.removeEntity(entity);
                try{
                    Entity.projectiles.remove(entity);
                    Entity.enemies.remove(entity);
                }catch(Exception ignored){

                }
            }
            renderer.render(light, camera);
            ParticleMaster.render(camera);
            guiRenderer.render();
            //System.out.println(player.getRotation().toString());
            if(holdedEntity != null){
                if(ray.getCurrentTerrainPoint() != null) {
                    holdedEntity.setPosition(new Location(ray.getCurrentTerrainPoint().x, ray.getCurrentTerrainPoint().y, ray.getCurrentTerrainPoint()
                            .z));
                }
            }
            if(Entity.enemies.size() <= 0){
                Player.waveTest++;
                GUIText.replaceText("Wave:", "Wave: " + Player.waveTest);
                Player.waveEnded = true;
            }
            if(Player.waveEnded){
                System.out.println("NIFGA");
                for(int i = 0; i < (Player.waveTest * 1.5) + 5; i++){
                    int randomX = ThreadLocalRandom.current().nextInt(-100,100 + 1);
                    int randomZ = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
                    EnemyEntity enemyEntity = new EnemyEntity(models.trumpEnemy(), new Location(Player.player.getPosition().getX() + randomX, Player.player.getPosition().getY() + 5, Player.player.getPosition().getZ() + randomZ), 180,0,180, 4,10);
                    Main.renderer.addEntity(enemyEntity);
                }
                Player.waveEnded = false;
            }


            //light.setPosition(camera.getPosition());
            //System.out.println(MathHelper.distanceBetweenObjects(new Location(1,5,1), camera.getPosition()));
            //System.out.println(camera.getPosition().toString());
            TextMaster.render();
            DisplayManager.updateDisplay();
        }
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.deleteVAOandVBOs();
    }


    public static void main(String[] args) {
        new Main();

    }

}
