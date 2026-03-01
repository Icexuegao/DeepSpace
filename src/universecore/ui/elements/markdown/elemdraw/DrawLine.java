package universecore.ui.elements.markdown.elemdraw;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.util.pooling.Pools;
import universecore.ui.elements.markdown.Markdown;

public class DrawLine extends Markdown.DrawObj {
  float width;
  Color color;

  //use get
  DrawLine(){}

  public static DrawLine get(Markdown owner, Color color, float offX, float offY, float width) {
    DrawLine res = Pools.obtain(DrawLine.class, DrawLine::new);
    res.parent = owner;
    res.color = color;
    res.offsetX = offX;
    res.offsetY = offY;
    res.width = width;

    return res;
  }

  @Override
  public int priority() {
    return 1;
  }

  @Override
  public void reset() {
    super.reset();
    parent = null;
    color = null;
    width = 0;
  }

  @Override
  protected void draw() {
    Lines.stroke(2, color);
    Lines.line(parent.x + offsetX, parent.y + parent.getHeight() + offsetY,
        parent.x + offsetX + width, parent.y + parent.getHeight() + offsetY);
  }
}
