package universecore.ui.elements.markdown.highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockCapture extends Capture{
  private final Capture beginCapture;
  private final Capture endCapture;

  private final List<TokenMatcher> children = new ArrayList<>();
  private List<TokenMatcher> list;

  public BlockCapture(Capture beginCapture, Capture endCapture) {
    this.beginCapture = beginCapture;
    this.endCapture = endCapture;
  }

  public BlockCapture addChildPatterns(TokenMatcher... matchers){
    children.addAll(Arrays.asList(matchers));
    return this;
  }

  @Override
  public int match(MatcherContext context, Token token) throws TokenMatcher.MatchFailed {
    if (list == null) {
      list = new ArrayList<>();
      for (TokenMatcher child : children) {
        list.add(child.create());
      }
    }

    int len = beginCapture.match(context, token);

    MatcherContext subContext = context.subContext();
    subContext.forwardCursor(len);
    subContext.pushBlock(new Block(null, list));

    c: while (subContext.currCursor() < subContext.getTokensCountInContext()){
      Token curr = subContext.getTokenInContext(subContext.currCursor());

      Block block = subContext.peekBlock();
      Scope scope = block.scope();

      List<TokenMatcher> mats = block.matchers();
      mats.sort(TokenMatcher::compareTo);

      for (TokenMatcher matcher : mats) {
        try {
          int n = matcher.match(subContext, curr);
          if (scope != null) curr.scope = scope;
          matcher.apply(subContext, curr);

          subContext.forwardCursor(n);
          len += n;

          continue c;
        } catch (TokenMatcher.MatchFailed ignored) {}
      }

      try {
        int endLen = endCapture.match(subContext, curr);
        subContext.popBlock();
        subContext.forwardCursor(endLen);
        len += endLen;

        break;
      } catch (TokenMatcher.MatchFailed ignored) {}
    }

    return len;
  }

  @Override
  public void applyScope(MatcherContext context, Token token, int matchedLen) {}

  @Override
  public Capture create() {
    return new BlockCapture(beginCapture, endCapture).addChildPatterns(children.toArray(new TokenMatcher[0]));
  }
}
