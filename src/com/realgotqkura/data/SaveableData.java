package com.realgotqkura.data;

import com.realgotqkura.fontMeshCreator.GUIText;

import java.util.HashMap;
import java.util.Map;

public class SaveableData {

    //public static float COINS; //Ingame currency used for the shop; Gain <some> every kill
    public static int GOLD = 0; //Used to buy characters. Gain 1 every 5 waves
    public static int VASKO = 0;
    public static int MAGI = 0;
    public static int EMO = 0;
    public static int NAD_T = 0;
    public static int DINAMIXO = 0;
    public static int LUBUMIRA = 0;
    public static int KRISTIAN = 0;

    public static void changeGold(int gold){
        GOLD = gold;
        try{
            GUIText.replaceText(":Gold", GOLD + ":Gold");
        }catch(NullPointerException ignored){

        }
    }
}
