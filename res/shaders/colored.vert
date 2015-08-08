#version 330

in vec2 in_Position;
in mat3 in_Transform;
in vec4 in_Color;

uniform mat4 u_Projection;

out vec2 v_Position;

void main()
{
    v_Position = in_Position;
    gl_Position = vec4(in_Position, 0, 1);
}