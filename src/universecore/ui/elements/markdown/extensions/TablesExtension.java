package universecore.ui.elements.markdown.extensions;

import arc.scene.ui.layout.Table;
import arc.util.Align;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.*;
import org.commonmark.ext.gfm.tables.internal.TableBlockParser;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import universecore.ui.elements.markdown.DrawRendererContext;
import universecore.ui.elements.markdown.LayoutNodeRenderer;
import universecore.ui.elements.markdown.MDLayoutRenderer;
import universecore.ui.elements.markdown.Markdown;
import universecore.ui.elements.markdown.elemdraw.DrawTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TablesExtension implements Parser.ParserExtension, MDLayoutRenderer.DrawRendererExtension {
  private TablesExtension() {
  }

  public static Extension create() {
    return new TablesExtension();
  }

  @Override
  public void extend(Parser.Builder parserBuilder) {
    parserBuilder.customBlockParserFactory(new TableBlockParser.Factory());
  }

  @Override
  public void extend(MDLayoutRenderer.Builder rendererBuilder) {
    rendererBuilder.nodeRendererFactory(TableRenderer::new);
  }

  private static class TableRenderer extends LayoutNodeRenderer {
    private Table currentTable;

    public TableRenderer(DrawRendererContext context) {
      super(context);
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
      return new HashSet<>(Arrays.asList(
          TableBlock.class,
          TableHead.class,
          TableBody.class,
          TableRow.class,
          TableCell.class
      ));
    }

    @Override
    public void render(Node node){
      if (node instanceof TableBlock block) renderBlock(block);
      else if (node instanceof TableHead head) renderHead(head);
      else if (node instanceof TableBody body) renderBody(body);
      else if (node instanceof TableRow row) renderRow(row);
      else if (node instanceof TableCell cell) renderCell(cell);
    }

    protected void renderBlock(TableBlock node){
      Table last = currentTable;
      currentTable = new Table();

      visitChildren(node);

      Markdown.MarkdownStyle style = context.element.getStyle();
      context.row();
      context.lineOff += style.linesPadding;
      context.draw(DrawTable.get(context.element, new Table(t -> {
        t.image().color(style.lineColor).width(1.5f).growY();
        t.add(currentTable).fill();
        t.image().color(style.lineColor).width(1.5f).growY();
      }), context.rendOff, -context.lineOff));

      context.updateHeight(currentTable.getPrefHeight());
      context.row();
      context.lineOff += style.linesPadding;

      currentTable = last;
    }

    protected void renderHead(TableHead node){
      visitChildren(node);
    }

    protected void renderBody(TableBody node){
      visitChildren(node);
    }

    protected void renderRow(TableRow node){
      visitChildren(node);
      currentTable.row();
    }

    protected void renderCell(TableCell node){
      Markdown.MarkdownStyle style = context.element.getStyle();
      currentTable.table(currentTable.getRows()%2 == 0? style.tableBack1: style.tableBack2, t -> {
        t.image().color(style.lineColor).width(1.5f).growY();
        t.table(ce -> ce.add(new Markdown(context.element, node.getFirstChild())))
            .grow().pad(style.linesPadding*2).get()
            .align(switch (node.getAlignment()) {
                case LEFT -> Align.left;
                case RIGHT -> Align.right;
                default -> Align.center;
            });
        t.image().color(style.lineColor).width(1.5f).growY();
      }).grow();
    }
  }
}
