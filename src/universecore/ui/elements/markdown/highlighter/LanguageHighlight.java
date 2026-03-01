package universecore.ui.elements.markdown.highlighter;

public interface LanguageHighlight<C extends TokensContext> {
  String language();

  C initContext();

  void splitTokens(TokensContext context, String text);

  int flowScope(C context, Token token);
}
