package singularity.graphic.graphic3d;

import arc.struct.ObjectMap;

public class Material {
  protected final ObjectMap<ShaderData<?>, Object> shaderData = new ObjectMap<>();

  public final ShaderProgram shader;

  public Material(ShaderProgram shader) {
    this.shader = shader;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void setupData() {
    shader.baseShader.bind();
    for (ShaderData model : shader.shaderData) {
      model.apply(shader, shaderData.get(model));
    }
  }

  public <T> void setData(ShaderData<T> data, T value) {
    if (!shader.shaderData.contains(data)) throw new IllegalArgumentException("Shader data " + data.name + " does not exist");
    shaderData.put(data, value);
  }
}
