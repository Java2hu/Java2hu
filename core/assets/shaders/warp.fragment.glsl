#version 120
varying vec4 v_color;       
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float time;

void main()
{
	vec2 uv = v_texCoords;
	
	float yMul = (uv.y / 1.0);
	float tMul = mod(time * 5.0, 5.0);
	
	float size = 80.0;
	
	yMul = float(int(yMul * size)) / size;
	
	float tSize = 30.0;
	
	tMul = float(int(tMul * tSize)) / tSize;
	
	if(tMul > 2.5)
		tMul = 5.0 - tMul;
		
	if(yMul > 1.0)
		yMul = 2.0 - yMul;
	
	float mul = 2.0 * (yMul * tMul);
	
	uv.x = uv.x + ((0.08 * tMul * sin(2.0 * mul * 3.14)));
	
	gl_FragColor = texture2D(u_texture, uv);
}