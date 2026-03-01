package universecore.ui.elements.markdown.elemdraw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.pooling.Pools;
import universecore.ui.elements.markdown.Markdown;

public class DrawHr extends Markdown.DrawObj {
  Color color;

  //use get
  DrawHr(){}

  public static DrawHr get(Markdown owner, Color color, float offY) {
    DrawHr res = Pools.obtain(DrawHr.class, DrawHr::new);
    res.parent = owner;
    res.color = color;
    res.offsetX = 0;
    res.offsetY = offY;

    return res;
  }

  @Override
  public void reset() {
    super.reset();
    color = null;
  }

  @Override
  protected void draw() {
    Draw.color(tmp1.set(color).mul(Draw.getColor()));
    Draw.rect(Core.atlas.white(), parent.x + parent.getWidth()/2, parent.y + parent.getHeight() + offsetY, parent.getWidth(), 4);
  }
}
