package com.realgotqkura.particles;

import com.realgotqkura.engine.Loader;
import com.realgotqkura.entities.Camera;
import org.lwjgl.system.CallbackI;
import org.lwjglx.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleMaster {

    public static List<Particle> particles = new ArrayList<>();
    private static ParticleRenderer renderer;


    public static void init(Loader loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);

    }

    public static void update(){
        Iterator<Particle> iterator = particles.iterator();
        while(iterator.hasNext()){
            Particle p = iterator.next();
            boolean alive = p.update();
            if(!alive){
              iterator.remove();
            }
        }
    }

    public static void render(Camera camera){
        renderer.render(particles, camera);
    }

    public static void cleanUp(){
        particles.clear();
    }

    public static void addParticle(Particle particle){
        particles.add(particle);
    }
}
