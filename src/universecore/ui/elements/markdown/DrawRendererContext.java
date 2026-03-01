package universecore.ui.elements.markdown;

import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.style.Drawable;
import arc.scene.ui.Tooltip;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.ui.Fonts;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import universecore.ui.elements.markdown.elemdraw.DrawBoard;
import universecore.ui.elements.markdown.elemdraw.DrawClickable;
import universecore.ui.elements.markdown.elemdraw.DrawCode;
import universecore.ui.elements.markdown.elemdraw.DrawStr;

public abstract class DrawRendererContext {
  public final Markdown element;

  private final Seq<Markdown.DrawObj> drawObjs = new Seq<>();
  private final ObjectMap<Node, TextureRegion> imgCache = new ObjectMap<>();

  public boolean prefSizeCalculating;

  /**面板堆叠计数器，在绘制带有背景面板的对象时记录层序使用，应正确分配其序号*/
  public int boardLayers;
  /**列表堆叠计数器，在绘制带有缩进的列表时记录嵌套次数使用*/
  public int listLayer;

  /**当前绘制对象的左侧边距值（单位为空格字符的宽度），通常在嵌套块中使用，该值在换行时会用于计算渲染对象的左侧偏移量*/
  public float padding;
  /**当前绘制对象使用的比例缩放器（若可用）*/
  public float currScl;

  /**当前绘制对象的横向坐标偏移量标尺，通常由换行动作计算获得*/
  public float rendOff;
  /**当前绘制对象的纵向坐标偏移量标尺（自上到下），通常由换行动作计算获得*/
  public float lineOff;
  /**当前文档的总高度，通常由换行动作计算获得*/
  public float totalHeight;

  /**上一次绘制的文本信息映像*/
  public TextMirror lastText;
  /**当前文本绘制时所使用的字体，通常用于嵌套块定义内容字体*/
  public Font currFont;
  /**当前文本绘制时所使用的颜色，通常用于嵌套块定义内容颜色*/
  public Color currFontColor;

  protected DrawRendererContext(Markdown element) {
    this.element = element;
  }

  /**渲染一个Markdown语法节点
   *
   * @param node 待渲染节点*/
  public abstract void render(Node node);

  /**获取渲染结果的可迭代对象*/
  public Iterable<Markdown.DrawObj> renderResult() {
    return drawObjs;
  }

  /**添加一个绘制对象*/
  public void draw(Markdown.DrawObj obj){
    drawObjs.add(obj);
  }

  /**获取图像缓存，如果图像不存在则调用{@code o}生成*/
  public TextureRegion imageCache(Image image, Prov<TextureRegion> o) {
    return imgCache.get(image, o);
  }

  /**初始化渲染上下文*/
  public void init(){
    boardLayers = 0;
    listLayer = 0;
    padding = 0;
    rendOff = 0;
    lineOff = 0;
    currScl = 1;
    totalHeight = 0;
    lastText = null;
    currFont = element.getStyle().font;
    currFontColor = element.getStyle().textColor;
    drawObjs.clear();
  }

  /**清空图像缓存*/
  public void clearCache(){
    init();
    imgCache.clear();
  }

  /**更新文档显示区的高度（不会降低）
   *
   * @param h 显示位置的高度*/
  public void updateHeight(float h) {
    totalHeight = Math.max(totalHeight, h);
  }

  /**@see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
  public TextMirror makeStr(String str, Font font, Color color) {
    return makeStr(str, font, null, null, color);
  }

  /**@see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
  public TextMirror makeStr(String str, Font font, Color color, String openUrl) {
    return makeStr(str, font, openUrl, null, color);
  }

  /**@see DrawRendererContext#makeStr(String, Font, String, Drawable, Color) */
  public TextMirror makeStr(String str, Font font, Drawable background, Color color) {
    return makeStr(str, font, null, background, color);
  }

  /**添加一段文本显示器，并返回它的{@linkplain TextMirror 边界信息映射对象}
   *
   * @param str 显示文本内容
   * @param font 文本字体
   * @param openUrl 文本超链接，为null则不定义超链接
   * @param background 文本背景，为null则透明
   * @param color 文本颜色*/
  public TextMirror makeStr(String str, Font font, String openUrl, Drawable background, Color color) {
    float maxWidth = !element.contentWrap || element.getWidth() <= font.getSpaceXadvance()*3? Float.MAX_VALUE: element.getWidth() - rendOff;
    float tmp = 0;
    int index = 0;

    float width = 0;
    updateHeight(font.getLineHeight()*currScl);
    for (int c : str.chars().toArray()) {
      Font.Glyph glyph = font.getData().getGlyph((char) c);
      if (glyph == null){
        index++;
        continue;
      }

      tmp += glyph.xadvance*currScl*font.getScaleX();
      if (tmp > maxWidth){
        break;
      }
      width = tmp;
      index++;
    }

    TextMirror res = lastText;
    if (width > 0){
      rendOff += background != null? background.getLeftWidth(): 0;
      if (res == null) res = new TextMirror(str.substring(0, index), font, color, rendOff, -lineOff, width, totalHeight);
      else res.sub = new TextMirror(str.substring(0, index), font, color, rendOff, -lineOff, width, totalHeight);

      draw(openUrl != null?
          DrawClickable.get(element, str.substring(0, index), font,
              () -> Core.app.openURI(openUrl), new Tooltip(t -> t.table(element.getStyle().board).get().add(openUrl)),
              color, rendOff, -lineOff, currScl
          ):
          DrawStr.get(element, str.substring(0, index), font, color, rendOff, -lineOff, currScl, background));
      rendOff += width + (background != null? background.getRightWidth(): 0) + font.getSpaceXadvance()*font.getScaleX();
    }
    else if (res == null) res = new TextMirror("", font, color, rendOff, -lineOff, 0, totalHeight);
    else res.sub = new TextMirror("", font, color, rendOff, -lineOff, 0, totalHeight);

    lastText = res;

    if (index < str.length()){
      if (index == 0) index = 1; //TODO 确保至少处理一个字符(处理安卓循环报错) 我不知道这是否正确 但确实处理了
      row();
      res.sub = makeStr(str.substring(index), font, openUrl, background, color);
    }

    return res;
  }

  /**添加一个代码块显示区域
   *
   * @param lang 代码采用的语言标签
   * @param code 代码内容*/
  public void makeCodeBox(String lang, String code) {
    Markdown.MarkdownStyle style = element.getStyle();

    padding += 4;
    lineOff += style.linesPadding;
    row();
    float begin = lineOff;
    lineOff += style.linesPadding*2;
    DrawCode pane = DrawCode.get(element, lang, code, style.codeFont, rendOff, -lineOff, style.codeBlockStyle);
    updateHeight(pane.height() + style.linesPadding);
    draw(pane);
    padding -= 4;
    row();
    lineOff += style.linesPadding*2;
    draw(DrawBoard.get(element, style.codeBlockBack, boardLayers, lineOff - begin, rendOff, -begin));

    DrawClickable c = DrawClickable.get(element, Core.bundle.get("editor.copy"), Fonts.outline,
        () -> Core.app.setClipboardText(code),
        null, style.subTextColor, pane.width() - 64, -begin - style.linesPadding*2 - 8, 1
    );
    draw(c);
    Element e = c.getElem();
    e.color.a = 0.4f;
    e.hovered(() -> e.actions(Actions.alpha(1, 0.5f)));
    e.exited(() -> e.actions(Actions.alpha(0.4f, 0.5f)));
  }

  /**执行换行，该操作将更新文档区域的高度并重置行偏移标尺*/
  public void row() {
    Markdown.MarkdownStyle style = element.getStyle();
    element.prefWidth = Math.max(element.prefWidth, rendOff);

    rendOff = padding*style.font.getSpaceXadvance();
    lineOff += totalHeight + style.linesPadding*currScl;

    element.prefHeight = lineOff;
    totalHeight = 0;
  }
}