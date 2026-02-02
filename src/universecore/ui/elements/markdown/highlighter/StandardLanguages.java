package universecore.ui.elements.markdown.highlighter;

import universecore.ui.elements.markdown.highlighter.defaults.JavaHighlight;
import universecore.ui.elements.markdown.highlighter.defaults.LuaHighlight;

public class StandardLanguages {
  public static PatternsHighlight JAVA,LUA;

  static {
    JAVA = JavaHighlight.create();
    LUA = LuaHighlight.create();
  }
}
