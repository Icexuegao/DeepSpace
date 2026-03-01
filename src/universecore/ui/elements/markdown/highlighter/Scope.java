package universecore.ui.elements.markdown.highlighter;

import java.util.Objects;

public interface Scope {
  void apply(Token token, ScopeHandler handler);

  enum Default implements Scope {
    //internal special
    NONE,
    SPACE,
    KEYWORD,
    OPERATOR,
    TYPE,
    STRING,
    NUMBER,
    CONTROL,
    COMMENT,
    SEPARATOR,
    VARIABLE,
    FUNCTION,
    FUNCTION_INVOKE,
    ARGUMENT;

    @Override
    public void apply(Token token, ScopeHandler handler) {
      handler.applyScope(token, this);
    }
  }

  enum JavaScope implements Scope{
    TYPE_ARG,
    FIELD,
    DOCS,
    DOC_MARK,
    CONSTRUCTOR,
    ANNOTATION;

    @Override
    public void apply(Token token, ScopeHandler handler) {
      handler.applyScope(token, this);
    }
  }

  enum LuaScope implements Scope{
    LOCAL_VARS,
    TABLE_VARS,
    KEYWORD_BODY,
    KEYWORD_CONTROL,
    KEYWORD_VAR1,
    KEYWORD_VAR2,
    KEYWORD_SELF,
    KEYWORD_FUNCTION;

    @Override
    public void apply(Token token, ScopeHandler handler) {
      handler.applyScope(token, this);
    }
  }

  class RainbowSeparator implements Scope{
    public int depth;
    public Scope type;

    public RainbowSeparator(int depth,Scope type){
      this.depth = depth;
      this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RainbowSeparator obj=((RainbowSeparator)o);
        return obj.depth==depth&&Objects.equals(obj.type, type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(depth, type);
    }

    @Override
    public void apply(Token token, ScopeHandler handler) {
      handler.applyScope(token, this);
    }

    @Override
    public String toString() {
        return "RainbowSeparator{"+"depth="+depth+", type="+type.toString()+"}";
    }
  }
}
