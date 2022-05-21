package com.realgotqkura.entities;
import com.realgotqkura.utilities.Location;

public class Camera {

    private Location position = new Location(0,10,0);
    private int pitch;
    private int yaw;
    private int tilt;

    public Camera(){}

    public void move(Player player){
        this.position = player.getPosition();
        this.yaw = (int) -player.getRotY();
        this.pitch = (int) player.getRotZ();
        //player.setRotY(yaw);
    }

    public Location getPosition() {
        return position;
    }

    public int getPitch() {
        return pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public int getTilt() {
        return tilt;
    }
}
