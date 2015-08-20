#version 330 core

in vec2 v_TextureCoords;

uniform sampler2D u_TextureCanvas;
uniform vec4 u_Color;

out vec4 out_Color;

void main()
{
    out_Color = vec4(texture(u_TextureCanvas, v_TextureCoords).rgb, 1) * u_Color;
}