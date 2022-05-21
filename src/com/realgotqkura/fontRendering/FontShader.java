package com.realgotqkura.fontRendering;


import com.realgotqkura.shader.ShaderProgram;
import org.lwjglx.util.vector.Vector2f;
import org.lwjglx.util.vector.Vector3f;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/com/realgotqkura/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/com/realgotqkura/fontRendering/fontFragment.txt";

	private int translation_loc;
	private int color_loc;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}


	@Override
	protected void getAllUniform() {
		translation_loc = super.getUniformLocation("translation");
		color_loc = super.getUniformLocation("color");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColor(Vector3f color){
		super.loadVector(color_loc, color);
	}

	protected void loadTranslation(Vector2f translation){
		super.load2DVector(translation_loc,translation);
	}


}
