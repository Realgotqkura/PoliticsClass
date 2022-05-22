package com.realgotqkura.entities;

import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.engine.Loader;
import com.realgotqkura.engine.MasterRenderer;
import com.realgotqkura.engine.testing.TexturedModels;
import com.realgotqkura.engine.testing.VertexListsOfShapes;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontRendering.TextMaster;
import com.realgotqkura.guis.GUIRenderer;
import com.realgotqkura.guis.GUIS;
import com.realgotqkura.guis.GuiTexture;
import com.realgotqkura.guis.playerinventoryutils.PlInvUtils;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity{

    public static Player player;
    public static final int MAX_HEALTH = 50;
    public static int health = 50;
    private static final int WALK_SPEED = 10;
    private static final int RUN_SPEED = 20;
    private static final float SIDEWAYS_SPEED = 20;
    private static final int GRAVITY = -50;
    private static final int JUMP_POWER = 30;

    public static boolean insideAGUI = false;
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

    public Player(TexturedModel model, Location loc, float rotX, float rotY, float rotZ, float scale, Loader loader) {
        super(model, loc, rotX, rotY, rotZ, scale);
        this.loader = loader;
        player = this;
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


    public void inputs(){ //Inputs are upside down cuz idk
        TexturedModels models = new TexturedModels(loader);
        if(GLFW.glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS){
            if(!ePressed){
                ePressed = true;
                if(insideAGUI){
                    insideAGUI = false;
                    glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    GUIS.closePlayerInventory();
                    GUIS.guis.get(0).setPosition(new Vector2f(0,0));
                    GUIS.guis.get(0).setTexture(Main.loader.loadTexture("cursor"));
                }else{
                    insideAGUI = true;
                    GUIS.loadPlayerInventory();
                    GUIS.guis.get(0).setPosition(new Vector2f(0,0));
                    //glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
            }
        }

        if(GLFW.glfwGetKey(window, GLFW_KEY_E) == GLFW_RELEASE){
            ePressed = false;
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
                if(isInsideAGUI()){
                    GuiTexture cursor = GUIS.guis.get(0);
                    for(int i = 0; i < GUIS.playerInventoryGUIs.size(); i++){
                        GuiTexture gui = GUIS.playerInventoryGUIs.get(i);
                        //System.out.println(cursor.getPosition().toString());(gui.getScale() / 2)
                        if(MathHelper.isInside2D(cursor.getPosition(), new Vector2f(gui.getPosition().x - (gui.getScale().x), gui.getPosition().y - (gui.getScale().y)),
                                new Vector2f(gui.getPosition().x + (gui.getScale().x), gui.getPosition().y + (gui.getScale().y)))){
                            System.out.println(gui.getName());
                            clickedSlot = i;

                        }
                    }
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
                }else{

                    if(Main.holdedEntity == null){
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
                        Projectile projectile = new Projectile(models.craftingTable(), player.getPosition(), 0,0,0, 0.5F, new Vector2f(player.getRotY(), player.getRotZ()), 60);
                        Main.renderer.addEntity(projectile);


                        if(waveEnded){
                            System.out.println("NIFGA");
                            for(int i = 0; i < (waveTest * 1.5) + 5; i++){
                                int randomX = ThreadLocalRandom.current().nextInt(-100,100 + 1);
                                int randomZ = ThreadLocalRandom.current().nextInt(-100, 100 + 1);
                                EnemyEntity enemyEntity = new EnemyEntity(models.trumpEnemy(), new Location(this.getPosition().getX() + randomX, this.getPosition().getY() + 5, this.getPosition().getZ() + randomZ), 180,0,180, 4);
                                Main.renderer.addEntity(enemyEntity);
                            }
                            waveEnded = false;
                        }
                    }else {
                        Main.holdedEntity = null;
                    }
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

    //Just changes the variable, doesn't actually add an item
    public static void addItemInCursor(boolean e){
        hasItemInCursor = e;
    }
}
