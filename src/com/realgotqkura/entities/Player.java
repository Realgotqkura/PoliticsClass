package com.realgotqkura.entities;

import com.realgotqkura.data.SaveableData;
import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.engine.Loader;
import com.realgotqkura.engine.RayCast;
import com.realgotqkura.engine.testing.TexturedModels;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.guis.GUIS;
import com.realgotqkura.guis.GuiTexture;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.NVUniformBufferUnifiedMemory;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Vector2f;

import java.nio.DoubleBuffer;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity{

    public static Player player;
    public static int MAX_HEALTH = 50;
    public static int health = 50;
    public static int WALK_SPEED = 10;
    private static final int RUN_SPEED = 20;
    private static final float SIDEWAYS_SPEED = 20;
    public static final int GRAVITY = -50;
    private static final int JUMP_POWER = 30;
    public static float playerDamageStat = 1;
    public static int playerAbilityDamageStat = 1;
    public static float playerCoinGainStat = 1;
    public static int playerCooldownStat = 0; //This stat shows by how much the cooldown of the ability will get lowered by


    public static int magiAbilityTicks = 0;
    public static float playerCoins = 0;
    public static boolean abilityInUse = false;
    public static boolean abilityOnCooldown = false;
    public static boolean insideAGUI = false;
    private TexturedModels models;
    private int currentWalkSpeed;
    private float currentSidewaysSpeed;
    private float stackedAccelerationSpeed;
    private double oldMouseX = 0, oldMouseY = 0;
    private boolean rotX = false, rotY = false;
    private boolean jumping = false;
    private boolean ran = false;        //NOT EVEN RELATED TO RUNNING
    private boolean run;
    private float speed;
    private float upwardsSpeed = 0;
    private final long window = DisplayManager.window;
    private boolean ePressed = false;
    private boolean mousePressed = false;
    private Loader loader;
    private static boolean hasItemInCursor = false;
    private int clickedSlot = 100;
    private boolean escClicked = false;
    public static boolean waveEnded = false;
    public static int waveTest = 0;
    public static String playableCharacter;
    public static String ability;
    public static int playerKills = 0;
    public static int cooldownKills = 0;
    public static boolean cooldownOnWave = false;
    public static int waveStartedAt; //Za Ceco abilityto bahti deformiraniq
    public static int abilityCooldownKills = 0; //The amount of kills needed to satisfy the cooldown. Its not the same
    //as cooldownKills. cooldownKills is just to track the amount of kills left of the cooldown;
    public static List<String> months = new ArrayList<>(Arrays.asList("January","February", "March", "April", "May", "June",
            "July", "August","September","October","November","December"));
    public static String currentMonth = "January";
    public static String lastMonth = "January";
    public static int monthIndex = 1;
    public static int lubumiraCachedEntities = 0;
    public static int currentlyBuying = 0;
    private boolean Bclicked = false;

    public Player(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, Loader loader) {
        super(model, loc, rotX, rotY, rotZ, scale);
        this.loader = loader;
        player = this;
        models = new TexturedModels(loader);
    }

    public List<Double> mouse(){
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(window, x, y);

        double newMouseX = x.get();
        double newMouseY = y.get();

        double deltaX = newMouseX - oldMouseX;
        double deltaY = newMouseY - oldMouseY;

        rotX = newMouseX != oldMouseX;
        rotY = newMouseY != oldMouseY;

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;

        return new ArrayList<>(Arrays.asList(deltaX,deltaY));
    }

    public void teleport(Location loc){
        super.setPosition(loc);
    }

    public void move(Terrain terrain){
        GuiTexture cursor = GUIS.guis.get(0);
        float terrainHeight = terrain.getHeightAtLocation(this.getPosition().getX(), this.getPosition().getZ());
        inputs();
        List<Double> xAndY = mouse();

        if(!this.isInsideAGUI()){
            //jump
            upwardsSpeed += GRAVITY * Time.deltaTime();
            super.increasePosition(0, upwardsSpeed * Time.deltaTime(), 0);
            if(this.getPosition().getY() < terrainHeight + 5){
                this.getPosition().setY(terrainHeight + 5);
                upwardsSpeed = 0;
                jumping = false;
            }

            //rotation
            if(rotX){

                super.increaseRotation(0, (float) (((xAndY.get(0) * 7) * -1) * Time.deltaTime()),0); //left and right
            }
            if(rotY && MathHelper.clamp(-88,88, super.getRotZ()) == 1) {
                super.increaseRotation(0,0,-0.5F);
                //super.increaseRotation(0, 0, (float) ((xAndY.get(1) * 6) * Time.deltaTime())); //up and down
                ran = true;
            }else if(rotY && MathHelper.clamp(-88,88, super.getRotZ()) == 2) {
                super.increaseRotation(0,0, 0.5F);
                //super.increaseRotation(0, 0, (float) ((xAndY.get(1) * 6) * Time.deltaTime())); //up and down
                ran = true;
            }
            if(!ran){
                super.increaseRotation(0, 0, (float) ((xAndY.get(1) * 7) * Time.deltaTime()));
            }




            //movement
            if(currentWalkSpeed + (stackedAccelerationSpeed / -1) < -30){
                speed = -30;
            }else{
                speed = currentWalkSpeed + (stackedAccelerationSpeed / -1);
            }


            if(jumping && Input.keys[GLFW_KEY_W]){
                speed -= 5;
            }else if(jumping && Input.keys[GLFW_KEY_S]){
                speed += 10;
            }


            float distance = speed * Time.deltaTime();
            float sidewaysDistance = currentSidewaysSpeed * Time.deltaTime();

            //float upY = (float) (distance * Math.sin(Math.toRadians(super.getRotZ())));  << For Creative mode if ever created (Moving up and down)
            float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
            float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
            float sidewaysDx = (float) (sidewaysDistance * Math.sin(Math.toRadians(super.getRotY() - 90)));
            float sidewaysDz = (float) (sidewaysDistance * Math.cos(Math.toRadians(super.getRotY()- 90)));
            super.increasePosition(dx, 0/* <- up (use upY)*/,dz); //forward and backwards
            super.increasePosition(sidewaysDx,0,sidewaysDz); //left and right
        }

        if(isInsideAGUI()){
            cursor.setPosition(new Vector2f(cursor.getPosition().x + (float) (xAndY.get(0) / 1280F), cursor.getPosition().y + (float) (xAndY.get(1) / -720F)));
            if(MathHelper.clamp(-1,1, cursor.getPosition().x) == 1){
                cursor.setPosition(new Vector2f(0.9999F, cursor.getPosition().y));
            }
            if(MathHelper.clamp(-1,1, cursor.getPosition().y) == 1){
                cursor.setPosition(new Vector2f(cursor.getPosition().x,0.9999F));
            }
            if(MathHelper.clamp(-1,1, cursor.getPosition().x) == 2){
                cursor.setPosition(new Vector2f(-0.9999F, cursor.getPosition().y));
            }
            if(MathHelper.clamp(-1,1, cursor.getPosition().y) == 2){
                cursor.setPosition(new Vector2f(cursor.getPosition().x,-0.9999F));
            }
        }

        //reset
        this.currentSidewaysSpeed = 0;
        this.currentWalkSpeed = 0;
        this.ran = false;
    }

    private void jump(){
        if(!jumping){
            upwardsSpeed = JUMP_POWER;
        }
    }

    private boolean rPressed = false;

    public void inputs(){ //Inputs are upside down cuz idk

        if(GLFW.glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS){
            if(!rPressed){
                rPressed = true;
                if(!GUIS.inShopInv){
                    GUIS.loadShopGUI();
                    insideAGUI = true;
                    GUIS.guis.get(0).setPosition(new Vector2f(0,0));
                }else{
                    GUIS.closeShop();
                    insideAGUI = false;
                    GUIS.guis.get(0).setPosition(new Vector2f(0,0));
                }
            }
        }
        if(GLFW.glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS){
            if(!ePressed && !insideAGUI){
                ePressed = true;
                switch(playableCharacter){
                    case "Nino":
                        //Invisibility ability
                        if(!abilityOnCooldown){
                            abilityInUse = true;
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (Cooldown)");
                            abilityOnCooldown = true;
                        }
                        break;
                    case "Mitko":
                        //Shuriken jutsu ability
                        if(!abilityOnCooldown){
                            for(int i = 0; i < 360; i++){
                                if(i % 20 == 0){
                                    Projectile projectile = new Projectile(models.shuriken(), player.getPosition(), 90,0,0, 0.5F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.SHURIKEN);
                                    Main.renderer.addEntity(projectile);
                                    projectile.setDirection(new Vector2f(i,0));
                                }
                            }
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (Cooldown)");
                            abilityOnCooldown = true;
                        }
                        break;
                    case "Vladi":
                        //Shuriken jutsu ability
                        if(!abilityOnCooldown){
                            abilityInUse = true;
                            Projectile projectile = new Projectile(models.shuriken(), player.getPosition(), 90,0,0, 0.5F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.AMATERASU);
                            Main.renderer.addEntity(projectile);
                            projectile.setDirection(projectile.getDirection());
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (In Use)");
                        }
                        break;
                    case "Gosho":
                        //Shuriken jutsu ability
                        if(!abilityOnCooldown){
                            Projectile projectile = new Projectile(models.bomb(), player.getPosition(), 0,0,0, 1F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.BOMB);
                            Main.renderer.addEntity(projectile);
                            projectile.setDirection(projectile.getDirection());
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (In Use)");
                            abilityOnCooldown = true;
                        }
                        break;
                    case "Lora":
                    case "Magi":
                        if(!abilityOnCooldown || !abilityInUse){
                            abilityInUse = true;
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (In Use)");
                        }
                        break;
                    case "Emo":
                        if(!abilityOnCooldown){
                            abilityOnCooldown = true;
                            this.setPosition(new Location(this.getPosition().getX() + 75 * (float) Math.sin(Math.toRadians(this.getRotY()) - 90),
                                    this.getPosition().getY(),this.getPosition().getZ() + 75 * (float) Math.cos(Math.toRadians(this.getRotY()) - 90)));
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (Cooldown " + abilityCooldownKills + " kills left)");
                        }
                        break;
                    case "Vasko":
                        if(!abilityOnCooldown){
                            abilityOnCooldown = true;
                            for(EnemyEntity enemy : Entity.enemies){
                                enemy.hurtEnemy(1 + (2 * Player.playerAbilityDamageStat), enemy); //Hardcoded 1 for now lol
                            }
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (Cooldown " + abilityCooldownKills + " kills left)");
                        }
                        break;
                    case "Ceco":
                        if(!abilityOnCooldown || !abilityInUse){
                            abilityInUse = true;
                            waveStartedAt = waveTest;
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (In Use)");
                        }
                        break;
                    case "Nad.T":
                        if(!abilityOnCooldown){
                            Timer t = new Timer();
                            long period = 1*1000; //For example 1 second
                            long delay = 1*1000; //For example 1 second
                            Main.timers.add(t);
                            t.schedule(new TimerTask() {
                                int index = 0;
                                @Override
                                public void run() {
                                    if(index < 10){
                                        for(EnemyEntity entity : Entity.enemies){
                                            EnemyEntity.Speed = 0.11F / 2;
                                            entity.hurtEnemy(0.2F, entity);
                                        }
                                    }else{
                                        EnemyEntity.Speed *= 2;
                                        Main.timers.remove(t);
                                        t.cancel();
                                    }
                                    index++;
                                }
                            }, delay, period);
                            abilityOnCooldown = true;
                            GUIText.replaceText("Ability", "Ability (E): " + ability + " (Cooldown " + abilityCooldownKills + " kills left)");
                        }
                        break;
                    case "Lubumira":
                        int random = ThreadLocalRandom.current().nextInt(0,100+1);
                        if(random == 69){
                            waveTest++;
                        }
                        if(waveTest % 2 != 0){
                            Entity.deleteEntityCache.addAll(Entity.enemies);
                            lubumiraCachedEntities += Entity.enemies.size();
                            playerCoins += Entity.enemies.size();
                        }
                        break;
                }
                }
            }

        if(GLFW.glfwGetKey(window, GLFW_KEY_E) == GLFW_RELEASE){
            ePressed = false;
        }
        if(GLFW.glfwGetKey(window, GLFW_KEY_R) == GLFW_RELEASE){
            rPressed = false;
        }
        if(Input.keys[GLFW_KEY_W] && Input.keys[GLFW_KEY_LEFT_CONTROL]){
            this.currentWalkSpeed = -RUN_SPEED;
            this.stackedAccelerationSpeed += 0.2F;
            run = true;
        }else if(Input.keys[GLFW_KEY_W]){
            this.currentWalkSpeed = -WALK_SPEED;
            this.stackedAccelerationSpeed += 0.1F;
            run = false;
        }
        if(Input.keys[GLFW_KEY_S]) {
            this.currentWalkSpeed = WALK_SPEED;
            this.stackedAccelerationSpeed = 0;
        }
        if(Input.keys[GLFW_KEY_A]){
            this.currentSidewaysSpeed = +SIDEWAYS_SPEED;
        }
        if(Input.keys[GLFW_KEY_D]){
            this.currentSidewaysSpeed = -SIDEWAYS_SPEED;
        }
        if(Input.keys[GLFW_KEY_SPACE]){
            jump();
            jumping = true;
        }
        if(Input.keys[GLFW_KEY_T]){
            teleport(new Location(0,5,0));
            stackedAccelerationSpeed = 0;
        }
        /*
        if (Input.keys[GLFW_KEY_LEFT_SHIFT]) {
            this.getPosition().setY(this.getPosition().getY() - 1);
        }                                                                <<Used for testing purposes
        if (Input.keys[GLFW_KEY_RIGHT_SHIFT]) {
            this.getPosition().setY(this.getPosition().getY() + 1);
        }
         */

        if(!Input.keys[GLFW_KEY_W] && stackedAccelerationSpeed > 0){ //Lower the acceleration speed when the W key isn't being held
            stackedAccelerationSpeed = stackedAccelerationSpeed - (stackedAccelerationSpeed / 100); //so it imitates a car slowing down i guess idk
            if(stackedAccelerationSpeed < 0.1){
                stackedAccelerationSpeed = 0;
            }
        }

        if(GLFW.glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS){
            if(!Bclicked){
                Bclicked = true;
                switch(currentlyBuying){
                    case 1:
                        if(SaveableData.EMO == 0 && SaveableData.GOLD >= 15){
                            SaveableData.EMO = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 15);
                            GUIS.slotTextures.replace(37, new GuiTexture(Main.loader.loadTexture("EmoResized"), GUIS.playerInventoryGUIs.get(37).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Emo"));
                        }
                        break;
                    case 2:
                        if(SaveableData.VASKO == 0 && SaveableData.GOLD >= 18){
                            SaveableData.VASKO = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 18);
                            GUIS.slotTextures.replace(36, new GuiTexture(Main.loader.loadTexture("VaskoResized"), GUIS.playerInventoryGUIs.get(36).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Vasko"));
                        }
                        break;
                    case 3:
                        if(SaveableData.DINAMIXO == 0 && SaveableData.GOLD >= 10){
                            SaveableData.DINAMIXO = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 10);
                            GUIS.slotTextures.replace(35, new GuiTexture(Main.loader.loadTexture("Ceco_Resized"), GUIS.playerInventoryGUIs.get(35).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Ceco"));
                        }
                        break;
                    case 4:
                        if(SaveableData.LUBUMIRA == 0 && SaveableData.GOLD >= 12){
                            SaveableData.LUBUMIRA = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 12);
                            GUIS.slotTextures.replace(34, new GuiTexture(Main.loader.loadTexture("LubumiraResized"), GUIS.playerInventoryGUIs.get(34).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Lubumira"));
                        }
                        break;
                    case 5:
                        if(SaveableData.MAGI == 0 && SaveableData.GOLD >= 13){
                            SaveableData.MAGI = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 13);
                            GUIS.slotTextures.replace(33, new GuiTexture(Main.loader.loadTexture("MagiResized"), GUIS.playerInventoryGUIs.get(33).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Magi"));
                        }
                        break;
                    case 6:
                        if(SaveableData.NAD_T == 0 && SaveableData.GOLD >= 14){
                            SaveableData.NAD_T = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 14);
                            GUIS.slotTextures.replace(32, new GuiTexture(Main.loader.loadTexture("NadTResized"), GUIS.playerInventoryGUIs.get(32).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "NadT"));
                        }
                        break;
                    case 7:
                        if(SaveableData.KRISTIAN == 0 && SaveableData.GOLD >= 6){
                            SaveableData.KRISTIAN = 1;
                            SaveableData.changeGold(SaveableData.GOLD - 6);
                            GUIS.slotTextures.replace(31, new GuiTexture(Main.loader.loadTexture("KristianResized"), GUIS.playerInventoryGUIs.get(31).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0, 0, 0), "Kristian"));
                        }
                }
            }
        }

        if(GLFW.glfwGetKey(window, GLFW_KEY_9) == GLFW_PRESS){
            SaveableData.changeGold(20);
        }

        if(GLFW.glfwGetKey(window, GLFW_KEY_B) == GLFW_RELEASE){
            Bclicked = false;
        }

        if(GLFW.glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS){
            if(isInsideAGUI()){
                GUIS.guis.get(0).setTexture(loader.loadTexture("cursor"));
                GUIS.guis.get(0).setName("Cursor");
            }else {
                org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose(DisplayManager.window, true);
            }
            escClicked = true;
        }
        if(GLFW.glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_RELEASE){
            escClicked = false;
        }

        if(GLFW.glfwGetMouseButton(window, 0) == GLFW_PRESS && !mousePressed){
            mousePressed = true;
                if(isInsideAGUI() && GUIS.inCharacterInv){
                    GuiTexture cursor = GUIS.guis.get(0);
                    for(int i = 0; i < GUIS.playerInventoryGUIs.size(); i++){
                        GuiTexture gui = GUIS.playerInventoryGUIs.get(i);
                        //System.out.println(cursor.getPosition().toString());(gui.getScale() / 2)
                        if(MathHelper.isInside2D(cursor.getPosition(), new Vector2f(gui.getPosition().x - (gui.getScale().x), gui.getPosition().y - (gui.getScale().y)),
                                new Vector2f(gui.getPosition().x + (gui.getScale().x), gui.getPosition().y + (gui.getScale().y)))){
                            System.out.println(gui.getName());
                            switch(i){
                                case 42:
                                    playableCharacter = "Nino";
                                    ability = "Invisibility";
                                    abilityCooldownKills = 5;
                                    break;
                                case 41:
                                    playableCharacter = "Mitko";
                                    ability = "Shuriken jutsu";
                                    abilityCooldownKills = 5;
                                    GUIText passiveMitko = new GUIText("Passive: 25% chance for double cooldown reduction", 2.5F, Main.primaryFont, new Vector2f(0,0.8F), 1F, false);
                                    break;
                                case 40:
                                    playableCharacter = "Vladi";
                                    ability = "Amaterasu";
                                    abilityCooldownKills = 7;
                                    break;
                                case 39:
                                    playableCharacter = "Gosho";
                                    ability = "Flaming Airstrike";
                                    abilityCooldownKills = 5;
                                    WALK_SPEED += (WALK_SPEED / 2);
                                    GUIText passiveGosho = new GUIText("Passive: 1.5x Speed", 3, Main.primaryFont, new Vector2f(0,0.8F), 0.7F, false);
                                    break;
                                case 38:
                                    playableCharacter = "Lora";
                                    ability = "20/20 Vision";
                                    abilityCooldownKills = 4;
                                    playerCoinGainStat++;
                                    GUIText passiveLora = new GUIText("Passive: +1 Coin per kill", 3, Main.primaryFont, new Vector2f(0,0.8F), 0.7F, false);
                                    break;
                                case 37:
                                    if(SaveableData.EMO == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 15G (B)");
                                            currentlyBuying = 1;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 15G (B)");
                                            currentlyBuying = 1;
                                        }
                                    }else {
                                        playableCharacter = "Emo";
                                        ability = "Flashy Raijin";
                                        abilityCooldownKills = 3; //Gonna be upgradeable to 1 or 0 idk yet
                                        GUIText passiveEmo = new GUIText("Passive: 2x damage every 3rd wave", 3F, Main.primaryFont, new Vector2f(0, 0.8F), 0.7F, false);
                                    }
                                    break;
                                case 36:
                                    if(SaveableData.VASKO == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 18G (B)");
                                            currentlyBuying = 2;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 18G (B)");
                                            currentlyBuying = 2;
                                        }
                                    }else {
                                        playableCharacter = "Vasko";
                                        ability = "Earthquake"; //Mnogo milo
                                        abilityCooldownKills = 4; //Early game trash but when shop is introduced it will be juicy
                                        GUIText passiveVasko = new GUIText("Passive: 2x Enemy HP and 1.5x Enemy Speed", 2.5F, Main.primaryFont, new Vector2f(0, 0.8F), 1F, false);
                                    }
                                    break;
                                case 35:
                                    if(SaveableData.DINAMIXO == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 10G (B)");
                                            currentlyBuying = 3;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 10G (B)");
                                            currentlyBuying = 3;
                                        }
                                    }else {
                                        playableCharacter = "Ceco";
                                        ability = "Deformed Brain";
                                        cooldownOnWave = true;
                                        GUIText statIncreaseTxt = new GUIText("Last stat UP:", 3, Main.primaryFont, new Vector2f(0, 0.8F), 0.7F, false);
                                    }
                                    break;
                                case 34:
                                    if(SaveableData.LUBUMIRA == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 12G (B)");
                                            currentlyBuying = 4;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 12G (B)");
                                            currentlyBuying = 4;
                                        }
                                    }else {
                                        playableCharacter = "Lubumira";
                                        ability = "KAMUI";
                                        abilityCooldownKills = 0;
                                        GUIText passiveLubumira = new GUIText("Passive: On ability Use 1% chance for +1 wave", 2.5F, Main.primaryFont, new Vector2f(0, 0.8F), 1F, false);
                                    }
                                    break;
                                case 33:
                                    if(SaveableData.MAGI == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 13G (B)");
                                            currentlyBuying = 5;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 13G (B)");
                                            currentlyBuying = 5;
                                        }
                                    }else {
                                        playableCharacter = "Magi";
                                        ability = "Negative Attraction";
                                        abilityCooldownKills = 3;
                                        GUIText passiveMagi = new GUIText("Passive: Each wave +1HP", 3F, Main.primaryFont, new Vector2f(0, 0.8F), 0.7F, false);
                                    }
                                    break;
                                case 32:
                                    if(SaveableData.NAD_T == 0){
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 14G (B)");
                                            currentlyBuying = 6;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.73F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 14G (B)");
                                            currentlyBuying = 6;
                                        }
                                    }else {
                                        playableCharacter = "Nad.T";
                                        ability = "Rose Thorns";
                                        abilityCooldownKills = 6;
                                        GUIText passiveNAdt = new GUIText("Passive: Rose projectile On hit: -10% enemy speed", 2.5F, Main.primaryFont, new Vector2f(0, 0.8F), 1F, false);
                                    }
                                    break;
                                case 31:
                                    if(SaveableData.KRISTIAN == 0) {
                                        try{
                                            GUIText.replaceTextLoc("Shop:", new Vector2f(0.76F, 0F));
                                            GUIText.replaceText("Shop:", "Buy: 6G (B)");
                                            currentlyBuying = 7;
                                        }catch(NullPointerException e){
                                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.76F, 0F));
                                            GUIText.replaceText("Buy:", "Buy: 6G (B)");
                                            currentlyBuying = 7;
                                        }
                                    }else {
                                        playableCharacter = "Kristian";
                                        ability = "Gay Pride";
                                        abilityCooldownKills = 0;
                                        GUIText month = new GUIText("Current Month: January", 3, Main.primaryFont, new Vector2f(0, 0.8F), 0.7F, false);
                                        Timer timer = new Timer();
                                        long period = 1 * 10 * 1000; //For example 10 second
                                        long delay = 1 * 10 * 1000; //For example 10 second
                                        Main.timers.add(timer);
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                lastMonth = months.get(monthIndex);
                                                monthIndex++;
                                                currentMonth = months.get(monthIndex);
                                                if (monthIndex >= 11) {
                                                    monthIndex = 0;
                                                }
                                            }
                                        }, delay, period);
                                        break;
                                    }
                            }

                        }
                    }
                    if(playableCharacter != null){
                        GUIText text = new GUIText("Character: " + playableCharacter,3, Main.primaryFont, new Vector2f(0,0.2F), 0.5F, false);
                        GUIText AbiltyT = new GUIText("Ability (E): " + ability + " (Ready)",3, Main.primaryFont, new Vector2f(0,0.9F), 1F, false);
                        if(playableCharacter.equalsIgnoreCase("ceco")){
                            GUIText.replaceText("Ability (E):", "Ability: " + ability + " (Always Active)");
                        }else if(playableCharacter.equalsIgnoreCase("kristian")){
                            GUIText.replaceText("Ability (E):", "Ability: " + ability + " (Waiting for June)");
                        }else if(playableCharacter.equalsIgnoreCase("lubumira")){
                            GUIText.replaceText("Ability (E):", "Ability: " + ability + " (Only on Odd waves)");
                        }
                        insideAGUI = false;
                        glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                        GUIS.closePlayerInventory();
                        GUIS.guis.get(0).setPosition(new Vector2f(0,0));
                        GUIS.guis.get(0).setTexture(Main.loader.loadTexture("cursor"));
                        GUIText.replaceText(":Gold", Math.round(Math.floor(playerCoins)) + " :Coins");
                        try{
                            GUIText.replaceTextLoc("Buy:", new Vector2f(0.82F, 0F));
                            GUIText.replaceText("Buy:", "Shop: R");
                        }catch(NullPointerException ignored){

                        }
                    }
                    /*
                    if(clickedSlot < 69 && clickedSlot > 0){
                        try{
                            if(GUIS.playerInventoryGUIs.get(clickedSlot) != null && !hasItemInCursor){
                               switch(GUIS.slotTextures.get(clickedSlot - 1).getName()) {
                                   case "Tree":
                                       PlInvUtils.setItem(clickedSlot - 1, "Tree", loader);
                                       break;
                                   case "Rock":
                                       PlInvUtils.setItem(clickedSlot - 1, "Rock", loader);
                                       break;
                               }
                                clickedSlot = 100;
                                return;
                            }
                        }catch(NullPointerException ignored){
                        }
                    }

                    try{
                        if(GUIS.guis.get(0).getTexture() != loader.loadTexture("cursor")){
                            if(GUIS.playerInventoryGUIs.get(clickedSlot) != null){
                                if(GUIS.slotTextures.get(clickedSlot - 1) == null){
                                    if(!GUIS.guis.get(0).getName().equals("Cursor")){
                                        GUIS.slotTextures.put(clickedSlot - 1, new GuiTexture(loader.loadTexture(GUIS.guis.get(0).getName() + "SlotTxt"),GUIS.playerInventoryGUIs.get(clickedSlot).getPosition(), new Vector2f(0.03F,0.05F), new RotationVector(0,0,0), GUIS.guis.get(0).getName()));
                                        GUIS.guis.get(0).setTexture(loader.loadTexture("cursor"));
                                        GUIS.guis.get(0).setName("Cursor");
                                        hasItemInCursor = false;
                                    }
                                }
                            }
                        }
                    }catch(IndexOutOfBoundsException ignored){

                    }
                     */
                }else if(insideAGUI && GUIS.inShopInv){
                    GuiTexture cursor = GUIS.guis.get(0);
                    for(int i = 0; i < GUIS.shopGUI.size(); i++) {
                        GuiTexture gui = GUIS.shopGUI.get(i);
                        //System.out.println(cursor.getPosition().toString());(gui.getScale() / 2)
                        if (MathHelper.isInside2D(cursor.getPosition(), new Vector2f(gui.getPosition().x - (gui.getScale().x), gui.getPosition().y - (gui.getScale().y)),
                                new Vector2f(gui.getPosition().x + (gui.getScale().x), gui.getPosition().y + (gui.getScale().y)))) {
                            System.out.println(gui.getName());
                            switch(gui.getName()){
                                case "Upgrade_One":
                                    if(playerCoins >= 100 * ShopVariables.UPGRADE_DAMAGE_LVL){
                                        playerDamageStat++;
                                        playerCoins -= 100 * ShopVariables.UPGRADE_DAMAGE_LVL;
                                        ShopVariables.UPGRADE_DAMAGE_LVL++;
                                        GUIText.replaceText(":Coins", Math.round(Math.floor(playerCoins)) + " :Coins");
                                        GUIText.replaceText("Proj.", "Proj. Damage +1: " + (100 * ShopVariables.UPGRADE_DAMAGE_LVL));
                                    }
                                    break;
                                case "Upgrade_Two":
                                    if(playerCoins >= 150 * ShopVariables.UPGRADE_COIN_LVL){
                                        playerCoinGainStat++;
                                        playerCoins -= 150 * ShopVariables.UPGRADE_COIN_LVL;
                                        ShopVariables.UPGRADE_COIN_LVL++;
                                        GUIText.replaceText(":Coins", Math.round(Math.floor(playerCoins)) + " :Coins");
                                        GUIText.replaceText("Coin per", "Coin per kill +1: " + (150 * ShopVariables.UPGRADE_COIN_LVL));
                                    }
                                    break;
                                case "Ability_Upgrade":
                                    if(playerCoins >= 200 * ShopVariables.ABILITY_UPGRADE_LVL && ShopVariables.ABILITY_UPGRADE_LVL <= 4){
                                        playerCooldownStat++;
                                        playerAbilityDamageStat++;
                                        playerCoins -= 200 * ShopVariables.ABILITY_UPGRADE_LVL;
                                        ShopVariables.ABILITY_UPGRADE_LVL++;
                                        GUIText.replaceText(":Coins", Math.round(Math.floor(playerCoins)) + " :Coins");
                                        GUIText.replaceText("Ability upgrade:", "Ability upgrade: " + (200 * ShopVariables.ABILITY_UPGRADE_LVL));
                                    }
                                    break;
                            }
                        }
                    }
                }else{
                    if(playableCharacter.equalsIgnoreCase("nad.t")){
                        if(Entity.projectiles.size() <= 3){
                            Main.renderer.addEntity(new Projectile(models.Rose(), new Location(player.getPosition().getX() + 0.2F, player.getPosition().getY(), player.getPosition().getZ() + 0.2F), 0,0,0, 0.05F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.THORNS));
                        }
                    }else if(!playableCharacter.equalsIgnoreCase("kristian") && !currentMonth.equalsIgnoreCase("June")){
                        if(Entity.projectiles.size() <= 3){
                            Main.renderer.addEntity(new Projectile(models.craftingTable(), new Location(player.getPosition().getX() + 0.2F, player.getPosition().getY(), player.getPosition().getZ() + 0.2F), 180,0,0, 0.5F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.DEFAULT));
                            if(Player.abilityInUse && playableCharacter.contains("Lora")){
                                Main.renderer.addEntity(new Projectile(models.craftingTable(), new Location(player.getPosition().getX() + 0.2F, player.getPosition().getY() + 1, player.getPosition().getZ() + 0.2F), 180,0,0, 0.5F, new Vector2f(player.getRotY(), player.getRotZ()), 60, ProjectileType.DEFAULT));
                            }
                        }
                    }


                        /*
                        for(Entity entity : Entity.entities){
                            if(MathHelper.isInside(Location.Vec3toLocation(Main.ray.getCurrentTerrainPoint()),
                                    new Location(entity.getPosition().getX() - 2, entity.getPosition().getY() + (entity.getScale() * 3), entity.getPosition().getZ()  - 2),
                                    new Location(entity.getPosition().getX() + 2, entity.getPosition().getY() - (entity.getScale() * 3), entity.getPosition().getZ() + 2))) {
                                //On click it adds the icon version of this material in the inventory
                                switch(entity.getModel().getName()){
                                    case "TrumpEnemy":
                                        Entity.deleteEntityCache.add(entity);

                                        if(enemies.isEmpty()){
                                            for(GUIText text : TextMaster.texts.get(Main.primaryFont)){
                                                if(text.getText().contains("Wave:")){
                                                    waveTest++;
                                                    text.replaceText(text, "Wave: " + waveTest);
                                                    waveEnded = true;
                                                    break;
                                                }
                                            }
                                            waveEnded = true;
                                        }
                                        break;

                                }
                                Main.renderer.addEntity(entity);
                            }
                        }// end of for loop

                     */

                    }

        }


        if(GLFW.glfwGetMouseButton(window, 0) == GLFW_RELEASE){
            mousePressed = false;
        }


        if(Input.keys[GLFW_KEY_R]){
            GUIS.guis.get(0).setTexture(Main.loader.loadTexture("cursor"));
        }
        if(Input.keys[GLFW_KEY_F]){
            this.setRotZ(0);
        }

    }



    public float getVelocity(){
        return speed;
    }

    public void setVelocity(float velocity){
        this.speed = velocity;
    }

    public boolean isJumping(){
        return jumping;
    }

    public boolean isRunning(){
        return run;
    }

    public RotationVector getRotation(){
        return new RotationVector(this.getRotX(),this.getRotY(),this.getRotZ());
    }

    public boolean isInsideAGUI(){
        return insideAGUI;
    }

    public static boolean isCursorEmpty(){
        return hasItemInCursor;
    }

    public static void addCoin(float coin){
        playerCoins += coin;
        GUIText.replaceText(":Coin", Math.round(Math.floor(playerCoins)) + " :Coins");
    }

    //Just changes the variable, doesn't actually add an item
    public static void addItemInCursor(boolean e){
        hasItemInCursor = e;
    }
}
