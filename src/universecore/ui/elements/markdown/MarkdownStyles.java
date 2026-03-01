package universecore.ui.elements.markdown;

import arc.Core;
import arc.files.Fi;
import arc.freetype.FreeTypeFontGenerator;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.scene.style.BaseDrawable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import universecore.ui.elements.markdown.highlighter.Highlighter;

import java.io.IOException;
import java.io.InputStream;

import static mindustry.gen.Tex.paneLeft;
import static mindustry.gen.Tex.whiteui;
import static universecore.ui.elements.markdown.highlighter.Scope.Default.*;
import static universecore.ui.elements.markdown.highlighter.Scope.Default.RainbowSeparator;
import static universecore.ui.elements.markdown.highlighter.Scope.JavaScope.*;
import static universecore.ui.elements.markdown.highlighter.Scope.LuaScope.*;
import static universecore.ui.elements.markdown.highlighter.StandardLanguages.JAVA;
import static universecore.ui.elements.markdown.highlighter.StandardLanguages.LUA;
public class MarkdownStyles {
  /**默认Markdown文档风格记录对象，等宽字体来自JetBrains IDEs的Mono字体，十分感谢*/
  public static final Markdown.MarkdownStyle defaultMD;

  static {
    Markdown.MarkdownStyle temp;
    try (InputStream stream = MarkdownStyles.class.getClassLoader().getResourceAsStream("fonts/JetBrainsMono.ttf")) {
      Fi f = Vars.modDirectory.child("JetBrainsMono.ttf");
      f.write(stream, false);

      FreeTypeFontGenerator gen = new FreeTypeFontGenerator(f);
      temp = makeDefault(gen.generateFont(new FreeTypeFontGenerator.FreeTypeFontParameter() {{
        size = (int) Scl.scl(19);
        borderWidth = Scl.scl(0.3f);
        shadowOffsetY = 2;
        incremental = true;
        borderColor = color;
      }}));

      f.delete();
    } catch (IOException e) {
      Log.err(e);
      temp = makeDefault(Fonts.def);
    }

    defaultMD = temp;
  }

  /**@deprecated 请使用makeDefault*/
  @Deprecated
  public static Markdown.MarkdownStyle defaultMD(Font mono) {
    return makeDefault(mono);
  }

  public static Markdown.MarkdownStyle makeDefault(Font mono){
    return new Markdown.MarkdownStyle(){{
      font = subFont = Fonts.def;
      codeFont = mono;
      FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Core.files.internal("fonts/font.woff"));
      strongFont = gen.generateFont(new FreeTypeFontGenerator.FreeTypeFontParameter(){{
        size = (int) Scl.scl(19);
        borderWidth = Scl.scl(0.3f);
        shadowOffsetY = 2;
        incremental = true;
        borderColor = color;
      }});
      emFont = Fonts.def;

      textColor = Color.white;
      emColor = Pal.accent;
      subTextColor = Color.lightGray;
      lineColor = Color.gray;
      linkColor = Pal.place;

      linesPadding = 5;
      tablePadHor = 14;
      tablePadVert = 10;
      paragraphPadding = 14;

      board = paneLeft;
      codeBack = ((TextureRegionDrawable) whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
      codeBack.setLeftWidth(4);
      codeBack.setRightWidth(4);
      codeBlockBack = ((TextureRegionDrawable) whiteui).tint(Tmp.c1.set(Pal.darkerGray));
      codeBlockStyle = Styles.smallPane;

      tableBack1 = ((TextureRegionDrawable) whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
      tableBack2 = ((TextureRegionDrawable) whiteui).tint(Tmp.c1.set(Color.gray).a(0.7f));

      curtain = ((TextureRegionDrawable) whiteui).tint(Pal.darkerGray);

      listMarks = new Drawable[]{
          new BaseDrawable(){
            @Override
            public void draw(float x, float y, float width, float height) {
              Fill.square(x + width/2, y + height/2, width*0.25f, 45f);
            }
          },
          new BaseDrawable(){
            @Override
            public void draw(float x, float y, float width, float height) {
              Fill.circle(x + width/2, y + height/2, width*0.3f);
            }
          },
          new BaseDrawable(){
            @Override
            public void draw(float x, float y, float width, float height) {
              Lines.stroke(1);
              Lines.circle(x + width/2, y + height/2, width*0.3f);
            }
          }
      };

      highlighter = new Highlighter().addLanguage(JAVA).addLanguage(LUA);
      codeColorProvider = new ColorProvider();
      codeColorProvider.defaultColor = Color.lightGray;
      codeColorProvider.createMap("java")
          .put(Color.valueOf("#ffffff00"),    SPACE)
          .put(Color.valueOf("#676d9dff"),    COMMENT)
          .put(Color.valueOf("#CC7832ff"),    KEYWORD, CONTROL, SEPARATOR)
          .put(Color.valueOf("#6897bbff"),    NUMBER)
          .put(Color.valueOf("#be7032ff"),    TYPE)
          .put(Color.valueOf("#507874ff"),    TYPE_ARG)
          .put(Color.valueOf("#6a8759ff"),    STRING)
          .put(Color.valueOf("#bbb529ff"),    ANNOTATION)
          .put(Color.valueOf("#629755ff"),    DOCS, DOC_MARK)
          .put(Color.valueOf("#ffc66dff"),    FUNCTION)
          .put(Color.valueOf("#3c7893ff"),    CONSTRUCTOR)
          .put(Color.valueOf("#bfbfbfff"),    NONE, FUNCTION_INVOKE, ARGUMENT, VARIABLE, OPERATOR);
      codeColorProvider.createMap("lua")
          .put(Color.valueOf("#ffffff00"),    SPACE)
          .put(Color.valueOf("#7f7f7fff"),    COMMENT)
          .put(Color.valueOf("#c09df6ff"),    KEYWORD)
          .put(Color.valueOf("#c09df6ff"),    KEYWORD_BODY)
          .put(Color.valueOf("#dbaad9ff"),    KEYWORD_CONTROL)
          .put(Color.valueOf("#ff7382ff"),    KEYWORD_SELF)
          .put(Color.valueOf("#f89876ff"),    KEYWORD_VAR1)
          .put(Color.valueOf("#6fb8ffff"),    KEYWORD_VAR2)
          .put(Color.valueOf("#5fd3c0ff"),    TABLE_VARS)
          .put(Color.valueOf("#9cd7f1ff"),    CONTROL, SEPARATOR, OPERATOR)
          .put(Color.valueOf("#ee9c7cff"),    NUMBER)
          .put(Color.valueOf("#e47c8fff"),    SEPARATOR)
          .put(Color.valueOf("#f4c87cff"),    ARGUMENT)
          .put(Color.valueOf("#c5e59aff"),    STRING)
          .put(Color.valueOf("#82a3f2ff"),    FUNCTION)
          .put(Color.valueOf("#6fb8ffff"),    FUNCTION_INVOKE)
          .put(Color.valueOf("#82a3f2ff"),    KEYWORD_FUNCTION)
          .put(Color.valueOf("#ccd4f2ff"),    NONE, VARIABLE, LOCAL_VARS)
          .putProv(scope -> scope instanceof RainbowSeparator, (RainbowSeparator scope) -> switch(scope.depth){
            case 0 -> Color.valueOf("#fe767cff");
            case 1 -> Color.valueOf("#8cdef5ff");
            case 2 -> Color.valueOf("#f5a4dfff");
            case 3 -> Color.valueOf("#c6e88dff");
            case 4 -> Color.valueOf("#fc976aff");
            case 5 -> Color.valueOf("#85aaffff");
            case 6 -> Color.valueOf("#ffc37aff");
            default -> throw new AssertionError();
          });
          //.put(Color.valueOf("#fe767cff"),    new RainbowSeparator(0, SEPARATOR))
          //.put(Color.valueOf("#8cdef5ff"),    new RainbowSeparator(1, SEPARATOR))
          //.put(Color.valueOf("#f5a4dfff"),    new RainbowSeparator(2, SEPARATOR))
          //.put(Color.valueOf("#c6e88dff"),    new RainbowSeparator(3, SEPARATOR))
          //.put(Color.valueOf("#fc976aff"),    new RainbowSeparator(4, SEPARATOR))
          //.put(Color.valueOf("#85aaffff"),    new RainbowSeparator(5, SEPARATOR))
          //.put(Color.valueOf("#ffc37aff"),    new RainbowSeparator(6, SEPARATOR));
    }};
  }
}
