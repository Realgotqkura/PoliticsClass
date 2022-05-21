package com.realgotqkura.engine;

import com.realgotqkura.entities.Camera;
import com.realgotqkura.entities.Entity;
import com.realgotqkura.entities.Light;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.shader.StaticShader;
import com.realgotqkura.shader.TerrainShader;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.LWColor;
import org.lwjgl.opengl.GL11;
import org.lwjglx.util.vector.Matrix4f;

import java.util.*;

public class MasterRenderer {

    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1F;
    private static final float FAR_PLANE = 400f;
    public LWColor plainColor = new LWColor(255,10,50);

    private Matrix4f projectionMatrix;
    private StaticShader shader = new StaticShader();
    private EntityRenderer entityRenderer;

    //Terrain renderer
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    public List<Entity> entityList = new ArrayList<>();
    public Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(){
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(Light light, Camera camera){
        //255,10,50 RGB Tsukuyomi
        prepare(plainColor); //Color of background
        shader.start();
        shader.loadSkyColor(plainColor);
        shader.loadLight(light);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColor(plainColor);
        terrainShader.loadLight(light);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.renderTerrain(terrains);
        terrainShader.stop();
        //entities.clear();
    }

    public void addTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void addEntity(Entity entity){
        for(TexturedModel model : entities.keySet()){
            if(entity.getModel().equals(model)){
                entities.get(model).add(entity);
                entityList.add(entity);
                return;
            }
        }
        List<Entity> newTextureEntity = new ArrayList<>(Collections.singletonList(entity));
        entities.put(entity.getModel(), newTextureEntity);
    }

    public void cleanUp(){
        entities.clear();
        terrains.clear();
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare(LWColor color){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT); //Clears the color of previous frame
        GL11.glClearColor((float) color.getRed() / 255,(float) color.getGreen() / 255,(float) color.getBlue() / 255, 1.0F); //Background color | 1 is the max value per param
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }
}
