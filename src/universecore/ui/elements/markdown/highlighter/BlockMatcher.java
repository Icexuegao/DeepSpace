package universecore.ui.elements.markdown.highlighter;

import java.util.*;

import static universecore.ui.elements.markdown.highlighter.SerialMatcher.applyCapture;
import static universecore.ui.elements.markdown.highlighter.SerialMatcher.matchCapture;

public class BlockMatcher implements TokenMatcher, NameIndexer<TokenMatcher>{
  private Scope scope;
  private int priority = 0;
  private List<Capture> beginCaptures;
  private List<Capture> endCaptures;
  private final Map<String, TokenMatcher> children = new LinkedHashMap<>();

  private int[] beginLens;
  private int[] endLens;

  public static BlockMatcher create(List<Capture> beginCaptures, List<Capture> endCaptures){
    return create(null, beginCaptures, endCaptures);
  }

  public static BlockMatcher create(Scope scope, List<Capture> beginCaptures, List<Capture> endCaptures){
    return create(0, scope, beginCaptures, endCaptures);
  }

  public static BlockMatcher create(int priority, List<Capture> beginCaptures, List<Capture> endCaptures){
    return create(priority, null, beginCaptures, endCaptures);
  }

  public static BlockMatcher create(int priority, Scope scope, List<Capture> beginCaptures, List<Capture> endCaptures){
    BlockMatcher res = new BlockMatcher();
    res.scope = scope;
    res.priority = priority;
    res.beginCaptures = beginCaptures;
    res.endCaptures = endCaptures;

    res.beginLens = new int[beginCaptures.size()];
    res.endLens = new int[endCaptures.size()];

    return res;
  }

  public BlockMatcher addChildPattern(String patternName, TokenMatcher matcher){
    children.put(patternName, matcher.create());
    return this;
  }

  @Override
  public int match(MatcherContext context, Token token) throws MatchFailed {
    return matchCapture(beginCaptures, beginLens, context, token);
  }

  @Override
  public void apply(MatcherContext context, Token token) {
    applyCapture(scope, beginCaptures, beginLens, context, token);

    List<TokenMatcher> matchers = new ArrayList<>(children.values());
    matchers.add(new InnerBlockMatcher());
    context.pushBlock(new Block(scope, this, matchers));
  }

  @Override
  public TokenMatcher create() {
    BlockMatcher res = create(priority, scope, beginCaptures, endCaptures);
    children.forEach(res::addChildPattern);
    return res;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @SuppressWarnings("DuplicatedCode")
  @Override
  public List<TokenMatcher> indexes(String... names){
    List<TokenMatcher> list = new ArrayList<>();
    Set<String> set = new HashSet<>(Arrays.asList(names));
    for (Map.Entry<String, TokenMatcher> entry : children.entrySet()) {
      if(set.contains(entry.getKey())) list.add(entry.getValue());
    }
    return list;
  }

  @Override
  public List<TokenMatcher> allIndexed(){
    return new ArrayList<>(children.values());
  }

  private class InnerBlockMatcher implements TokenMatcher {
    @Override
    public int match(MatcherContext context, Token token) throws MatchFailed {
      return matchCapture(endCaptures, endLens, context, token);
    }

    @Override
    public void apply(MatcherContext context, Token token) {
      applyCapture(scope, endCaptures, endLens, context, token);
      context.popBlock();
    }

    @Override
    public TokenMatcher create() {
      return new InnerBlockMatcher();
    }

    @Override
    public int getPriority() {
      return priority;
    }
  }
}
