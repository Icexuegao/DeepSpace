package universecore.ui.elements.markdown.highlighter;

import java.util.List;

public interface TokenMatcher extends Comparable<TokenMatcher> {
  int match(MatcherContext context, Token token) throws MatchFailed;
  void apply(MatcherContext context, Token token);
  TokenMatcher create();
  int getPriority();

  default int compareTo(TokenMatcher o) {
    return Integer.compare(o.getPriority(), getPriority());
  }

  interface MatcherGroup {
    List<TokenMatcher> asMatchers();
  }

  class MatchFailed extends Throwable{
    public static final MatchFailed INSTANCE = new MatchFailed();

    private MatchFailed() {}
  }
}
