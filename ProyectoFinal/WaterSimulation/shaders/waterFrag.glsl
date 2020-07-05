#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif


uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudv;

in vec4 clipSpace;
in vec2 textCoords;
in vec3 orientation;

const float waveStrength = 0.3;

uniform float offset;


void main(void) {

    vec2 ndc = (clipSpace.xy / clipSpace.w)/2.0 + 0.5;

    vec2 refractTexCoords = vec2(ndc.x, 1.0 - ndc.y);
    vec2 reflectTexCoords = vec2(ndc.x, 1.0 - ndc.y);


    vec2 distortion1 = (texture(dudv, vec2(textCoords.x + offset, textCoords.y)).rg * 2.0 - 1.0) * waveStrength;
    vec2 distortion2 = (texture(dudv, vec2(-textCoords.x, textCoords.y + offset)).rg * 2.0 - 1.0) * waveStrength;

    vec2 distortion = distortion1 + distortion2;


    reflectTexCoords += distortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, 0.001, 0.999);



    vec4 reflectColour = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColour = texture(refractionTexture, refractTexCoords);

    vec3 viewVector = normalize(orientation);

    float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));

    gl_FragColor = mix(mix(reflectColour, refractColour, refractiveFactor), vec4(0.0,0.3,1.0,1.0), 0.15);

    //gl_FragColor = vec4(0,0,1,1);
}