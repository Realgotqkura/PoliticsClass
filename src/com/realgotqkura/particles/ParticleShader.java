package com.realgotqkura.particles;


import com.realgotqkura.shader.ShaderProgram;
import org.lwjglx.util.vector.Matrix4f;
import org.lwjglx.util.vector.Vector2f;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/realgotqkura/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "src/com/realgotqkura/particles/particleFShader.txt";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int location_texOffset1;
	private int location_texOffset2;
	private int location_CoordsInfo;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniform() {
		location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_texOffset1 = super.getUniformLocation("texOffset1");
		location_texOffset2 = super.getUniformLocation("texOffset2");
		location_CoordsInfo = super.getUniformLocation("texCoordsInfo");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	protected void loadModelViewMatrix(Matrix4f modelView) {
		super.loadMatrix(location_modelViewMatrix, modelView);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}


	protected void loadTextureCoordsInfo(Vector2f offset1, Vector2f offset2, float rows, float blend){
		super.load2DVector(location_texOffset1, offset1);
		super.load2DVector(location_texOffset2, offset2);
		super.load2DVector(location_CoordsInfo, new Vector2f(rows,blend));
	}

}
