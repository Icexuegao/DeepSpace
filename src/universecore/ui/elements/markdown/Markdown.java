package universecore.ui.elements.markdown;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import org.commonmark.Extension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import universecore.ui.elements.markdown.extensions.CurtainExtension;
import universecore.ui.elements.markdown.extensions.InsExtension;
import universecore.ui.elements.markdown.extensions.StrikethroughExtension;
import universecore.ui.elements.markdown.extensions.TablesExtension;
import universecore.ui.elements.markdown.highlighter.Highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**Markdown文档渲染元素，*/
public class Markdown extends WidgetGroup {
  public static final String[] IMAGE_BASE_64_List = {
      "data:image/png;base64,",
      "data:image/jpg;base64,",
      "data:image/jpeg;base64,"
  };

  private static final List<Extension> defaultExtensions = new ArrayList<>(Arrays.asList(
      TablesExtension.create(),
      InsExtension.create(),
      StrikethroughExtension.create(),
      CurtainExtension.create()
  ));

  private final Seq<DrawObj> drawObjs = new Seq<>();

  private Node node;
  private MarkdownStyle style;

  float prefWidth, prefHeight;
  boolean prefInvalid = true;
  boolean contentWrap = true;
  float lastPrefHeight;

  protected final List<Extension> extensions;
  protected final Parser parser;
  protected final MDLayoutRenderer renderer;
  protected final DrawRendererContext rendererContext;

  public static void defaultExtensions(Extension... extensions){
    checkExtensions(Arrays.asList(extensions));

    Markdown.defaultExtensions.addAll(Arrays.asList(extensions));
  }

  @Deprecated
  public Markdown(String md, Font mono){
    this(md, MarkdownStyles.defaultMD(mono));
  }

  public Markdown(String md, MarkdownStyle style){
    this(Collections.emptyList(), md, style);
  }

  public Markdown(List<Extension> extensions, String md, MarkdownStyle style){
    checkExtensions(extensions);

    this.extensions = new ArrayList<>(extensions);
    parser = Parser.builder().extensions(extensions).extensions(defaultExtensions).build();
    renderer = MDLayoutRenderer.builder().extensions(extensions).extensions(defaultExtensions).build();
    rendererContext = renderer.createContext(this);

    node = parser.parse(md);
    touchable = Touchable.childrenOnly;

    this.style = style;
  }

  /**internal usage
   *
   * @hidden */
  public Markdown(Markdown parent, Node node){
    this.extensions = new ArrayList<>(parent.extensions);
    parser = null;
    renderer = MDLayoutRenderer.builder().extensions(parent.extensions).extensions(defaultExtensions).build();
    rendererContext = renderer.createContext(this);

    this.node = node;
    touchable = Touchable.childrenOnly;

    this.style = parent.getStyle();
  }

  public void setDocument(String string){
    node = parser.parse(string);
    invalidate();
  }

  public void setStyle(MarkdownStyle style){
    this.style = style;
    invalidate();
  }

  public MarkdownStyle getStyle(){
    return style;
  }

  public void setContentWrap(boolean wrap){
    contentWrap = wrap;
  }

  private void calculatePrefSize(boolean layoutStep){
    rendererContext.prefSizeCalculating = !layoutStep;

    prefHeight = prefWidth = 0;

    for (DrawObj obj : drawObjs) {
      obj.free();
    }
    drawObjs.clear();

    renderer.renderLayout(node);

    prefWidth = Math.max(prefWidth, rendererContext.rendOff);
    prefHeight += rendererContext.totalHeight;

    prefInvalid = false;

    if(prefHeight != lastPrefHeight){
      lastPrefHeight = prefHeight;
      invalidateHierarchy();
    }

    rendererContext.prefSizeCalculating = false;
  }

  @Override
  public void layout() {
    calculatePrefSize(true);

    drawObjs.addAll(rendererContext.renderResult());
    drawObjs.sort((a, b) -> a.priority() - b.priority());

    clearChildren();
    for (DrawObj obj : drawObjs) {
      if (obj instanceof ActivityDrawer activity){
        Element element = activity.getElem();
        addChild(element);
        element.setBounds(
            obj.offsetX,
            height + obj.offsetY - activity.height(),
            activity.width(),
            activity.height()
        );
        element.validate();
      }
    }
  }

  @Override
  public float getPrefWidth() {
    if (contentWrap) return 0f;

    if (prefInvalid) calculatePrefSize(false);
    return prefWidth;
  }

  @Override
  public float getPrefHeight() {
    if (prefInvalid) calculatePrefSize(false);
    return prefHeight;
  }

  @Override
  protected void drawChildren() {
    for (DrawObj obj : drawObjs) {
      if (obj instanceof ActivityDrawer act && cullingArea != null
          && !cullingArea.overlaps(obj.offsetX, height + obj.offsetY, act.width(), act.height())) continue;

      Draw.reset();
      Draw.alpha(parentAlpha);
      obj.draw();
    }
    super.drawChildren();
  }

  private static void checkExtensions(List<Extension> extensions) {
    for (Extension extension : extensions) {
      if (!(extension instanceof MDLayoutRenderer.DrawRendererExtension)
          || !(extension instanceof Parser.ParserExtension))
        throw new IllegalArgumentException("extension must be a DrawRendererExtension and a ParserExtension");
    }
  }

  public static class MarkdownStyle {
    public Font font, codeFont, emFont, strongFont, subFont;
    public Color textColor, emColor, subTextColor, lineColor, linkColor;
    public float linesPadding, tablePadHor, tablePadVert, paragraphPadding;
    public Drawable board, codeBack, codeBlockBack, tableBack1, tableBack2, curtain;
    public ScrollPane.ScrollPaneStyle codeBlockStyle;
    public Drawable[] listMarks;
    public Highlighter highlighter;
    public ColorProvider codeColorProvider;
  }

  public abstract static class DrawObj implements Pool.Poolable {
    protected static final Color tmp1 = new Color(), tmp2 = new Color();

    protected Markdown parent;
    protected float offsetX, offsetY;

    protected abstract void draw();

    public int priority() {
      return 0;
    }

    @Override
    public void reset() {
      offsetX = offsetY = 0;
    }

    void free() {
      Pools.free(this);
    }
  }

  public interface ActivityDrawer {
    Element getElem();

    float width();

    float height();
  }
}
