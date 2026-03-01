package universecore.ui.elements.markdown;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.tables.*;
import org.commonmark.ext.ins.Ins;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import universecore.ui.elements.markdown.extensions.Curtain;

public abstract class AbsExtensionVisitor extends AbstractVisitor {
  @Override
  public void visit(CustomNode customNode) {
    if (customNode instanceof TableBody body) visit(body);
    else if (customNode instanceof TableHead head) visit(head);
    else if (customNode instanceof TableRow row) visit(row);
    else if (customNode instanceof TableCell cell) visit(cell);
    else if (customNode instanceof Ins ins) visit(ins);
    else if (customNode instanceof Strikethrough strikethrough) visit(strikethrough);
    else if (customNode instanceof Curtain curtain) visit(curtain);
    else super.visit(customNode);
  }

  @Override
  public void visit(CustomBlock customBlock) {
    if (customBlock instanceof TableBlock table) visit(table);
    else super.visit(customBlock);
  }

  public void visit(TableBlock table){
    visitChildren(table);
  }

  public void visit(TableHead head){
    visitChildren(head);
  }

  public void visit(TableBody body){
    visitChildren(body);
  }

  public void visit(TableRow tableRow){
    visitChildren(tableRow);
  }

  public void visit(TableCell cell){
    visitChildren(cell);
  }

  public void visit(Strikethrough strikethrough){
    visitChildren(strikethrough);
  }

  public void visit(Ins ins){
    visitChildren(ins);
  }

  public void visit(Curtain curtain){
    visitChildren(curtain);
  }
}
