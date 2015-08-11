#version 330

in vec2 in_Position;
in mat3 in_Transform;
in vec4 in_Color;

uniform mat4 u_ProjectionMatrix;

out vec4 v_Color;

void main()
{
    vec3 pos = vec3(in_Position, 1);

    pos = in_Transform * pos;
    pos /= pos.z;
    gl_Position = u_ProjectionMatrix * vec4(pos.xy, 0, 1);

    v_Color = in_Color;
}