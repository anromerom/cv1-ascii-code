#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  vec3 gr = vec3(0.3);
  vec4 col = texture2D(texture, vertTexCoord.st);
  vec4 color = vec4(vec3(dot(col.xyz,gr)),1.0);
  gl_FragColor = color;
}