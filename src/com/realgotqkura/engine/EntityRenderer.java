package com.realgotqkura.engine;

import com.realgotqkura.entities.Entity;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.shader.StaticShader;
import com.realgotqkura.textures.ModelTexture;
import com.realgotqkura.utilities.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix4f;

import java.util.List;
import java.util.Map;

public class EntityRenderer {


    public Matrix4f projectionMatrix;
    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

    }


    public void render(Map<TexturedModel, List<Entity>> entities){
        for(TexturedModel model : entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> sameTextureEntities = entities.get(model);
            for(Entity entity : sameTextureEntities){
                prepareEntity(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT,0);
            }
            unbindTModels();
        }
    }

    private void prepareTexturedModel(TexturedModel model){
        RawModel Rmodel = model.getRawModel();
        GL30.glBindVertexArray(Rmodel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        if(texture.isTransparent()){
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLight(texture.isUsingFakeLight());
        shader.loadShineVars(texture.getShinyDamper(), texture.getReflectivity());
        GL13.glActiveTexture(model.getTexture().getTextureID());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
    }

    private void prepareEntity(Entity entity){
        Matrix4f matrix = MathHelper.createTransformMatrix(entity.getPosition().toVector3f(), entity.getRotX(),
                entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadMatrix(matrix);
    }

    private void unbindTModels(){
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }


}
