package com.realgotqkura.particles;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.entities.Camera;
import com.realgotqkura.utilities.InsertionSort;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Matrix4f;

import java.util.*;

public class ParticleMaster {

    public static Map<ParticleTexture ,List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;


    public static void init(Loader loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);

    }

    public static void update(Camera camera){
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while(mapIterator.hasNext()){
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while(iterator.hasNext()){
                Particle p = iterator.next();
                boolean alive = p.update(camera);
                if(!alive){
                    iterator.remove();
                    if(list.isEmpty()){
                        mapIterator.remove();
                    }
                }
            }
            InsertionSort.sortHighToLow(list);
        }

    }

    public static void render(Camera camera){
            renderer.render(particles, camera);
    }

    public static void cleanUp(){
        particles.clear();
    }

    public static void addParticle(Particle particle){
        List<Particle> list = particles.computeIfAbsent(particle.getTexture(), k -> new ArrayList<>());
        list.add(particle);
    }
}
