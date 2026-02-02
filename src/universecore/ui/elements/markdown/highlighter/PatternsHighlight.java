package universecore.ui.elements.markdown.highlighter;

import arc.func.Cons;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsHighlight implements LanguageHighlight<MatcherContext>, NameIndexer<TokenMatcher> {
  private final String language;
  private final Map<String, TokenMatcher> matchers = new LinkedHashMap<>();
  private final Map<String, TokenMatcher> rawContextMatchers = new LinkedHashMap<>();

  public Pattern tokensSplit;
  public Pattern rawTokenMatcher;
  public Pattern symbolMatcher;

  public PatternsHighlight(String language) {
    this.language = language;
  }

  public PatternsHighlight addPattern(String patternName, TokenMatcher matcher) {
    this.matchers.put(patternName, matcher.create());

    return this;
  }

  public PatternsHighlight addRawContextPattern(String patternName, TokenMatcher matcher) {
    this.rawContextMatchers.put(patternName, matcher.create());

    return this;
  }

  @Override
  public List<TokenMatcher> indexes(String... names) {
    List<TokenMatcher> list = new ArrayList<>();
    Set<String> set = new HashSet<>(Arrays.asList(names));
    for (Map.Entry<String, TokenMatcher> entry : matchers.entrySet()) {
      if (set.contains(entry.getKey())) list.add(entry.getValue());
    }
    return list;
  }

  @Override
  public List<TokenMatcher> allIndexed() {
    return new ArrayList<>(matchers.values());
  }

  @Override
  public String language() {
    return language;
  }

  @Override
  public MatcherContext initContext() {
    MatcherContext res = new MatcherContext();

    res.inRawContext = false;
    List<TokenMatcher> list = new ArrayList<>();
    for (TokenMatcher tokenMatcher : matchers.values()) {
      list.add(tokenMatcher.create());
    }
    res.pushBlock(new Block(null, list));

    res.inRawContext = true;
    List<TokenMatcher> raw = new ArrayList<>();
    for (TokenMatcher tokenMatcher : rawContextMatchers.values()) {
      raw.add(tokenMatcher.create());
    }
    res.pushBlock(new Block(null, raw));

    res.inRawContext = false;
    return res;
  }

  @Override
  public void splitTokens(TokensContext context, String text) {
    splitMatcher(rawTokenMatcher, text,
        str -> splitMatcher(tokensSplit, str,
            t -> {
              Token token = new Token(t);
              token.scope = Scope.Default.COMMENT;
              context.putTokenRaw(token);
            },
            t -> splitMatcher(symbolMatcher, t, sym -> {
              Token newToken = new Token(sym);
              context.putTokenRaw(newToken);
            })
        ),
        str -> splitMatcher(tokensSplit, str,
            t -> {
              Token token = new Token(t);
              token.scope = Scope.Default.SPACE;
              context.putTokenRaw(token);
            },
            t -> splitMatcher(symbolMatcher, t, sym -> {
              Token newToken = new Token(sym);
              context.putToken(newToken);
            })
        )
    );

  }

  private void splitMatcher(Pattern pattern, String str, Cons<String> cons){
    splitMatcher(pattern, str, cons, cons);
  }

  private void splitMatcher(Pattern pattern, String str, Cons<String> join, Cons<String> side){
    Matcher matcher = pattern.matcher(str);
    int lastEnd = 0;

    while (matcher.find()) {
      int matchStart = matcher.start();

      if (matchStart != lastEnd) {
        side.get(str.substring(lastEnd, matchStart));
      }

      lastEnd = matcher.end();

      join.get(matcher.group());
    }

    if (lastEnd != str.length()) {
      side.get(str.substring(lastEnd));
    }
  }

  @Override
  public int flowScope(MatcherContext context, Token token) {
    Block block = context.peekBlock();
    Scope scope = block.scope();
    if (scope != null) token.scope = scope;

    List<TokenMatcher> mats = block.matchers();
    mats.sort(TokenMatcher::compareTo);

    for (TokenMatcher matcher : mats) {
      try {
        int n = matcher.match(context, token);
        matcher.apply(context, token);

        return n;
      } catch (TokenMatcher.MatchFailed ignored) {}
    }

    return 1;
  }
}
