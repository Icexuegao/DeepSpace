package universecore.ui.elements.markdown.elemdraw;

import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.scene.Element;
import arc.scene.ui.TextButton;
import arc.scene.ui.Tooltip;
import arc.util.pooling.Pools;
import mindustry.ui.Styles;
import universecore.ui.elements.markdown.Markdown;

public class DrawClickable extends DrawStr implements Markdown.ActivityDrawer {
  TextButton openUrl;

  //use get
  private DrawClickable(){
    super();
  }

  public static DrawClickable get(Markdown owner, String str, Font strFont, Runnable clicked, Tooltip tooltip, Color color, float ox, float oy, float scl) {
    DrawClickable res = Pools.obtain(DrawClickable.class, DrawClickable::new);
    res.parent = owner;
    res.text = str;
    res.openUrl = new TextButton(str, new TextButton.TextButtonStyle(Styles.nonet) {{
      fontColor = color;
      font = strFont;
    }}) {{
      clicked(clicked);
      label.setScale(scl);
      label.setWrap(false);

      if (tooltip != null) addListener(tooltip);
    }};
    res.offsetX = ox;
    res.offsetY = oy;
    res.scl = scl;
    res.color = color;

    return res;
  }

  @Override
  protected void draw() {
  }

  @Override
  public Element getElem() {
    return openUrl;
  }

  @Override
  public float width() {
    return openUrl.getLabel().getPrefWidth();
  }

  @Override
  public float height() {
    return openUrl.getLabel().getPrefHeight();
  }

  @Override
  public void reset() {
    super.reset();
    openUrl = null;
  }
}
