varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D tex;

uniform float width;
uniform float height;
uniform float pixelation;

void main()
{
 float dx = pixelation/width;
 float dy = pixelation/height;
 vec2 coord = vec2((dx*floor(v_texCoords.x/dx)),
                   1-(dy*floor(v_texCoords.y/dy)));
 gl_FragColor = texture2D(tex, coord) * v_color;
}