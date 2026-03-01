package universecore.ui.elements.markdown.extensions;

import org.commonmark.node.Node;
import org.commonmark.node.Nodes;
import org.commonmark.node.SourceSpans;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

public class CurtainDelimiterProcessor implements DelimiterProcessor {
  private static final char delem = '$';

  @Override
  public char getOpeningCharacter() {
    return delem;
  }

  @Override
  public char getClosingCharacter() {
    return delem;
  }

  @Override
  public int getMinLength() {
    return 1;
  }

  @Override
  public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
    if (openingRun.length() == 1 && closingRun.length() == 1) {
      SourceSpans sourceSpans = new SourceSpans();
      sourceSpans.addAllFrom(openingRun.getOpeners(1));
      Text opener = openingRun.getOpener();

      Node res = new Curtain();
      for (Node node : Nodes.between(opener, closingRun.getCloser())) {
        res.appendChild(node);
        sourceSpans.addAll(node.getSourceSpans());
      }
      sourceSpans.addAllFrom(closingRun.getClosers(1));
      res.setSourceSpans(sourceSpans.getSourceSpans());
      opener.insertAfter(res);

      return 1;
    } else {
      return 0;
    }
  }
}
