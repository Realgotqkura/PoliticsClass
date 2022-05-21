package com.realgotqkura.terrain;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.textures.ModelTexture;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Terrain {

    public static final int SIZE = 800;
    private static final int MAX_TERRAIN_HEIGHT = 40;
    private static final int MAX_COLOR = 256 * 256 * 256;
    public static float[][] heights;

    private int x;
    private int z;
    private RawModel model;
    private ModelTexture texture;

    public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightmap){
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightmap);
    }

    public float getHeightAtLocation(float worldX, float worldZ){
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if(gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0){
            return 0;
        }
        float answer;
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        if (xCoord <= (1-zCoord)) {
            answer = MathHelper
                    .barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = MathHelper
                    .barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }

        return answer;
    }


    private RawModel generateTerrain(Loader loader, String heightmap) {

        BufferedImage map = null;
        try {
            map = ImageIO.read(new File("res/" + heightmap + ".png"));
        }catch (IOException e){
            e.printStackTrace();
        }

        int VERTEX_COUNT = map.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j,i,map);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;

                Vector3f normal = calculateNormals(j,i,map);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadtoVao(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormals(int x, int y, BufferedImage image){
        float heightL = getHeight(x-1,y,image);
        float heightR = getHeight(x+1,y,image);
        float heightD = getHeight(x, y-1, image);
        float heightU = getHeight(x,y,image);
        return new Vector3f(heightL - heightR,2f,heightD-heightU);
    }

    public static float getHeight(int x, int y, BufferedImage image){
        if(x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()){
            return 0;
        }

        float height = image.getRGB(x,y);
        height += MAX_COLOR / 2f;
        height /= MAX_COLOR / 2f;
        height *= MAX_TERRAIN_HEIGHT;
        return height;
    }



    public int getX() {
        return x;
    }


    public int getZ() {
        return z;
    }


    public RawModel getModel() {
        return model;
    }


    public ModelTexture getTexture() {
        return texture;
    }


}
