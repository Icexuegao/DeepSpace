package singularity.graphic.renders;

import arc.graphics.Camera;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mat;

import static singularity.graphic.SglShaders.*;

public class Aftershadow {
  private final FrameBuffer buffer, pingpong;
  private final Camera camera;
  private final TextureRegion region = new TextureRegion();
  private final Mat last = new Mat();

  private final Color baseColor = new Color(1, 1, 1, 0);

  public boolean linear = true;
  public float coef = -0.01f;

  public Aftershadow(float width, float height, float bufferScl) {
    buffer = new FrameBuffer((int)(width*bufferScl), (int)(height*bufferScl));
    pingpong = new FrameBuffer((int)(width*bufferScl), (int)(height*bufferScl));
    camera = new Camera();
    camera.width = width;
    camera.height = height;
  }

  public void setBaseColor(float r, float g, float b){
    baseColor.set(r, g, b, 0);
  }

  public void setBaseColor(Color color){
    baseColor.set(color.r, color.g, color.b, 0);
  }

  public void update(){
    pingpong.begin(baseColor);
    buffer.blit(simpleScreen);
    pingpong.end();

    buffer.begin(baseColor);
    AlphaAdjust shader = linear? linearAlpha: lerpAlpha;
    shader.coef = coef;
    pingpong.blit(shader);
    buffer.end();
  }

  public void clear(){
    buffer.begin(Color.clear);
    buffer.end();
  }

  public void draw(float drawX, float drawY, Runnable draw){
    Draw.draw(Draw.z(), () -> {
      buffer.begin();
      camera.position.set(drawX, drawY);
      camera.update();
      last.set(Draw.proj());
      Draw.proj(camera);
      draw.run();
      Draw.proj(last);
      buffer.end();

      region.set(buffer.getTexture());
      Draw.rect(region, drawX, drawY, camera.width, camera.height);
    });
  }
}
