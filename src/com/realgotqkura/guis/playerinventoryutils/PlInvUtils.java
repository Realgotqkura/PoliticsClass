package com.realgotqkura.guis.playerinventoryutils;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.entities.Player;
import com.realgotqkura.guis.GUIS;

public class PlInvUtils {

    //sets an item to a specific slot
    //Name is the name of the texture of the item *Will change to an item class soon*
    public static void setItem(int slot, String name, Loader loader){
        GUIS.guis.get(0).setTexture(loader.loadTexture(name + "SlotTxt"));
        GUIS.guis.get(0).setName(name);
        GUIS.slotTextures.put(slot, null);
        Player.addItemInCursor(true);
    }
}
