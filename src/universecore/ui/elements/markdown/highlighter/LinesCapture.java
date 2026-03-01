package universecore.ui.elements.markdown.highlighter;

import java.util.regex.Pattern;

public class LinesCapture extends Capture{
  private final Pattern lineSep;
  private final Scope scope;

  public LinesCapture(){
    this.scope = null;
    this.lineSep = Pattern.compile("\\r\\n|\\r|\\n");
  }

  public LinesCapture(Scope scope){
    this.scope = scope;
    this.lineSep = Pattern.compile("\\r\\n|\\r|\\n");
  }

  public LinesCapture(Scope scope, Pattern lineSep) {
    this.lineSep = lineSep;
    this.scope = scope;
  }

  @Override
  public int match(MatcherContext context, Token token) throws TokenMatcher.MatchFailed {
    if (token.rawIndex >= context.getTokensRaw().size() - 1)
      return 1;

    if (!lineSep.matcher(context.getTokensRaw().get(token.rawIndex + (context.inRawContext? 0: 1)).text).find())
      throw TokenMatcher.MatchFailed.INSTANCE;

    return 1;
  }

  @Override
  public void applyScope(MatcherContext context, Token token, int matchedLen) {
    if (scope != null) token.scope = scope;
  }

  @Override
  public LinesCapture create() {
    LinesCapture capture = new LinesCapture(scope);
    capture.setMatchOnly(matchOnly).setOptional(optional);
    return capture;
  }
}
