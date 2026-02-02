package universecore.ui.elements.markdown;

import org.commonmark.renderer.NodeRenderer;

public interface DrawRendererFactory {
  NodeRenderer create(DrawRendererContext context);
}
