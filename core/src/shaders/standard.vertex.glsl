// Standard file for use in SpriteBatch.setShader(...);
#version 120
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
varying vec2 v_texCoords;
varying vec4 v_color;
uniform mat4 u_projTrans;

void main()
{
	v_color = a_color;
	v_texCoords = a_texCoord0;
	gl_Position = u_projTrans * a_position;
}