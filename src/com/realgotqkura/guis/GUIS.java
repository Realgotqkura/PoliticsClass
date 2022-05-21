package com.realgotqkura.guis;

import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.main.Main;
import com.realgotqkura.utilities.RotationVector;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Vector2f;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class GUIS {


    private static Vector2f anchor;
    /*
    GUITexture scale and position:

    (0;0) which is the position is the middle of the screen.
    the maximum position you can set it so the middle of the texture doesn't go offscreen is 1 so (1;1)

    The scale just gets the middle (0;0) or any position and gets the 2 values from the Vec2 scale and puts the corners in those
    positions like a 2D plane so if the middle is (0;0) and the scale is (0.5,0.5) the position of the corners are
    going to me (0.5;0.5), (-0.5;0.5), (-0.5,-0.5) and (0.5,-0.5);
     */

    public static List<GuiTexture> guis = new ArrayList<>();
    public static List<GuiTexture> playerInventoryGUIs = new ArrayList<>();
    public static HashMap<Integer, GuiTexture> slotTextures = new HashMap<>();

    public static void initGUIs(){
        guis.add(new GuiTexture(Main.loader.loadTexture("cursor"), new Vector2f(0,0), new Vector2f(0.03F,0.05F),
                new RotationVector(0,0,0), "Cursor"));
        loadPlayerInventory();
        closePlayerInventory();
        //guis.add(new GuiTexture(Main.loader.loadTexture("PlayerInventory"), new Vector2f(0,0), new Vector2f(0.5F,0.6F), new RotationVector(0,0,0)));
    }

    private static void plInventoryAlg(int xSlots, int ySlots){
        int texture = Main.loader.loadTexture("PlayerInventory");
        Vector2f[] anchors = new Vector2f[xSlots + 1];
        anchors[0] = anchor;
        int row = 0;
        for(int i = 0; i < xSlots; i++){
            addIntoPlayerInventory(new GuiTexture(texture, new Vector2f(anchors[i].x + 0.15F, anchors[i].y), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Inv_Slot" + playerInventoryGUIs.size()));
            anchors[i + 1] =  new Vector2f(anchors[i].x + 0.15F, anchors[i].y);
        }
        float newY = 0;
        for(int i = 0; i < ySlots; i++){
            for(Vector2f anchor : anchors){
                addIntoPlayerInventory(new GuiTexture(texture, new Vector2f(anchor.x, anchor.y + 0.15F * (row + 1)), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Inv_Slot" + playerInventoryGUIs.size()));

            }

            row++;
        }
        for(int i = 0; i < 42; i++){
            slotTextures.put(i, null);
        }


    }

    public static void closePlayerInventory(){
        for(int i = 0; i < playerInventoryGUIs.size(); i++){
            playerInventoryGUIs.get(i).nullifyTexture();
        }
    }

    public static void clickPlayerInventory(){

    }


    public static void loadPlayerInventory(){
        int texture = Main.loader.loadTexture("PlayerInventory");
        try{
            playerInventoryGUIs.get(0).setTexture(texture);
            for(int i = 1; i < playerInventoryGUIs.size(); i++){
                playerInventoryGUIs.get(i).setTexture(texture);
            }
        }catch(IndexOutOfBoundsException e){
            addIntoPlayerInventory(new GuiTexture(texture, new Vector2f(0,0), new Vector2f(0.5F,0.6F), new RotationVector(0,0,0),"Inv_Plot"));
            addIntoPlayerInventory(new GuiTexture(texture, new Vector2f( -0.5F - (-0.13F), -0.5F - (-0.06F)), new Vector2f(0.05F,0.06F), new RotationVector(0,0,0), "Inv_Slot"));
            anchor = playerInventoryGUIs.get(1).getPosition();
            plInventoryAlg(5,6);

        }
    }

    private static void addIntoPlayerInventory(GuiTexture texture){
        playerInventoryGUIs.add(texture);
    }
}
