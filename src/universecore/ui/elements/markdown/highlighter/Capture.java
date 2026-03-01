package universecore.ui.elements.markdown.highlighter;

public abstract class Capture {
  public boolean matchOnly = false;
  public boolean optional = false;

  public Capture setMatchOnly(boolean matchOnly) {
    this.matchOnly = matchOnly;
    return this;
  }

  public Capture setOptional(boolean optional) {
    this.optional = optional;
    return this;
  }

  public abstract int match(MatcherContext context, Token token) throws TokenMatcher.MatchFailed;
  public abstract void applyScope(MatcherContext context, Token token, int matchedLen);

  public abstract Capture create();
}
