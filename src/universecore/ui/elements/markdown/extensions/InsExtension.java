package universecore.ui.elements.markdown.extensions;

import org.commonmark.Extension;
import org.commonmark.ext.ins.Ins;
import org.commonmark.ext.ins.internal.InsDelimiterProcessor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import universecore.ui.elements.markdown.DrawRendererContext;
import universecore.ui.elements.markdown.LayoutNodeRenderer;
import universecore.ui.elements.markdown.MDLayoutRenderer;
import universecore.ui.elements.markdown.TextMirror;
import universecore.ui.elements.markdown.elemdraw.DrawLine;

import java.util.Collections;
import java.util.Set;

public class InsExtension implements Parser.ParserExtension, MDLayoutRenderer.DrawRendererExtension {
  private InsExtension() {
  }

  public static Extension create() {
    return new InsExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customDelimiterProcessor(new InsDelimiterProcessor());
  }

  @Override
  public void extend(MDLayoutRenderer.Builder rendererBuilder) {
    rendererBuilder.nodeRendererFactory(LayoutInsNodeRenderer::new);
  }

  private static class LayoutInsNodeRenderer extends LayoutNodeRenderer {
    protected LayoutInsNodeRenderer(DrawRendererContext element) {
      super(element);
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
      return Collections.singleton(Ins.class);
    }

    @Override
    public void render(Node node) {
      context.lastText = null;

      visitChildren(node);

      TextMirror orig = context.lastText;
      while (orig != null){
        context.draw(DrawLine.get(
            context.element,
            context.lastText.fontColor,
            context.lastText.offx,
            context.lastText.offy - context.lastText.height,
            context.lastText.width
        ));

        orig = orig.sub;
      }
    }
  }
}
