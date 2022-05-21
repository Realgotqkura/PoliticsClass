package com.realgotqkura.engine;

import com.realgotqkura.entities.Entity;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.shader.TerrainShader;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.textures.ModelTexture;
import com.realgotqkura.utilities.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void renderTerrain(List<Terrain> terrainList){
        for(Terrain terrain : terrainList){
            prepareTerrain(terrain);
            loadTerrain(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT,0);
            unbindTModels();
        }
    }

    private void prepareTerrain(Terrain terrain){
        RawModel Rmodel = terrain.getModel();
        GL30.glBindVertexArray(Rmodel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = terrain.getTexture();
        shader.loadShineVars(texture.getShinyDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
    }

    private void loadTerrain(Terrain terrain){
        Matrix4f matrix = MathHelper.createTransformMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0,
                0, 0, 1);
        shader.loadMatrix(matrix);
    }

    private void unbindTModels(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
}
