package universecore.ui.elements.markdown;

import org.commonmark.Extension;
import org.commonmark.internal.renderer.NodeRendererMap;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.ArrayList;
import java.util.List;

public class MDLayoutRenderer {
  private final List<DrawRendererFactory> nodeRendererFactories;
  private RendererContext context;

  private MDLayoutRenderer(Builder builder) {
    nodeRendererFactories = new ArrayList<>(builder.nodeRendererFactories.size() + 1);
    nodeRendererFactories.addAll(builder.nodeRendererFactories);

    nodeRendererFactories.add(BaseDrawRenderer::new);
  }

  public static Builder builder() {
    return new Builder();
  }

  public RendererContext createContext(Markdown element) {
    context = new RendererContext(element);
    return context;
  }

  public void renderLayout(Node node) {
    if (context == null) throw new IllegalStateException("context must be created first");
    context.init();
    context.render(node);
  }

  public static class Builder {
    private final List<DrawRendererFactory> nodeRendererFactories = new ArrayList<>();

    public MDLayoutRenderer build() {
      return new MDLayoutRenderer(this);
    }

    public Builder nodeRendererFactory(DrawRendererFactory nodeRendererFactory) {
      if (nodeRendererFactory == null) {
        throw new NullPointerException("nodeRendererFactory must not be null");
      }
      this.nodeRendererFactories.add(nodeRendererFactory);
      return this;
    }

    public Builder extensions(Iterable<? extends Extension> extensions) {
      if (extensions == null) {
        throw new NullPointerException("extensions must not be null");
      }
      for (Extension extension : extensions) {
        if (extension instanceof DrawRendererExtension ext) {
          ext.extend(this);
        }
      }
      return this;
    }
  }

  public interface DrawRendererExtension extends Extension {
    void extend(Builder rendererBuilder);
  }

  public class RendererContext extends DrawRendererContext {
    protected final NodeRendererMap nodeRendererMap = new NodeRendererMap();

    private RendererContext(Markdown element) {
      super(element);

      for (int i = nodeRendererFactories.size() - 1; i >= 0; i--) {
        DrawRendererFactory nodeRendererFactory = nodeRendererFactories.get(i);
        NodeRenderer nodeRenderer = nodeRendererFactory.create(this);
        nodeRendererMap.add(nodeRenderer);
      }
    }

    @Override
    public void render(Node node){
      nodeRendererMap.render(node);
    }
  }
}
