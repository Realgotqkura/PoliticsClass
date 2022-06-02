package com.realgotqkura.engine.testing;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.engine.ModelData;
import com.realgotqkura.engine.OBJLoader;
import com.realgotqkura.models.RawModel;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.textures.ModelTexture;

public class TexturedModels {

    private Loader loader;

    public TexturedModels(Loader loader){
        this.loader = loader;
    }

    public TexturedModel bomb(){
        ModelTexture table = new ModelTexture(loader.loadTexture("BombYes"));
        //table.setTransparency(true);
        //table.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("Bomb");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), table, "bomb");
    }

    public TexturedModel shuriken(){
        ModelTexture table = new ModelTexture(loader.loadTexture("metal"));
        table.setTransparency(true);
        table.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("shuriken");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), table, "shuriken");
    }

    public TexturedModel craftingTable(){
        ModelTexture table = new ModelTexture(loader.loadTexture("CraftingTableTex"));
        table.setTransparency(true);
        table.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("CraftingTable");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), table, "CraftingTable");
    }

    public TexturedModel trumpEnemy(){
        ModelTexture trump = new ModelTexture(loader.loadTexture("TrumpColors"));
        ModelData data = OBJLoader.loadOBJModel("TrumpEnemy");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), trump, "TrumpEnemy");
    }

    public TexturedModel treeModel(boolean setBig){
        ModelTexture grass = null;
        if(setBig){
            grass = new ModelTexture(loader.loadTexture("TreeTex"));
        }else {
            grass = new ModelTexture(loader.loadTexture("TreeTex2"));
        }
        grass.setShinyDamper(10);
        grass.setReflectivity(0);
        ModelData data = OBJLoader.loadOBJModel("Tree");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), grass, "tree");
    }

    public TexturedModel rockModel(){
        ModelTexture rock = new ModelTexture(loader.loadTexture("RockTexture"));
        rock.setReflectivity(0);
        rock.setShinyDamper(10);
        ModelData data = OBJLoader.loadOBJModel("BunchOfRocks");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), rock, "rocks");
    }

    public TexturedModel grassModel(){
        ModelTexture grass = new ModelTexture(loader.loadTexture("grassTex"));
        grass.setTransparency(true);
        grass.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("Grass");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), grass, "grass");
    }

    public TexturedModel fernModel(){
        ModelTexture fern = new ModelTexture(loader.loadTexture("fern"));
        fern.setTransparency(true);
        fern.setFakeLight(true);
        ModelData data = OBJLoader.loadOBJModel("fern");
        return new TexturedModel(loader.loadtoVao(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices()), fern, "fern");
    }
}
