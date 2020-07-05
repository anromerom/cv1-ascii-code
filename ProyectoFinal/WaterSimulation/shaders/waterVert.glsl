
uniform mat4 transform;
uniform mat4 modelview;
uniform mat4 model;

attribute vec4 position;
attribute vec4 color;

uniform vec4 plane;

varying vec4 vertColor;

out vec4 clipSpace;
out vec2 textCoords;
out vec3 orientation;

uniform vec3 eyePosition;

const float tiling = 6.0;


void main(void) {

	vec4 worldPosition = model * position;
	clipSpace = transform * position;
	gl_Position = clipSpace;
	textCoords = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5) * tiling;


	orientation = eyePosition - worldPosition.xyz;

}