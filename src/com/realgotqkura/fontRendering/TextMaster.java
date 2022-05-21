package com.realgotqkura.fontRendering;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.fontMeshCreator.FontType;
import com.realgotqkura.fontMeshCreator.GUIText;
import com.realgotqkura.fontMeshCreator.TextMeshCreator;
import com.realgotqkura.fontMeshCreator.TextMeshData;
import org.lwjgl.system.CallbackI;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {

    private static Loader loader;
    public static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(Loader loader1){
        loader = loader1;
        renderer = new FontRenderer();
    }

    public static void loadText(GUIText text){
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.FontloadtoVao(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public static void render(){
        renderer.render(texts);
    }

    public static void removeText(GUIText text){
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if(textBatch == null){
            texts.remove(text.getFont());
        }
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }

}
