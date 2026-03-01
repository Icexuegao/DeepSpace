package universecore.ui.elements.markdown.highlighter;

import java.util.List;

public interface NameIndexer<T> {
  List<TokenMatcher> indexes(String... names);

  List<TokenMatcher> allIndexed();
}
