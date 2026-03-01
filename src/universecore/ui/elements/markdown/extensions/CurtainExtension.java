package universecore.ui.elements.markdown.extensions;

import org.commonmark.Extension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import universecore.ui.elements.markdown.DrawRendererContext;
import universecore.ui.elements.markdown.LayoutNodeRenderer;
import universecore.ui.elements.markdown.MDLayoutRenderer;
import universecore.ui.elements.markdown.Markdown;
import universecore.ui.elements.markdown.elemdraw.DrawCurtain;

import java.util.Collections;
import java.util.Set;

public class CurtainExtension implements Parser.ParserExtension, MDLayoutRenderer.DrawRendererExtension {
  private CurtainExtension() {
  }

  public static Extension create() {
    return new CurtainExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customDelimiterProcessor(new CurtainDelimiterProcessor());
  }

  @Override
  public void extend(MDLayoutRenderer.Builder rendererBuilder) {
    rendererBuilder.nodeRendererFactory(CurtainRenderer::new);
  }

  private static class CurtainRenderer extends LayoutNodeRenderer {
    public CurtainRenderer(DrawRendererContext context) {
      super(context);
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
      return Collections.singleton(Curtain.class);
    }

    @Override
    public void render(Node node) {
      Markdown.MarkdownStyle style = context.element.getStyle();
      context.lastText = null;
      float begin = context.rendOff;
      context.rendOff += style.curtain.getLeftWidth();

      visitChildren(node);

      context.rendOff += style.curtain.getRightWidth();
      context.draw(DrawCurtain.get(context.element, style.curtain, begin, context.lastText.offy,
          context.lastText.width + style.curtain.getLeftWidth() + style.curtain.getRightWidth(),
          context.lastText.height + style.curtain.getTopHeight() + style.curtain.getBottomHeight()
      ));
    }
  }
}
