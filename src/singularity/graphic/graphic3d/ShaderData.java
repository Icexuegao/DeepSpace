package singularity.graphic.graphic3d;

import arc.graphics.GLTexture;
import arc.math.Mat;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;

public abstract class ShaderData<T> {
  public final String name;

  public ShaderData(String name) {
    this.name = name;
  }

  public abstract void apply(ShaderProgram program, T data);

  public static <T extends GLTexture> ShaderData<T> uniformTexture(String uniform, int unit){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, GLTexture data) {
        data.bind(unit);
        program.baseShader.setUniformi(uniform, unit);
      }
    };
  }

  public static ShaderData<Float> uniformFloat(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Float data) {
        program.baseShader.setUniformf(uniform, data);
      }
    };
  }

  public static ShaderData<Integer> uniformInt(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Integer data) {
        program.baseShader.setUniformi(uniform, data);
      }
    };
  }

  public static ShaderData<Vec2> uniformVec(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Vec2 data) {
        program.baseShader.setUniformf(uniform, data);
      }
    };
  }

  public static ShaderData<Vec3> uniformVec3(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Vec3 data) {
        program.baseShader.setUniformf(uniform, data);
      }
    };
  }

  public static ShaderData<Mat> uniformMat(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Mat data) {
        program.baseShader.setUniformMatrix(uniform, data);
      }
    };
  }

  public static ShaderData<Mat3D> uniformMat3D(String uniform){
    return new ShaderData<>(uniform) {
      @Override
      public void apply(ShaderProgram program, Mat3D data) {
        program.baseShader.setUniformMatrix4(uniform, data.val);
      }
    };
  }
}
