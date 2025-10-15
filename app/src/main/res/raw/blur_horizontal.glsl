#version 320 es
precision highp float;

in vec2 vTexCoord;
uniform sampler2D uTexture;
uniform vec2 uTexelSize;
uniform float uBlurSize;
uniform int uRadius;
out vec4 fragColor;

void main() {
    vec2 coord = vTexCoord;
    vec4 sum = vec4(0.0);
    float totalWeight = 0.0;

    for(int i = -uRadius; i <= uRadius; i++){
        float w = exp(-float(i*i) / (2.0 * float(uRadius*uRadius) / 4.0));
        vec2 offset = vec2(float(i) * uTexelSize.x * uBlurSize, 0.0);
        sum += texture(uTexture, coord + offset) * w;
        totalWeight += w;
    }

    fragColor = sum / totalWeight;
}
