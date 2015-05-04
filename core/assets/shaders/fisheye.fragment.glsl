// Combination of a fish eye shader and a water shader, that fades out on the side, used for bosses!
#version 120
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec2 size1 = vec2(-99.0);
uniform vec2 pos1 = vec2(-99.0);

uniform vec2 size2 = vec2(-99.0);
uniform vec2 pos2 = vec2(-99.0);

uniform vec2 size3 = vec2(-99.0);
uniform vec2 pos3 = vec2(-99.0);

uniform float time = 0;
uniform int water = 1;

const float PI = 3.1415926535;

const float speed = 0.2;
const float speed_x = 0.3;
const float speed_y = 0.3;

const float emboss = 0.30;
const float intensity = 1.5;
const int steps = 8;
const float frequency = 6.0;
const int angle = 7;

const float delta = 60.;
const float intence = 700.;

const float reflectionCutOff = 0.012;
const float reflectionIntence = 200000.;


float col(vec2 coord)
{
   	float delta_theta = 2.0 * PI / float(angle);
    float col = 0.0;
    float theta = 0.0;
    
    for (int i = 0; i < steps; i++)
    {
      vec2 adjc = coord;
      theta = delta_theta*float(i);
      adjc.x += cos(theta)*time*speed + time * speed_x;
      adjc.y -= sin(theta)*time*speed - time * speed_y;
      col = col + cos( (adjc.x*cos(theta) - adjc.y*sin(theta))*frequency)*intensity;
    }

  	return cos(col);
}

vec4 render(vec4 resultS, vec2 size, vec2 pos, bool edge)
{
	vec2 uvS = v_texCoords;
	
	uvS.x = uvS.x * size.x;
	uvS.y = uvS.y * size.y;
	
  	float aperture = 180.0;
  	float apertureHalf = 0.5 * aperture * (PI / 180.0);
  	float maxFactor = sin(apertureHalf);
  
  	vec2 uv;

  	vec2 xy = 2.0 * (uvS.xy - pos.xy) - 1.0;
  	float d = length(xy);
  
  	float alphaRing = (1.8-maxFactor);
  	float innerRing = (2.0-maxFactor);
  	float outerRing = (2.2-maxFactor);

  	if(d < innerRing)
  	{
    	d = length(xy * maxFactor);
    
    	float z = sqrt(1.0 - d * d);
    	float r = atan(d, z) / PI;
    	float phi = atan(xy.y, xy.x);
    
    	uv.x = (r * cos(phi) + 0.5) + pos.x;
    	uv.y = (r * sin(phi) + 0.5) + pos.y;
  	}
  	else
  		uv = uvS.xy;
  		
  	uv.x = uv.x / size.x;
  	uv.y = uv.y / size.y;
  	
  	uvS.x = uvS.x / size.x;
  	uvS.y = uvS.y / size.y;
  
	float alpha = 0.0;
  
  	if((edge && d < outerRing) || (!edge && d < innerRing))
  	{
  		if(water == 1.0)
  		{
		vec2 res = vec2(100, 100);
  
  		vec2 p = uv.xy, c1 = p, c2 = p;
		float cc1 = col(c1);

		c2.x += res.x/delta;
		float dx = emboss*(cc1-col(c2))/delta;

		c2.x = p.x;
		c2.y += res.y/delta;
		float dy = emboss*(cc1-col(c2))/delta;

		c1.x += dx*2.0;
		c1.y = (c1.y+dy*2.0);
	
		uv = c1;
	
		if(d > alphaRing)
		{
			float diff = 0.0;
			
			diff = abs(outerRing - alphaRing);
			alpha = (d - alphaRing) * (1/diff);
		}
		}
  	}
  	else
  	{
    	uv = uvS.xy;
  	}
  	
  	if(uv == uvS)
  		return vec4(0.0);
  
  	vec4 result = texture2D(u_texture, uv);
  
  	if(alpha > 0.0)
  	{
  		vec4 resultMerge = vec4(0.0);
  	
  		resultMerge.r = (resultS.r * alpha) + (result.r * (1.0 - alpha));
  		resultMerge.g = (resultS.g * alpha) + (result.g * (1.0 - alpha));
  		resultMerge.b = (resultS.b * alpha) + (result.b * (1.0 - alpha));
  		resultMerge.a = result.a;
  		
  		result = resultMerge;
  	}
 
  	return result;
}

vec4 render(vec4 resultS, vec2 size, vec2 pos)
{
	return render(resultS, size, pos, true);
}

vec4 merge(vec4 a, vec4 b, float alpha)
{
	vec4 r = vec4(0.0);
	
  	if(!(a.r == b.r))
  		r.r = (a.r * alpha) + (b.r * (1.0 - alpha));
  	else
  		r.r = a.r;
  	
  	if(!(a.g == b.g))
  		r.g = (a.g * alpha) + (b.g * (1.0 - alpha));
  	else
  		r.g = a.g;
  		
  	if(!(a.b == b.b))
  		r.b = (a.b * alpha) + (b.b * (1.0 - alpha));
  	else
  		r.b = a.b;
  		
  	if(!(a.a == b.a))
  		r.a = (a.a * alpha) + (b.a * (1.0 - alpha));
  	else
  		r.a = a.a;
  		
  	return r;
}

vec4 merge(vec4 a, vec4 b)
{
	return merge(a, b, 0.5);
}

vec4 merge(vec4 a, vec4 b, vec4 c)
{
	float alpha = 1.0/3.0;
	
	vec4 r = vec4(0.0);
  	
  	if(!(a.r == b.r && b.r == c.r))
  		r.r = (a.r * alpha) + (b.r * alpha) + (c.r * alpha);
  	else
  		r.r = a.r;
  	
  	if(!(a.g == b.g && b.g == c.g))
  		r.g = (a.g * alpha) + (b.g * alpha) + (c.g * alpha);
  	else
  		r.g = a.g;
  		
  	if(!(a.b == b.b && b.b == c.b))
  		r.b = (a.b * alpha) + (b.b * alpha) + (c.b * alpha);
  	else
  		r.b = a.b;
  		
  	if(!(a.a == b.a && b.a == c.a))
  		r.a = (a.a * alpha) + (b.a * alpha) + (c.a * alpha);
  	else
  		r.a = a.a;
  		
  	return r;
}

void main()
{
	vec4 result;

	vec4 standard = texture2D(u_texture, v_texCoords);

	vec4 bubble1;
	bool bubble1exist = false;
	
	if(size1.x != -99.0)
	{
		bubble1 = render(standard, size1, pos1);
		
		if(bubble1.a != 0.0)
			bubble1exist = true;
	}
	
	vec4 bubble2;
	bool bubble2exist = false;
	
	if(size2.x != -99.0)
	{
		bubble2 = render(standard, size2, pos2);
		
		if(bubble2.a != 0.0)
			bubble2exist = true;
	}
	
	vec4 bubble3;
	bool bubble3exist = false;
	
	if(size3.x != -99.0)
	{
		bubble3 = render(standard, size3, pos3);
		
		if(bubble3.a != 0.0)
			bubble3exist = true;
	}
	
	int bubbles = 0;
	
	if(bubble1exist)
		bubbles++;
		
	if(bubble2exist)
		bubbles++;
		
	if(bubble3exist)
		bubbles++;
		
	if(bubbles == 1)
	{
		vec4 b;
		
		if(bubble1exist)
			b = bubble1;
		else if(bubble2exist)
			b = bubble2;
		else if(bubble3exist)
			b = bubble3;
			
		result = b;
	}
	else if(bubbles == 2)
	{
		vec4 b1;
		vec4 b1o;
		vec4 b2;
		vec4 b2o;
		
		if(bubble1exist)
		{
			b1 = render(standard, size1, pos1, false);
			b1o = bubble1;
		}
		else if(bubble2exist)
		{
			b1 = render(standard, size2, pos2, false);
			b1o = bubble2;
		}
		else if(bubble3exist)
		{
			b1 = render(standard, size3, pos3, false);
			b1o = bubble3;
		}
		
		if(bubble1exist && b1o != bubble1)
		{
			b2 = render(standard, size1, pos1, false);
			b2o = bubble1;
		}
		else if(bubble2exist && b1o != bubble2)
		{
			b2 = render(standard, size2, pos2, false);
			b2o = bubble2;
		}
		else if(bubble3exist)
		{
			b2 = render(standard, size3, pos3, false);
			b2o = bubble3;
		}
			
		if(b1.a == 0)
			result = b2o;
		else if(b2.a == 0)
			result = b1o;
		else
			result = merge(b1, b2);
	}
	else if(bubbles == 3)
	{
		vec4 b1 = render(standard, size1, pos1, false);
		vec4 b1o = bubble1;
		vec4 b2 = render(standard, size2, pos2, false);
		vec4 b2o = bubble2;
		vec4 b3 = render(standard, size3, pos3, false);
		vec4 b3o = bubble3;
			
		if(b1.a == 0 && b2.a == 0)
			result = b3o;
		else if(b1.a == 0 && b3.a == 0)
			result = b2o;
		else if(b2.a == 0 && b3.a == 0)
			result = b1o;
		else if(b1.a == 0)
			result = merge(b2, b3);
		else if(b2.a == 0)
			result = merge(b1, b3);
		else if(b3.a == 0)
			result = merge(b2, b1);
		else
			result = merge(b1, b2, b3);
	}
	
	if(result.a == 0.0)
		result = standard;
	else
		result = merge(result, standard, 0.85);
	
	gl_FragColor = result;
}