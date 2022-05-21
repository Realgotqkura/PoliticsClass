package com.realgotqkura.utilities;

import com.realgotqkura.engine.DisplayManager;

public class Time {

    public static float deltaTime(){
        return DisplayManager.getDelta();
    }
}
