#version 120
varying vec4 v_color;       
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main(void)
{
	vec4 blur = texture2D(u_texture, v_texCoords);
	
	blur.xyz = blur.xyz * vec3(1.2);
	blur.a = blur.a * 0.5;
	
	gl_FragColor = v_color * blur;
}