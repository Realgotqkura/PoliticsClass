package com.realgotqkura.engine;

import com.realgotqkura.models.RawModel;
import org.lwjgl.opengl.*;
import org.lwjglx.BufferUtils;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    List<Integer> vaos = new ArrayList<>();
    List<Integer> vbos = new ArrayList<>();
    List<Integer> textures = new ArrayList<>();

    //Positions - list of vertexes (x,y,z)
    public RawModel loadtoVao(float[] positions,float[] textureCoords, float[] normals, int[] indices){
        int vaoID = createVao();
        bindIndeciseVBO(indices);
        storeDataIntAttribList(0,3, positions);
        storeDataIntAttribList(1,2, textureCoords);
        storeDataIntAttribList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length); //Dividing by 3 because every vertex has (x,y,z)

    }

    public RawModel guiLoadToVao(float[] positions){
        int vaoID = createVao();
        storeDataIntAttribList(0,2,positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / 2);
    }

    public int FontloadtoVao(float[] positions,float[] textureCoords){
        int vaoID = createVao();
        storeDataIntAttribList(0,2, positions);
        storeDataIntAttribList(1,2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    public RawModel depricatedLoadToVao(float[] positions, float[] textureCoords, int[] indices){
        int vaoID = createVao();
        bindIndeciseVBO(indices);
        storeDataIntAttribList(0,3, positions);
        storeDataIntAttribList(1,2, textureCoords);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }


    public int loadTexture(String textureName){
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + textureName + ".png"));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
        }catch(IOException e){
            e.printStackTrace();
        }
        assert texture != null;
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    public int createVao(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void bindIndeciseVBO(int[] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    public void storeDataIntAttribList(int attributeNumber,int size, float[] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT,false,0,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //Unbind vbo

    }


    //Use when finished using vao
    private void unbindVAO(){
        GL30.glBindVertexArray(0); //0 will unbind it
    }

    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void deleteVAOandVBOs(){
        for(int vao : vaos){
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos){
            GL15.glDeleteBuffers(vbo);
        }
        for(int texture : textures){
            GL11.glDeleteTextures(texture);
        }
    }

}
