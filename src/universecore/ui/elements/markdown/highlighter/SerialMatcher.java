package universecore.ui.elements.markdown.highlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerialMatcher implements TokenMatcher {
  private List<Capture> captures;
  private int priority = 0;

  private int[] lens;

  private SerialMatcher(){}

  public static SerialMatcher create(Capture... captures){
    SerialMatcher res = new SerialMatcher();
    res.captures = new ArrayList<>(Arrays.asList(captures));
    res.priority = 0;
    res.lens = new int[captures.length];

    return res;
  }

  public static SerialMatcher create(int priority, Capture... captures){
    SerialMatcher res = new SerialMatcher();
    res.captures = new ArrayList<>(Arrays.asList(captures));
    res.priority = priority;
    res.lens = new int[captures.length];

    return res;
  }

  @Override
  public int match(MatcherContext context, Token token) throws MatchFailed {
    return SerialMatcher.matchCapture(captures, lens, context, token);
  }

  @Override
  public void apply(MatcherContext context, Token token) {
    SerialMatcher.applyCapture(captures, lens, context, token);
  }

  @Override
  public TokenMatcher create() {
    return create(priority, captures.toArray(new Capture[0]));
  }

  @Override
  public int getPriority() {
    return priority;
  }

  public static int matchCapture(List<Capture> captures, int[] lens, MatcherContext context, Token token) throws MatchFailed {
    Arrays.fill(lens, 0);

    int off = 0;
    int max = context.getTokensCountInContext();
    for (int i = 0, capturesSize = captures.size(); i < capturesSize; i++) {
      if (token.getIndexInContext(context) + off >= max) break;

      Capture capture = captures.get(i);
      Token curr = context.getTokenInContext(token.getIndexInContext(context) + off);

      try {
        int len = capture.match(context, curr);
        if (capture.matchOnly) continue;

        lens[i] = len;
        off += len;
      } catch (MatchFailed e){
        if (!capture.optional) throw e;
        else lens[i] = 0;
      }
    }

    return off;
  }

  public static int matchCapture(List<Capture> captures, Capture endCapture, Runnable ended, int[] lens, MatcherContext context, Token token) throws MatchFailed {
    Arrays.fill(lens, 0);

    int off = 0;
    int max = context.getTokensCountInContext();
    for (int i = 0, capturesSize = captures.size(); i < capturesSize; i++) {
      if (token.getIndexInContext(context) + off >= max) break;

      Capture capture = captures.get(i);
      Token curr = context.getTokenInContext(token.getIndexInContext(context) + off);

      if (endCapture != null) try{
        int len = endCapture.match(context, curr);
        endCapture.applyScope(context, curr, len);
        off += len;
        ended.run();
        break;
      } catch (MatchFailed ignored){}

      try {
        int len = capture.match(context, curr);
        if (capture.matchOnly) continue;

        lens[i] = len;
        off += len;
      } catch (MatchFailed e){
        if (!capture.optional) throw e;
        else lens[i] = 0;
      }
    }

    return off;
  }

  public static void applyCapture(List<Capture> captures, int[] lens, MatcherContext context, Token token){
    applyCapture(null, captures, lens, context, token);
  }

  public static void applyCapture(Scope altScope, List<Capture> captures, int[] lens, MatcherContext context, Token token){
    int off = 0;
    for (int i = 0; i < captures.size(); i++) {
      if (altScope != null) {
        for (int l = 0; l < lens[i]; l++) {
          context.getTokenInContext(token.getIndexInContext(context) + off + l).scope = altScope;
        }
      }

      if (captures.get(i).matchOnly || lens[i] <= 0) continue;

      captures.get(i).applyScope(context, context.getTokenInContext(token.getIndexInContext(context) + off), lens[i]);

      off += lens[i];
    }
  }
}
