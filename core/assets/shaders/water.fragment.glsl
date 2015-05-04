// Water shader used for the boss aura
#version 120
varying vec4 v_color;       
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float time = 0.5f;

void main()
{
	vec2 newCoords = v_texCoords;
	
	float oldY = newCoords.y;
	
	newCoords.y = newCoords.y + (sin(newCoords.x * time) * 0.02);
	newCoords.x = newCoords.x + (cos(oldY * time) * 0.02);
	
	vec4 sum = vec4(0);
	float blurSize = 0.01f * v_color.a;
	
	// Small Blur
	if(blurSize > 0)
	{
		sum += texture2D(u_texture, vec2(newCoords.x - blurSize, newCoords.y));
		sum += texture2D(u_texture, vec2(newCoords.x + blurSize, newCoords.y));
	}

	gl_FragColor = sum + texture2D(u_texture, newCoords);
	
	gl_FragColor = (gl_FragColor * v_color);
}