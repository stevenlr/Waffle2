#version 330 core

in vec4 v_Color;
in vec2 v_TextureCoords;

uniform sampler2D u_TextureAtlas;

out vec4 out_Color;

void main()
{
    out_Color = texture(u_TextureAtlas, v_TextureCoords) * v_Color;
}