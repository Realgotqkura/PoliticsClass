package com.realgotqkura.guis;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.entities.Player;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.utilities.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.util.vector.Matrix;
import org.lwjglx.util.vector.Matrix4f;

import java.util.List;

public class GUIRenderer {

    private final RawModel guiModel;
    private GuiShader shader;

    public GUIRenderer(Loader loader){
        float[] positions = {-1,1,-1,-1,1,1,1,-1};
        guiModel = loader.guiLoadToVao(positions);
        shader = new GuiShader();
    }

    public void render(){
        shader.start();
        GL30.glBindVertexArray(guiModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for(GuiTexture gui : GUIS.playerInventoryGUIs){
            if(gui.getTexture() != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
                Matrix4f matrix4f = MathHelper.create2DTransformationMatrix(gui.getPosition(), gui.getScale(), gui.getRotation());
                shader.loadTransformation(matrix4f);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiModel.getVertexCount());
            }
        }
        for(GuiTexture gui : GUIS.shopGUI){
            if(gui.getTexture() != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
                Matrix4f matrix4f = MathHelper.create2DTransformationMatrix(gui.getPosition(), gui.getScale(), gui.getRotation());
                shader.loadTransformation(matrix4f);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiModel.getVertexCount());
            }
        }
        for(int i = 0; i < GUIS.slotTextures.size(); i++){
            GuiTexture gui = GUIS.slotTextures.get(i);
            if(gui != null) {
                if(Player.insideAGUI){
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
                    Matrix4f matrix4f = MathHelper.create2DTransformationMatrix(gui.getPosition(), gui.getScale(), gui.getRotation());
                    shader.loadTransformation(matrix4f);
                    GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiModel.getVertexCount());
                }
            }
        }
        for(GuiTexture gui : GUIS.guis){
            if(gui.getTexture() != 0) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
                Matrix4f matrix4f = MathHelper.create2DTransformationMatrix(gui.getPosition(), gui.getScale(), gui.getRotation());
                shader.loadTransformation(matrix4f);
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, guiModel.getVertexCount());
            }
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp(){
        shader.cleanUp();
    }
}
