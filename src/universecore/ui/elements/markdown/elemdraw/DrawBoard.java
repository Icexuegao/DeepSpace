package universecore.ui.elements.markdown.elemdraw;

import arc.scene.style.Drawable;
import arc.util.pooling.Pools;
import universecore.ui.elements.markdown.Markdown;

public class DrawBoard extends Markdown.DrawObj {
  float height;
  Drawable drawable;
  int lay;

  //use get
  DrawBoard(){}

  public static DrawBoard get(Markdown owner, Drawable drawable, int layer, float height, float offX, float offY) {
    DrawBoard res = Pools.obtain(DrawBoard.class, DrawBoard::new);
    res.parent = owner;
    res.drawable = drawable;
    res.height = height;
    res.offsetX = offX;
    res.offsetY = offY;
    res.lay = layer;

    return res;
  }

  @Override
  public int priority() {
    return lay == 0? 0: -(1000 - lay);
  }

  @Override
  public void reset() {
    super.reset();
    parent = null;
    drawable = null;
    height = 0;
    lay = 0;
  }

  @Override
  protected void draw() {
    drawable.draw(parent.x + offsetX, parent.y + parent.getHeight() + offsetY - height, parent.getWidth() - offsetX, height);
  }
}
