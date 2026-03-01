package universecore.ui.elements.markdown.highlighter;

import java.util.Stack;

public class MatcherContext extends TokensContext {
  private final Stack<Block> blockStack = new Stack<>();
  private final Stack<Block> rawContextBlockStack = new Stack<>();

  private int currentIndex = 0;

  public void pushBlock(Block block){
    (inRawContext? rawContextBlockStack: blockStack).push(block);
  }

  public Block peekBlock(){
    Stack<Block> stack = inRawContext? rawContextBlockStack: blockStack;
    return stack.isEmpty()? null: stack.peek();
  }

  public void popBlock(){
    (inRawContext? rawContextBlockStack: blockStack).pop();
  }

  public int blockDepth(){
    return (inRawContext? rawContextBlockStack: blockStack).size();
  }

  public MatcherContext subContext(){
    MatcherContext res = new MatcherContext();
    res.currentIndex = currentIndex;
    res.tokens.addAll(tokens);
    res.rawTokens.addAll(rawTokens);
    return res;
  }
}
