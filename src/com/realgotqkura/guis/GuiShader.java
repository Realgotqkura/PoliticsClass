package com.realgotqkura.guis;

import com.realgotqkura.shader.ShaderProgram;
import org.lwjglx.util.vector.Matrix4f;

public class GuiShader extends ShaderProgram{
        private static final String VERTEX_FILE = "src/com/realgotqkura/guis/guiVertexShader.txt";
        private static final String FRAGMENT_FILE = "src/com/realgotqkura/guis/guiFragmentShader.txt";

        private int location_transformationMatrix;

        public GuiShader() {
            super(VERTEX_FILE, FRAGMENT_FILE);
        }

        public void loadTransformation(Matrix4f matrix){
            super.loadMatrix(location_transformationMatrix, matrix);
        }

        @Override
        protected void getAllUniform() {
            location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        }

        @Override
        protected void bindAttributes() {
            super.bindAttribute(0, "position");
        }
}
