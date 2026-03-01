package universecore.ui.elements.markdown;

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

public abstract class LayoutNodeRenderer implements NodeRenderer {
  protected final DrawRendererContext context;

  public LayoutNodeRenderer(DrawRendererContext context) {
    this.context = context;
  }

  protected void visitChildren(Node node){
    Node n = node.getFirstChild();
    while (n != null) {
      Node next = n.getNext();
      context.render(n);
      n = next;
    }
  }
}
