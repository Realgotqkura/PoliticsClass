package com.realgotqkura.models;

import com.realgotqkura.textures.ModelTexture;

public class TexturedModel {

    private final RawModel rawModel;
    private final ModelTexture texture;
    private String name;

    public TexturedModel(RawModel rawModel, ModelTexture modelTexture, String name){
        this.rawModel = rawModel;
        this.texture = modelTexture;
        this.name = name;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
