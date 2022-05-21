package com.realgotqkura.shader;

import com.realgotqkura.entities.Camera;
import com.realgotqkura.entities.Light;
import com.realgotqkura.utilities.LWColor;
import com.realgotqkura.utilities.MathHelper;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector3f;

public class StaticShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/com/realgotqkura/shader/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/com/realgotqkura/shader/fragmentShader.txt";
    public int transformationMatrix_loc;
    public int projectionMatrix_loc;
    public int viewMatrix_loc;
    public int lightPosition_loc;
    public int lightColor_loc;
    public int shinyDamper_loc;
    public int reflectivity_loc;
    public int fakeLight_loc;
    public int skyColor_loc;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniform() {
         transformationMatrix_loc = super.getUniformLocation("transformationMatrix");
         projectionMatrix_loc = super.getUniformLocation("projectionMatrix");
         viewMatrix_loc = super.getUniformLocation("viewMatrix");
         lightPosition_loc = super.getUniformLocation("lightPosition");
         lightColor_loc = super.getUniformLocation("lightColor");
         shinyDamper_loc = super.getUniformLocation("shinyDamper");
         reflectivity_loc = super.getUniformLocation("reflectivity");
         fakeLight_loc = super.getUniformLocation("useFakeLight");
         skyColor_loc = super.getUniformLocation("skyColor");
    }

    public void loadSkyColor(LWColor color){
        super.loadVector(skyColor_loc, new Vector3f(color.getRed(),color.getGreen(),color.getBlue()));
    }

    public void loadFakeLight(boolean value){
        super.loadBoolean(fakeLight_loc, value);
    }

    public void loadMatrix(Matrix4f matrix4f){
        super.loadMatrix(transformationMatrix_loc, matrix4f);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(projectionMatrix_loc, projection);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f view = MathHelper.createViewMatrix(camera);
        super.loadMatrix(viewMatrix_loc, view);
    }

    public void loadLight(Light light){
        super.loadVector(lightPosition_loc, light.getPosition());
        super.loadVector(lightColor_loc, light.getColor());
    }

    public void loadShineVars(float shinyDamper, float reflectivity){
        super.loadFloat(shinyDamper_loc, shinyDamper);
        super.loadFloat(reflectivity_loc, reflectivity);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normals");
    }
}
