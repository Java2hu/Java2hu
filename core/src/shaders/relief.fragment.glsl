#version 120
varying vec4 v_color;       
varying vec2 v_texCoords;
uniform sampler2D u_texture; 

void main()
{
  vec2 uv = v_texCoords.xy;
  
  vec4 c = texture2D(u_texture, uv);
  
  c += texture2D(u_texture, uv+0.001);
  c += texture2D(u_texture, uv+0.003);
  c += texture2D(u_texture, uv+0.005);

  c += texture2D(u_texture, uv-0.001);
  c += texture2D(u_texture, uv-0.003);
  c += texture2D(u_texture, uv-0.005);

  c.rgb = vec3((c.r+c.g+c.b)/3.0);
  c = c / 9.5;
  gl_FragColor = v_color * c;
}