package universecore.ui.elements.markdown.highlighter.defaults;

import universecore.ui.elements.markdown.highlighter.Capture;
import universecore.ui.elements.markdown.highlighter.PatternsHighlight;
import universecore.ui.elements.markdown.highlighter.SelectionCapture;

import java.util.regex.Pattern;

import static universecore.ui.elements.markdown.highlighter.Scope.Default.*;
import static universecore.ui.elements.markdown.highlighter.Scope.Default.RainbowSeparator;
import static universecore.ui.elements.markdown.highlighter.Scope.LuaScope.*;

/**
 * LuaHighlight
 */
public class LuaHighlight extends DefaultLangDSL {

  static Capture modifiersCapture() {
    return regex(1, Integer.MAX_VALUE, KEYWORD_CONTROL, "local");
  }

  static Capture variableCapture(int depth) {
    return forks(
        compound(
            modifiersCapture(),
            makeJoin(
                compound(
                    regex(LOCAL_VARS, "\\w+"),
                    compound(
                        token(OPERATOR, "="),
                        lazy(() -> expressionCapture(0))
                    ).setOptional(true)
                ),
                token(SEPARATOR, ",")
            )
        ),
        compound(
            makeJoin(
                compound(
                    //TODD depth
                    regex(depth > 0? TABLE_VARS: LOCAL_VARS, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+"),
                    token(OPERATOR, "="),
                    lazy(() -> expressionCapture(depth))
                ),
                token(SEPARATOR, ",")
            )
        )
    );
  }

  static Capture functionCapture() {
    return compound(
        token(KEYWORD_BODY, "function"),
        makeJoin(
            regex(FUNCTION, "\\w+"),
            forks(
                token(SEPARATOR, ":"),
                token(SEPARATOR, ".")
            )
        ),
        token(SEPARATOR, "("),
        makeJoin(
            forks(
                regex(ARGUMENT, "\\w+"),
                regex(ARGUMENT, "\\.+")
            ),
            token(SEPARATOR, ",")
        ).setOptional(true),
        token(SEPARATOR, ")"),
        functionStatementBlockCapture()
    );
  }

  static Capture statementCapture(int depth) {
    return forks(
        //IF
        compound(
            token(KEYWORD_BODY, "if"),
            expressionCapture(0),
            token(KEYWORD_BODY, "then"),
            ifStatementBlockCapture()
        ),
        //WHILE
        compound(
            token(KEYWORD_BODY, "while"),
            expressionCapture(0),
            token(KEYWORD_BODY, "do"),
            functionStatementBlockCapture()
        ),
        //REPEAT_UNTIL
        compound(
            token(KEYWORD_BODY, "repeat"),
            lazy(() -> statementsCapture(0)),
            token(KEYWORD_BODY, "until"),
            expressionCapture(0)
        ),
        //FOR
        compound(
            token(KEYWORD_BODY, "for"),
            expressionCapture(0),
            token(SEPARATOR, ","),
            expressionCapture(0),
            compound(
                token(SEPARATOR, ","),
                expressionCapture(0)
            ).setOptional(true),
            token(KEYWORD_BODY, "do"),
            functionStatementBlockCapture()
        ),
        //FOR IN
        compound(
            token(KEYWORD_BODY, "for"),
            makeJoin(
                regex(ARGUMENT, "\\w+"),
                token(SEPARATOR, ",")
            ),
            token(KEYWORD_CONTROL, "in"),
            expressionCapture(0),
            token(KEYWORD_BODY, "do"),
            functionStatementBlockCapture()
        ),

        //RETURN
        compound(
            token(KEYWORD_CONTROL, "return"),
            expressionCapture(0).setOptional(true)
        ),
        functionCapture(),
        variableCapture(depth),
        //EXPRESSION
        expressionCapture(depth),
        //CODE_BLOCK
        compound(
            token(KEYWORD_BODY, "do"),
            lazy(() -> statementsCapture(0)),
            token(KEYWORD_BODY, "end")
        )
    );
  }

  static Capture metaExpCapture(int depth) {
    return compound(
        forks(
            token(KEYWORD_SELF, "self"),
            compound(
                token(SEPARATOR, "("),
                lazy(() -> expressionCapture(depth)),
                token(SEPARATOR, ")")
            ),
            compound(
                token(new RainbowSeparator(depth%7, SEPARATOR), "{"),
                makeJoin(
                    lazy(() -> statementsCapture(depth + 1)),
                    token(SEPARATOR, ",")
                ),
                token(new RainbowSeparator(depth%7, SEPARATOR), "}")
            ),
            //LAMBDA
            compound(
                token(KEYWORD_BODY, "function"),
                token(SEPARATOR, "("),
                makeJoin(
                    forks(
                        regex(ARGUMENT, "\\w+"),
                        regex(ARGUMENT, "\\.+")
                    ),
                    token(SEPARATOR, ",")
                ).setOptional(true),
                token(SEPARATOR, ")"),
                functionStatementBlockCapture()
            ),
            //token(KEYWORD,"end").setMatchOnly(true),
            constantLiteralCapture(),
            //INVOKE
            compound(
                forks(
                    regex(KEYWORD_FUNCTION, "import"),
                    regex(FUNCTION_INVOKE, "require"),
                    regex(FUNCTION_INVOKE, "^(?!end$|do$|in$|function$|then$|if$|for$)\\w+")
                ),
                forks(
                    compound(
                        token("("),
                        makeJoin(
                            lazy(() -> expressionCapture(0)).setOptional(true),
                            token(SEPARATOR, ",")
                        ),
                        token(")")
                    ),
                    makeJoin(
                        lazy(() -> expressionCapture(0)),
                        token(SEPARATOR, ",")
                    )
                )
            ),
            //READ ARRAY
            compound(
                regex(VARIABLE, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+"),
                compound(1, Integer.MAX_VALUE,
                    token("["),
                    lazy(() -> expressionCapture(0)),
                    token("]")
                )
            ),
            compound(
                regex(OPERATOR, "[!+\\-~]"),
                lazy(() -> expressionCapture(0))
            ),
            //REF
            regex(VARIABLE, "^(?!end$|in$|do$|function$|if$|then$|for$)\\w+")
        )
    );
  }

  static Capture bracketStringCapture() {
    return compound(
        compound(
            token(STRING, "["),
            token(0, Integer.MAX_VALUE, STRING, "="),
            token(STRING, "[")
        ),
        new SelectionCapture(0, Integer.MAX_VALUE,
            lazy(LuaHighlight::bracketStringCapture),
            regex(STRING, "[^]]+")
        ),
        compound(
            token(STRING, "]"),
            token(0, Integer.MAX_VALUE, STRING, "="),
            token(STRING, "]")
        )
    );
  }

  static Capture constantLiteralCapture() {
    return forks(
        //STRING
        compound(
            token(STRING, "\""),
            new SelectionCapture(0, Integer.MAX_VALUE,
                regex(
                    CONTROL,
                    "(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])"
                ),
                regex(STRING, "[^\"]+")
            ),
            token(STRING, "\"")
        ),
        //CHARACTER
        compound(
            token(STRING, "'"),
            new SelectionCapture(
                regex(
                    CONTROL,
                    "(\\\\[0-7]{3})|(\\\\u[0-9a-fA-F]{4})|(\\\\[0abtnvfre\\\\\"'])"
                ),
                regex(STRING, "[^']")
            ),
            token(STRING, "'")
        ),
        //DOUBLE BRACKETS
        bracketStringCapture(),
        //NUMBER
        regex(NUMBER, "[\\d_]*(\\.[\\d_]+)?[fFdDlL]?"),
        //TODD
        //BOOLEAN
        regex(KEYWORD_VAR1, "true|false"),
        //NULL
        token(KEYWORD_VAR2, "nil")
    );
  }

  static Capture statementsCapture(int depth) {
    return compound(0, Integer.MAX_VALUE,
        statementCapture(depth)
    );
  }

  static Capture functionStatementBlockCapture() {
    return compound(
        lazy(() -> statementsCapture(0)),
        token(KEYWORD_BODY, "end")
    );
  }

  static Capture ifStatementBlockCapture() {
    return compound(
        lazy(() -> statementsCapture(0)),
        forks(
            token(KEYWORD_BODY, "end"),
            compound(
                token(KEYWORD_BODY, "else"),
                lazy(() -> statementsCapture(0)),
                token(KEYWORD_BODY, "end")
            )
        ),
        compound(
            token(KEYWORD_BODY, "elseif"),
            lazy(LuaHighlight::ifStatementBlockCapture)
        )
    );
  }

  static Capture statementBlockCapture() {
    return forks(
        compound(
            token(KEYWORD_BODY, "do"),
            lazy(() -> statementsCapture(0)),
            token(KEYWORD_BODY, "end")
        ),
        lazy(() -> statementsCapture(0))
    ).setOptional(true);
  }

  static Capture expressionCapture(int depth) {
    return makeJoin(
        compound(
            metaExpCapture(depth),
            regex(OPERATOR, "\\+\\+|--").setOptional(true)
        ),
        regex(OPERATOR, "(->|!=|==|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)|[=.+\\-*/%&|<>^]")
    );
  }

  public static PatternsHighlight create() {
    PatternsHighlight res = new PatternsHighlight("lua");

    res.tokensSplit = Pattern.compile("\\s+");
    res.rawTokenMatcher = Pattern.compile("(--\\[\\[(.|\\n)*?]]|--.*)");
    res.symbolMatcher = Pattern.compile(
        "(->|!=|==|<=|>=|&&|\\|\\||\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)" +
        "|(\\\\[0-7]{3})" +
        "|(\\\\u[0-9a-fA-F]{4})" +
        "|(\\\\[0abtnvfre\\\\\"'])" +
        "|[\\\\.+\\-*/%&|!<>~^=,;:(){}\"'\\[\\]]"
    );

    res//RAW CONTEXT
        .addRawContextPattern("line_comment", block(COMMENT,
            of(token(2, "-")),
            of(line(COMMENT))
        ))
        .addRawContextPattern("block_comment", block(COMMENT,
            of(token(COMMENT, "--"), token(COMMENT, "[[")),
            of(token(COMMENT, "]]"))
        ))

        .addPattern("keywords", serial(-100, token(
            KEYWORD,
            "local",
            "return", "require", "import",
            "do", "then", "while", "for", "in", "break", "end",
            "if", "else", "elseif", "goto", "until", "repeat",
            "and", "or", "not",
            "nil", "true", "false",
            "self", "function"
        )))
        .addPattern("statement", serial(-10, statementCapture(0)))
	      /*
        .addPattern("function", serial(functionCapture()))
        .addPattern("variable", serial(variableCapture()))
	      */;
    return res;
  }

}
