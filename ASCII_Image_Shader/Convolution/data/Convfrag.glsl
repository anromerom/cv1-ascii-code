#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 texOffset;
uniform mat3 convMat;
uniform int weight;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {

  vec2 tc0 = vertTexCoord.st + vec2(-texOffset.s, -texOffset.t);
  vec2 tc1 = vertTexCoord.st + vec2(         0.0, -texOffset.t);
  vec2 tc2 = vertTexCoord.st + vec2(+texOffset.s, -texOffset.t);
  vec2 tc3 = vertTexCoord.st + vec2(-texOffset.s,          0.0);
  vec2 tc4 = vertTexCoord.st + vec2(         0.0,          0.0);
  vec2 tc5 = vertTexCoord.st + vec2(+texOffset.s,          0.0);
  vec2 tc6 = vertTexCoord.st + vec2(-texOffset.s, +texOffset.t);
  vec2 tc7 = vertTexCoord.st + vec2(         0.0, +texOffset.t);
  vec2 tc8 = vertTexCoord.st + vec2(+texOffset.s, +texOffset.t);

  vec4 col0 = texture2D(texture, tc0);
  vec4 col1 = texture2D(texture, tc1);
  vec4 col2 = texture2D(texture, tc2);
  vec4 col3 = texture2D(texture, tc3);
  vec4 col4 = texture2D(texture, tc4);
  vec4 col5 = texture2D(texture, tc5);
  vec4 col6 = texture2D(texture, tc6);
  vec4 col7 = texture2D(texture, tc7);
  vec4 col8 = texture2D(texture, tc8);

  float co0 = convMat[0][0];
  float co1 = convMat[1][0];
  float co2 = convMat[2][0];
  float co3 = convMat[0][1];
  float co4 = convMat[1][1];
  float co5 = convMat[2][1];
  float co6 = convMat[0][2];
  float co7 = convMat[1][2];
  float co8 = convMat[2][2];
  

/*
  float co0 = -1;
  float co1 = -1;
  float co2 = -1;
  float co3 = -1;
  float co4 = 8;
  float co5 = -1;
  float co6 = -1;
  float co7 = -1;
  float co8 = -1;
*/

  vec4 sum = (co0*col0 + co1*col1 + co1*col2 + 
              co3*col3 + co4*col4 + co5*col5 +
              co6*col6 + co7*col7 + co8*col8)/weight;

  gl_FragColor = vec4(sum.rgb, 1.0) * vertColor;

 
}