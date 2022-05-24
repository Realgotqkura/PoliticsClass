package com.realgotqkura.engine.testing;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.entities.Entity;
import com.realgotqkura.main.Main;
import com.realgotqkura.models.TexturedModel;
import com.realgotqkura.terrain.Terrain;
import com.realgotqkura.utilities.Location;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TerrainPresets {

    private final Loader loader;

    public TerrainPresets(Loader loader){
        this.loader = loader;
    }


    public List<Entity> randomPreset(String heightmap){
        Random r = new Random(672348);
        int index = 0;
        TexturedModels models = new TexturedModels(loader);
        List<Entity> entities = new ArrayList<>();
        TexturedModel treeModel = models.treeModel(false);
        TexturedModel rockModel = models.rockModel();
        TexturedModel grassModel = models.grassModel();
        TexturedModel fernModel = models.fernModel();

        for(Terrain terrain : Main.terrains){
            for (int i = 0; i < 100; i++) {
                int randomHeight = ThreadLocalRandom.current().nextInt(0, 2);
                float x = r.nextFloat() * Terrain.SIZE + terrain.getX();
                float z = r.nextFloat() * Terrain.SIZE + terrain.getZ();
                float y = terrain.getHeightAtLocation(x,z);
                entities.add(new Entity(treeModel, new Location(x,y, z
                ), 180,0,180,randomHeight + 1));
            }
            for(int i = 0; i < 50; i++){
                float x = r.nextFloat() * Terrain.SIZE + terrain.getX();
                float z = r.nextFloat() * Terrain.SIZE + terrain.getZ();
                float ry = ThreadLocalRandom.current().nextInt(0, 360 + 1);
                float y = terrain.getHeightAtLocation(x,z);
                entities.add( new Entity(rockModel, new Location(x,y,z), 180,ry,180,1));
            }
            for(int i = 0; i < 50; i++){
                float x = r.nextFloat() * Terrain.SIZE + terrain.getX();
                float z = r.nextFloat() * Terrain.SIZE + terrain.getZ();
                float y = terrain.getHeightAtLocation(x,z);
                entities.add( new Entity(rockModel, new Location(x,y,z), 180,0,180,1));
            }
            for(int i = 0; i < 7500; i++) {
                float x = r.nextFloat() * Terrain.SIZE + terrain.getX();
                float z = r.nextFloat() * Terrain.SIZE + terrain.getZ();
                float rx = ThreadLocalRandom.current().nextInt(160, 200 + 1);
                float ry = ThreadLocalRandom.current().nextInt(0,180 + 1);
                float rz = ThreadLocalRandom.current().nextInt(160, 200 + 1);
                float y = terrain.getHeightAtLocation(x,z);
                entities.add( new Entity(grassModel, new Location(x,y,z), rx,ry,rz,1.3F));
            }

            for(int i = 0; i < 75; i++){
                float x = r.nextFloat() * Terrain.SIZE + terrain.getX();
                float z = r.nextFloat() * Terrain.SIZE + terrain.getZ();
                float y = terrain.getHeightAtLocation(x,z);
                entities.add( new Entity(fernModel, new Location(x,y,z), 180,0,180,1));
            }
        }


        //Entity.entities.addAll(entities);
        return entities;
    }

    public List<Entity> circlePreset(Entity anchor){
        List<Integer> evenlist = new ArrayList<>();
        TexturedModels models = new TexturedModels(loader);
        List<Entity> entities = new ArrayList<>();
        TexturedModel treeModel = models.treeModel(false);
        TexturedModel fernModel = models.fernModel();

        entities.add(new Entity(models.treeModel(true), new Location(anchor.getPosition().getX(), anchor.getPosition().getY(),anchor.getPosition().getZ()),
                180,0,180,5));

        for(int i = 0; i < 360; i++){
            entities.add(new Entity(treeModel
                    , new Location(anchor.getPosition().getX() + (float) (250 * Math.sin(i)), anchor.getPosition().getY(),anchor.getPosition().getZ() + (float) (250 * Math.cos(i))),
                    180,0,180,1));
            if(i == 359){
                for(int i1 = -250; i1 < 250; i1++){
                    if(i1 % 10 == 0){
                        evenlist.add(i1);

                    }
                }
            }
        }

        for(Integer num : evenlist){
            for(Integer num2 : evenlist){
                entities.add(new Entity(fernModel,
                        new Location(num,anchor.getPosition().getY(),num2),180,0,180,1));
            }
        }

        //Entity.entities.addAll(entities);
        return entities;
    }
}
