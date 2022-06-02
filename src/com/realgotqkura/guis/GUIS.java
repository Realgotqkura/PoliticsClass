package com.realgotqkura.guis;

import com.realgotqkura.engine.DisplayManager;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.main.Main;
import com.realgotqkura.shader.ShaderProgram;
import com.realgotqkura.utilities.RotationVector;
import com.realgotqkura.utilities.ShopVariables;
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
    public static List<GuiTexture> shopGUI = new ArrayList<>();
    public static boolean inCharacterInv = false;
    public static boolean inShopInv = false;

    public static void initGUIs(){
        guis.add(new GuiTexture(Main.loader.loadTexture("cursor"), new Vector2f(0,0), new Vector2f(0.03F,0.05F),
                new RotationVector(0,0,0), "Cursor"));
        //loadPlayerInventory();
        //closePlayerInventory();
        //guis.add(new GuiTexture(Main.loader.loadTexture("PlayerInventory"), new Vector2f(0,0), new Vector2f(0.5F,0.6F), new RotationVector(0,0,0)));
    }

    public static void loadShopGUI(){
        inShopInv = true;
        int texture = Main.loader.loadTexture("PlayerInventory");
        int Up1Texture = Main.loader.loadTexture("Damage_Upgrade");
        int Up2Texture = Main.loader.loadTexture("Coin_Upgrade");
        int Up3Texture = Main.loader.loadTexture("Ability_Upgrade");
        shopGUI.add(new GuiTexture(texture, new Vector2f(0,0), new Vector2f(0.5F,0.6F), new RotationVector(0,0,0),"Shop_Plot"));
        addIntoShop(new GuiTexture(Up1Texture, new Vector2f(-0.25F,0.3F), new Vector2f(0.1F, 0.15F), new RotationVector(0,0,0),"Upgrade_One"));
        GUIText upgrade_one_text = new GUIText("Proj. Damage +1: " + (100 * ShopVariables.UPGRADE_DAMAGE_LVL), 1.3F, Main.primaryFont, new Vector2f(0.28F,0.45F), 0.5F, false);

        addIntoShop(new GuiTexture(Up2Texture, new Vector2f(-0.25F / -1,0.3F), new Vector2f(0.1F, 0.15F), new RotationVector(0,0,0),"Upgrade_Two"));
        GUIText upgrade_two_text = new GUIText("Coin per kill +1: " + (150 * ShopVariables.UPGRADE_COIN_LVL), 1.3F, Main.primaryFont, new Vector2f(0.535F,0.45F), 0.5F, false);

        addIntoShop(new GuiTexture(Up3Texture, new Vector2f(0F,-0.24F), new Vector2f(0.1F, 0.15F), new RotationVector(0,0,0),"Ability_Upgrade"));
        GUIText upgrade_three_text = new GUIText("Ability upgrade: " + (200 * ShopVariables.ABILITY_UPGRADE_LVL), 1.3F, Main.primaryFont, new Vector2f(0.415F,0.7F), 0.5F, false);
    }


    //Creates GUI
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

        slotTextures.put(42, new GuiTexture(Main.loader.loadTexture("NinoResized"), playerInventoryGUIs.get(42).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Nino"));
        slotTextures.put(41, new GuiTexture(Main.loader.loadTexture("MitkoResized"), playerInventoryGUIs.get(41).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Mitko"));
        slotTextures.put(40, new GuiTexture(Main.loader.loadTexture("VladiResized"), playerInventoryGUIs.get(40).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Vladi"));
        slotTextures.put(39, new GuiTexture(Main.loader.loadTexture("GoshoResized"), playerInventoryGUIs.get(39).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Gosho"));
        slotTextures.put(38, new GuiTexture(Main.loader.loadTexture("LoraResized"), playerInventoryGUIs.get(38).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Lora"));
        slotTextures.put(37, new GuiTexture(Main.loader.loadTexture("EmoResized"), playerInventoryGUIs.get(37).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Emo"));
        slotTextures.put(36, new GuiTexture(Main.loader.loadTexture("VaskoResized"), playerInventoryGUIs.get(36).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Vasko"));
        slotTextures.put(35, new GuiTexture(Main.loader.loadTexture("Ceco_Resized"), playerInventoryGUIs.get(35).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Ceco"));
        slotTextures.put(34, new GuiTexture(Main.loader.loadTexture("Lubumira_Resized"), playerInventoryGUIs.get(34).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Lubumira"));
        slotTextures.put(33, new GuiTexture(Main.loader.loadTexture("Magi_Resized"), playerInventoryGUIs.get(33).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "Magi"));
        slotTextures.put(32, new GuiTexture(Main.loader.loadTexture("NadT_Resized"), playerInventoryGUIs.get(32).getPosition(), new Vector2f(0.05F, 0.06F), new RotationVector(0,0,0), "NadT"));

    }

    public static void closePlayerInventory(){
        slotTextures.clear();
        inCharacterInv = false;
        for(int i = 0; i < playerInventoryGUIs.size(); i++){
            playerInventoryGUIs.get(i).nullifyTexture();
        }
    }

    public static void closeShop(){
        inShopInv = false;
        for(int i = 0; i < shopGUI.size(); i++){
            shopGUI.get(i).nullifyTexture();
        }
        GUIText.removeText("Proj.");
        GUIText.removeText("Coin per");
        GUIText.removeText("Ability upgrade:");
    }

    public static void loadPlayerInventory(){
        inCharacterInv = true;
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

    private static void addIntoShop(GuiTexture texture){
        shopGUI.add(texture);
    }
}
