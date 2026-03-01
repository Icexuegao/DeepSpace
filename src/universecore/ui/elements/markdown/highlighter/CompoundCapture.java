package universecore.ui.elements.markdown.highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompoundCapture extends Capture{
  private final int minMatch;
  private final int maxMatch;
  private final List<Capture> captures;
  private Capture endCapture;

  private final List<List<Capture>> cap = new ArrayList<>();
  private final List<int[]> lens = new ArrayList<>();
  private final List<Integer> off = new ArrayList<>();

  public CompoundCapture(Capture... captures) {
    this(1, captures);
  }

  public CompoundCapture(int matches, Capture... captures) {
    this(matches, matches, captures);
  }

  public CompoundCapture(int minMatch, int maxMatch, Capture... captures) {
    this.minMatch = minMatch;
    this.maxMatch = maxMatch;
    this.captures = Arrays.asList(captures);
  }

  public CompoundCapture setEndCapture(Capture endCapture) {
    this.endCapture = endCapture;
    return this;
  }

  @Override
  public int match(MatcherContext context, Token token) throws TokenMatcher.MatchFailed {
    lens.clear();
    off.clear();
    cap.clear();

    boolean[] ended = new boolean[1];
    int off = 0;
    int max = context.getTokensCountInContext();
    for (int i = 0; i < maxMatch; i++) {
      if (token.getIndexInContext(context) + off >= max){
        if (i < minMatch) throw TokenMatcher.MatchFailed.INSTANCE;
        else break;
      }

      Token curr = context.getTokenInContext(token.getIndexInContext(context) + off);

      List<Capture> capt = new ArrayList<>();
      for (Capture c : captures) capt.add(c.create());
      cap.add(capt);

      int[] len = new int[captures.size()];
      try {
        ended[0] = false;
        int l = SerialMatcher.matchCapture(capt, endCapture, () -> ended[0] = true, len, context, curr);
        lens.add(len);
        this.off.add(off);
        if (ended[0] && endCapture != null && endCapture.matchOnly) break;
        off += l;
        if (ended[0]) break;
      } catch (TokenMatcher.MatchFailed e) {
        if (i < minMatch) throw e;
        else break;
      }
    }

    return off;
  }

  @Override
  public void applyScope(MatcherContext context, Token token, int matchedLen) {
    int max = context.getTokensCountInContext();
    for (int i = 0; i < off.size(); i++) {
      if (token.getIndexInContext(context) + off.get(i) >= max) break;
      SerialMatcher.applyCapture(cap.get(i), lens.get(i), context,
          context.getTokenInContext(token.getIndexInContext(context) + off.get(i))
      );
    }
  }

  @Override
  public CompoundCapture create() {
    Capture[] cap = new Capture[captures.size()];
    for (int i = 0; i < cap.length; i++) cap[i] = captures.get(i).create();
    CompoundCapture capture = new CompoundCapture(minMatch, maxMatch, cap);
    capture.setMatchOnly(matchOnly).setOptional(optional);
    capture.endCapture = endCapture;
    return capture;
  }
}
