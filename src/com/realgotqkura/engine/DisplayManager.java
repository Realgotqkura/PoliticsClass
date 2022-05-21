package com.realgotqkura.engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class DisplayManager {


    //public static final int FPS_CAP = 60;
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final String title = "Game";
    public static long window;

    private static long lastFrameTime;
    private static float deltaTime;


    public static void createDisplay(){
        if(!GLFW.glfwInit()){
            System.out.println("GLFW isn't initialized!");
            return;
        }

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, title, 0,0);

        if(window == 0){
            System.out.println("Window wasn't created!");
            return;
        }

        GLFWVidMode videos = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        assert videos != null;
        GLFW.glfwSetWindowPos(window, (videos.width() - WIDTH) / 2, (videos.height() -HEIGHT) / 2);
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);
        GLFW.glfwSwapInterval(1);
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);

        GL.createCapabilities();
        lastFrameTime = getFrameTime();
        glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public static void updateDisplay(){
        GLFW.glfwPollEvents();
        swapBuffers(); //If game sucks, try removing this
        long currentFrameTime = getFrameTime();
        deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static void swapBuffers(){
        GLFW.glfwSwapBuffers(window);
    }

    public static boolean shouldClose(){
        return GLFW.glfwWindowShouldClose(window);
    }

    public static float getDelta(){
        return deltaTime;
    }

    public static long getFrameTime(){
        return (long) (GLFW.glfwGetTime() * 1000.0D) * 1000 / 1000L;
    }
}
