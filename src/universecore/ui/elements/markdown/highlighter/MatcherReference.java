package universecore.ui.elements.markdown.highlighter;

import java.util.List;

public class MatcherReference implements TokenMatcher, TokenMatcher.MatcherGroup {
  private final int priority;
  private final NameIndexer<TokenMatcher> nameIndexer;
  private final String[] patternNames;

  public MatcherReference(NameIndexer<TokenMatcher> map, String... patternNames) {
    this.priority = 0;
    this.nameIndexer = map;
    this.patternNames = patternNames;
  }

  public MatcherReference(NameIndexer<TokenMatcher> map) {
    this.priority = 0;
    this.nameIndexer = map;
    this.patternNames = null;
  }

  public MatcherReference(int priority, NameIndexer<TokenMatcher> map, String... patternNames) {
    this.priority = priority;
    this.nameIndexer = map;
    this.patternNames = patternNames;
  }

  public MatcherReference(int priority, NameIndexer<TokenMatcher> map) {
    this.priority = priority;
    this.nameIndexer = map;
    this.patternNames = null;
  }

  @Override
  public List<TokenMatcher> asMatchers(){
    return patternNames == null? nameIndexer.allIndexed(): nameIndexer.indexes(patternNames);
  }

  @Override
  public int match(MatcherContext context, Token token) throws MatchFailed {
    throw MatchFailed.INSTANCE;
  }

  @Override
  public void apply(MatcherContext context, Token token) {}

  @Override
  public TokenMatcher create() {
    return new MatcherReference(priority, nameIndexer, patternNames);
  }

  @Override
  public int getPriority() {
    return priority;
  }
}
