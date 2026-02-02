package universecore.ui.elements.markdown;

import arc.Core;
import arc.func.Cons;
import arc.func.ConsT;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.util.Http;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import universecore.UncCore;
import universecore.ui.elements.markdown.elemdraw.DrawBoard;
import universecore.ui.elements.markdown.elemdraw.DrawHr;
import universecore.ui.elements.markdown.elemdraw.DrawImg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static universecore.ui.elements.markdown.Markdown.IMAGE_BASE_64_List;

public class BaseDrawRenderer extends AbstractVisitor implements NodeRenderer {
  public static final HashSet<Class<? extends Node>> TypeSet = new HashSet<>(Arrays.asList(Document.class, Heading.class, Paragraph.class, BlockQuote.class, BulletList.class, FencedCodeBlock.class, HtmlBlock.class, ThematicBreak.class, IndentedCodeBlock.class, Link.class, ListItem.class, OrderedList.class, Image.class, Emphasis.class, StrongEmphasis.class, Text.class, Code.class, HtmlInline.class, SoftLineBreak.class, HardLineBreak.class));

  protected final DrawRendererContext context;

  public BaseDrawRenderer(DrawRendererContext context) {
    this.context = context;
  }

  @Override
  public Set<Class<? extends Node>> getNodeTypes() {
    return TypeSet;
  }

  @Override
  public void render(Node node) {
    node.accept(this);
  }

  @Override
  protected void visitChildren(Node parent) {
    Node node = parent.getFirstChild();
    while (node != null) {
      Node next = node.getNext();
      context.render(node);
      node = next;
    }
  }

  @Override
  public void visit(BlockQuote blockQuote) {
    context.boardLayers++;
    context.padding += 4;
    float begRend = context.rendOff;
    context.row();
    float begin = context.lineOff;

    Markdown.MarkdownStyle style = context.element.getStyle();
    context.lineOff += style.paragraphPadding + style.linesPadding;
    super.visit(blockQuote);
    context.padding -= 4;
    context.row();
    context.draw(DrawBoard.get(context.element, style.board, context.boardLayers, context.lineOff - begin, begRend, -begin));
    context.lineOff += style.paragraphPadding;
    context.boardLayers--;
  }

  @Override
  public void visit(BulletList bulletList) {
    context.row();
    context.padding += 4;
    context.listLayer++;

    Markdown.MarkdownStyle style = context.element.getStyle();
    bulletList.accept(new AbsExtensionVisitor() {
      @Override
      protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();

        while (node != null) {
          Node next = node.getNext();
          context.row();

          float ox = context.rendOff, oy = -context.lineOff - context.currFont.getLineHeight();
          Drawable drawer = style.listMarks[(context.listLayer - 1) % style.listMarks.length];
          float scl = context.currScl;
          Font cu = context.currFont;

          Markdown elem = context.element;
          context.draw(new Markdown.DrawObj() {
            @Override
            protected void draw() {
              Draw.color(style.textColor);
              Draw.alpha(elem.color.a*UncCore.INSTANCE.getFieldAccessHelper().getFloat(elem,"parentAlpha"));
              drawer.draw(elem.x + ox + drawer.getLeftWidth() * scl, elem.y + elem.getHeight() + oy, cu.getLineHeight() * scl, cu.getLineHeight() * scl);
            }
          });
          context.rendOff += (context.currFont.getLineHeight() + drawer.getLeftWidth() + drawer.getRightWidth()) * context.currScl;
          context.updateHeight((context.currFont.getLineHeight() + drawer.getBottomHeight() + drawer.getTopHeight()) * context.currScl);

          context.render(node);

          context.row();
          node = next;
        }
      }
    });
    context.listLayer--;
    context.padding -= 4;
    context.row();
  }

  @Override
  public void visit(Code code) {
    Markdown.MarkdownStyle style = context.element.getStyle();
    context.lastText = context.makeStr(code.getLiteral(), style.codeFont, style.codeBack, style.subTextColor);
  }

  @Override
  public void visit(Emphasis emphasis) {
    if (emphasis.getClosingDelimiter().length() > 1) return;

    Markdown.MarkdownStyle style = context.element.getStyle();

    Font last = context.currFont;
    Color lastColor = context.currFontColor;
    context.currFont = style.emFont;
    context.currFontColor = style.emColor;
    //rendOff[0] -= currFont.getSpaceXadvance()/2; //只在这里有半个多余的空格长度，原因不明，手动回退
    super.visit(emphasis);
    context.currFont = last;
    context.currFontColor = lastColor;
  }

  @Override
  public void visit(StrongEmphasis strongEmphasis) {
    Font last = context.currFont;
    Markdown.MarkdownStyle style = context.element.getStyle();
    Color lastColor = context.currFontColor;
    context.currFont = style.strongFont;
    context.currFontColor = strongEmphasis.getClosingDelimiter().length() % 2 == 0 ? lastColor : style.emColor;
    super.visit(strongEmphasis);
    context.currFont = last;
    context.currFontColor = lastColor;
  }

  @Override
  public void visit(FencedCodeBlock fencedCodeBlock) {
    context.lastText = null;
    context.makeCodeBox(fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral());
  }

  @Override
  public void visit(HardLineBreak hardLineBreak) {
    context.row();
  }

  @Override
  public void visit(Heading heading) {
    Markdown.MarkdownStyle style = context.element.getStyle();
    context.lastText = null;
    context.row();
    float re = context.currScl;
    context.currScl = 5f / heading.getLevel();
    context.lineOff += context.currScl * style.font.getLineHeight() / 2;
    super.visit(heading);
    context.lineOff += context.currScl * style.font.getLineHeight() / 2;
    context.currScl = re;
    context.row();
  }

  @Override
  public void visit(ThematicBreak thematicBreak) {
    context.lastText = null;
    context.row();
    context.draw(DrawHr.get(context.element, context.element.getStyle().lineColor, -context.lineOff));
    context.lineOff += 4;
    context.row();
    super.visit(thematicBreak);
  }

  @Override
  public void visit(HtmlInline htmlInline) {
    throw new IllegalArgumentException("not support raw html");
  }

  @Override
  public void visit(HtmlBlock htmlBlock) {
    throw new IllegalArgumentException("not support raw html");
  }

  @Override
  public void visit(Image image) {
    context.row();

    TextureRegion region = Core.atlas.find(image.getDestination());
    boolean isBase64 = false;
    for (String s : IMAGE_BASE_64_List) {
      if (image.getDestination().startsWith(s)) {
        region = context.imageCache(image, () -> new TextureRegion(new Texture(new Pixmap(Base64Coder.decode(image.getDestination().replace(s, ""))))));
        isBase64 = true;
        break;
      }
    }

    if (!isBase64 && !Core.atlas.isFound(region)) {
      region = context.imageCache(image, () -> downloadImg(image.getDestination(), Core.atlas.find("nomap")));
    }

    Markdown.MarkdownStyle style = context.element.getStyle();
    float w = Math.min(context.element.getWidth(), region.width), h = region.height * (w / region.width);
    if (image.getTitle() != null) h += 4 + style.subFont.getLineHeight();
    context.draw(DrawImg.get(context.element, region, image.getTitle(), -context.lineOff, style.subFont, style.subTextColor));
    context.lineOff += h;
    context.row();
  }

  @Override
  public void visit(IndentedCodeBlock indentedCodeBlock) {
    context.lastText = null;
    context.makeCodeBox(null, indentedCodeBlock.getLiteral());
  }

  @Override
  public void visit(Link link) {
    Color old = context.currFontColor;
    Markdown.MarkdownStyle style = context.element.getStyle();

    context.currFontColor = style.linkColor;

    link.accept(new AbsExtensionVisitor() {
      @Override
      public void visit(Text text) {
        context.makeStr(text.getLiteral().trim(), context.currFont, context.currFontColor, link.getDestination());
      }

      @Override
      public void visit(Emphasis emphasis) {
        if (emphasis.getClosingDelimiter().length() > 1) return;

        Font last = context.currFont;
        Color lastColor = context.currFontColor;
        context.currFont = style.emFont;
        context.currFontColor = style.emColor;
        super.visit(emphasis);
        context.currFont = last;
        context.currFontColor = lastColor;
      }

      @Override
      public void visit(StrongEmphasis strongEmphasis) {
        Font last = context.currFont;
        Color lastColor = context.currFontColor;
        context.currFont = style.strongFont;
        context.currFontColor = strongEmphasis.getClosingDelimiter().length() % 2 == 0 ? lastColor : style.emColor;
        super.visit(strongEmphasis);
        context.currFont = last;
        context.currFontColor = lastColor;
      }
    });

    context.currFontColor = old;
  }

  @Override
  public void visit(OrderedList orderedList) {
    context.row();
    context.padding += 4;
    orderedList.accept(new AbstractVisitor() {
      @Override
      protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();

        int begin = orderedList.getStartNumber();
        while (node != null) {
          Node next = node.getNext();
          context.row();
          context.lastText = context.makeStr(Integer.toString(begin++) + orderedList.getDelimiter() + " ", context.currFont, context.currFontColor);

          context.render(node);

          context.row();
          node = next;
        }
      }
    });
    context.padding -= 4;
    context.row();
  }

  @Override
  public void visit(SoftLineBreak softLineBreak) {
    context.row();
  }

  @Override
  public void visit(Text text) {
    context.lastText = context.makeStr(text.getLiteral().trim(), context.currFont, context.currFontColor);
  }

  @Override
  public void visit(LinkReferenceDefinition linkReferenceDefinition) {
    throw new UnsupportedOperationException("unsupport yet");
  }

  @Override
  public void visit(Paragraph paragraph) {
    context.lastText = null;
    super.visit(paragraph);
    if (!(paragraph.getParent() instanceof ListItem))
      context.totalHeight += context.element.getStyle().paragraphPadding;
    context.row();
  }

  private static TextureRegion downloadImg(String url, TextureRegion errDef) {
    TextureRegion result = new TextureRegion(errDef);

    retryDown(url, res -> {
      Pixmap pix = new Pixmap(res.getResult());
      Core.app.post(() -> {
        try {
          Texture tex = new Texture(pix);
          tex.setFilter(Texture.TextureFilter.linear);
          result.set(tex);
          pix.dispose();
        } catch (Exception e) {
          Log.err(e);
        }
      });
    }, 5, Log::err);

    return result;
  }

  private static void retryDown(String url, ConsT<Http.HttpResponse, Exception> resultHandler, int maxRetry, Cons<Throwable> errHandler) {
    int[] counter = {0};
    Runnable[] get = new Runnable[1];

    get[0] = () -> Http.get(url, resultHandler, e -> {
      if (counter[0]++ <= maxRetry) get[0].run();
      else errHandler.get(e);
    });
    get[0].run();
  }
}
