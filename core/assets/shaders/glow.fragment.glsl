#version 120
varying vec4 v_color;       
varying vec2 v_texCoords;
uniform sampler2D u_texture; 

uniform float blurSize = 0.02;
uniform float intensity = 0.1;

void main()
{
   vec4 sum = vec4(0);

	// blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 4.0*blurSize)) * 0.05;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 3.0*blurSize)) * 0.09;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 2.0*blurSize)) * 0.12;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - blurSize)) * 0.15;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y)) * 0.16;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + blurSize)) * 0.15;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 2.0*blurSize)) * 0.12;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 3.0*blurSize)) * 0.09;
   sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 4.0*blurSize)) * 0.05;

   //increase blur with intensity!
   gl_FragColor = v_color * ((sum*intensity) + texture2D(u_texture, v_texCoords));
}