#version 330 core

in vec2 in_Position;
in mat3 in_Transform;
in vec4 in_Color;
in int in_TextureId;

uniform mat4 u_ProjectionMatrix;
uniform samplerBuffer u_TexturesData;

out vec2 v_TextureCoords;
out vec4 v_Color;

void main()
{
    vec3 pos = vec3(in_Position, 1);

    pos = in_Transform * pos;
    pos /= pos.z;
    gl_Position = u_ProjectionMatrix * vec4(pos.xy, 0, 1);

    vec4 textureData = texelFetch(u_TexturesData, in_TextureId);

    v_TextureCoords = in_Position * vec2(1, -1) + vec2(0, 1);
    v_TextureCoords = v_TextureCoords * 0.98 + 0.01;
    v_TextureCoords = v_TextureCoords * textureData.zw + textureData.xy;

    v_Color = in_Color;
}
