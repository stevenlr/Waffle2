#version 330 core

in vec2 in_Position;

uniform vec2 u_S0;
uniform vec2 u_S1;
uniform vec2 u_D0;
uniform vec2 u_D1;

out vec2 v_TextureCoords;

void main()
{
    v_TextureCoords = (in_Position * (u_S1 - u_S0) + u_S0);
    gl_Position = vec4((in_Position * (u_D1 - u_D0) + u_D0) * 2 - 1, 0, 1);
}