package universecore.ui.elements.markdown.elemdraw;

import arc.graphics.g2d.Font;
import arc.scene.Element;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.util.pooling.Pools;
import universecore.ui.elements.markdown.ColorProvider;
import universecore.ui.elements.markdown.Markdown;
import universecore.ui.elements.markdown.highlighter.TokensContext;

public class DrawCode extends Markdown.DrawObj implements Markdown.ActivityDrawer {
  ScrollPane pane;
  Label label;

  //use get
  DrawCode(){}

  public static DrawCode get(Markdown owner, String language, String code, Font textFont, float ox, float oy, ScrollPane.ScrollPaneStyle paneStyle) {
    DrawCode res = Pools.obtain(DrawCode.class, DrawCode::new);
    res.parent = owner;
    res.offsetX = ox;
    res.offsetY = oy;

    Font.FontData data = textFont.getData();
    float lastScl = data.scaleX;
    data.setScale(1f);

    StringBuilder str = new StringBuilder();
    if (language != null && owner.getStyle().highlighter != null && owner.getStyle().codeColorProvider != null) {
      TokensContext context = owner.getStyle().highlighter.analyze(language, code);
      ColorProvider provider = owner.getStyle().codeColorProvider;

      context.applyScopes((t, s) -> {
        str.append("[#")
            .append(provider.getColor(language, s).toString(), 0, 6)
            .append("]")
            .append(t.text);
      });
    }
    else str.append(code);
    data.setScale(lastScl);

    textFont.getData().markupEnabled = true;
    res.label = new Label(str, new Label.LabelStyle() {{ font = textFont; }});
    res.pane = new ScrollPane(res.label, paneStyle);
    res.label.validate();

    return res;
  }

  @Override
  protected void draw() {}

  @Override
  public void reset() {
    super.reset();
    label = null;
    pane = null;
  }

  @Override
  public Element getElem() {
    return pane;
  }

  @Override
  public float width() {
    return parent.getWidth() - offsetX - 12;
  }

  @Override
  public float height() {
    return label.getHeight();
  }
}
