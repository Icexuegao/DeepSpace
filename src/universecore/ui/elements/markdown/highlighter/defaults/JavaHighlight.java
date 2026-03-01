package universecore.ui.elements.markdown.highlighter.defaults;

import universecore.ui.elements.markdown.highlighter.Capture;
import universecore.ui.elements.markdown.highlighter.PatternsHighlight;
import universecore.ui.elements.markdown.highlighter.SelectionCapture;

import java.util.regex.Pattern;

import static universecore.ui.elements.markdown.highlighter.Scope.Default.*;
import static universecore.ui.elements.markdown.highlighter.Scope.JavaScope.*;

public class JavaHighlight extends DefaultLangDSL{
  static Capture modifiersCapture(){
    return regex(0, Integer.MAX_VALUE, KEYWORD, "public|private|protected|static|final|abstract|synchronized|volatile|transient|native|strictfp|default|sealed|non-sealed");
  }

  static Capture annotationCapture(){
    return compound(0, Integer.MAX_VALUE,
        regex(ANNOTATION, "@\\w+"),
        compound(
            token("("),
            makeJoin(
                lazy(JavaHighlight::expressionCapture),
                token(SEPARATOR, ",")
            ),
            token(")")
        ).setOptional(true)
    );
  }

  static Capture typeCapture(){
    return makeJoin(
        compound(
            annotationCapture().setOptional(true),
            regex(TYPE, "((?!class)\\w)+"),
            compound(
                token("<"),
                makeJoin(
                    lazy(JavaHighlight::typeCapture).setOptional(true),
                    token(SEPARATOR, ",")
                ),
                token(">")
            ).setOptional(true),
            compound(0, Integer.MAX_VALUE,
                token("["),
                token("]")
            )
        ),
        token(TYPE, ".")
    );
  }

  static Capture typeCaptureNonArray(){
    return makeJoin(
        compound(
            regex(TYPE, "((?!class)\\w)+"),
            compound(
                token("<"),
                makeJoin(
                    lazy(JavaHighlight::typeCapture).setOptional(true),
                    token(SEPARATOR, ",")
                ),
                token(">")
            ).setOptional(true)
        ),
        token(TYPE, ".")
    );
  }

  static Capture typeArgCapture(){
    return compound(
        token("<"),
        compound(0, Integer.MAX_VALUE,
            regex(TYPE_ARG, "\\w+"),
            compound(
                token(KEYWORD, "extends", "super"),
                typeCapture()
            ).setOptional(true),
            token(SEPARATOR, ",").setOptional(true)
        ),
        token(">")
    );
  }

  static Capture statementBlockCapture() {
    return new SelectionCapture(
        compound(
            token("{"),
            lazy(JavaHighlight::statementsCapture),
            token("}")
        ),
        lazy(JavaHighlight::statementCapture)
    ).setOptional(true);
  }

  static Capture arrayLiteralCapture(){
    return compound(
        token("{"),
        makeJoin(
            lazy(JavaHighlight::expressionCapture),
            regex(SEPARATOR, ",")
        ),
        token("}")
    );
  }

  static Capture constantLiteralCapture(){
    return new SelectionCapture(
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
        //NUMBER
        // forks(
        //     compound(
        //         regex(NUMBER, "[\\d_]+"),
        //         forks(
        //             compound(
        //                 token(NUMBER, "."),
        //                 regex(NUMBER, "[\\d_]+([eE][+-]?[\\d_]+)?[fFdD]?")
        //             ),
        //             regex(NUMBER, "[eE][+-]?[\\d_]+[fFdD]?")
        //         )
        //     ),
        //     regex(NUMBER, "[\\d_]+[lL]?")
        // ),
        regex(NUMBER, "[\\d_]+(\\.[\\d_]+)?([eE][\\d_]+)?[fFdDlL]?"),
        //BOOLEAN
        regex(KEYWORD, "true|false"),
        //NULL
        token(KEYWORD, "null")
    );
  }

  static Capture metaExpCapture(){
    return compound(
        new SelectionCapture(
            arrayLiteralCapture(),
            //OPERATE
            compound(
                token("("),
                lazy(JavaHighlight::expressionCapture),
                token(")")
            ),
            //SWITCH EXPRESSION
            compound(
                token(KEYWORD, "switch"),
                token("("),
                lazy(JavaHighlight::expressionCapture),
                token(")"),
                token("{"),
                compound(0, Integer.MAX_VALUE,
                    token(KEYWORD, "case"),
                    makeJoin(
                        new SelectionCapture(
                            constantLiteralCapture(),
                            regex(VARIABLE, "\\w+")
                        ),
                        token(SEPARATOR, ",")
                    ),
                    token("->"),
                    lazy(JavaHighlight::statementBlockCapture),
                    token(SEPARATOR, ";").setOptional(true)
                ),
                token("}")
            ),
            //LAMBDA
            compound(
                new SelectionCapture(
                    compound(
                        token("("),
                        makeJoin(
                            regex(ARGUMENT, "\\w+"),
                            token(SEPARATOR, ",")
                        ).setOptional(true),
                        token(")")
                    ),
                    regex(ARGUMENT, "\\w+")
                ),
                token("->"),
                statementBlockCapture()
            ),
            //INVOKE
            compound(
                new SelectionCapture(
                    regex(KEYWORD, "this|super"),
                    regex(FUNCTION_INVOKE, "\\w+")
                ),
                compound(
                    token("("),
                    makeJoin(
                        lazy(JavaHighlight::expressionCapture).setOptional(true),
                        token(SEPARATOR, ",")
                    ),
                    token(")")
                )
            ),
            //NEW
            compound(
                token(KEYWORD, "new"),
                typeCaptureNonArray(),
                token("("),
                makeJoin(
                    lazy(JavaHighlight::expressionCapture).setOptional(true),
                    token(SEPARATOR, ",")
                ),
                token(")"),
                compound(
                    token("{"),
                    lazy(JavaHighlight::statementsCapture),
                    token("}")
                ).setOptional(true)
            ),
            //NEW ARRAY
            compound(
                token(KEYWORD, "new"),
                typeCaptureNonArray(),
                compound(1, Integer.MAX_VALUE,
                    token("["),
                    lazy(JavaHighlight::expressionCapture),
                    token("]")
                ),
                compound(0, Integer.MAX_VALUE,
                    token("["),
                    token("]")
                )
            ),
            //NEW ARRAY LITERAL
            compound(
                token(KEYWORD, "new"),
                typeCaptureNonArray(),
                compound(1, Integer.MAX_VALUE,
                    token("["),
                    token("]")
                ),
                arrayLiteralCapture()
            ),
            //READ ARRAY
            compound(
                regex(VARIABLE, "\\w+"),
                compound(1, Integer.MAX_VALUE,
                    token("["),
                    lazy(JavaHighlight::expressionCapture),
                    token("]")
                )
            ),
            //FUNC_REF
            compound(
                typeCaptureNonArray(),
                token(2, ":"),
                new SelectionCapture(
                    token(KEYWORD, "new"),
                    regex(FUNCTION_INVOKE, "\\w+")
                )
            ),
            //CLASS REF
            compound(
                typeCapture(),
                token("."),
                token(KEYWORD, "class")
            ),
            //SINGLE OPERATOR
            compound(
                regex(OPERATOR, "[!+\\-~]"),
                lazy(JavaHighlight::expressionCapture)
            ),
            constantLiteralCapture(),
            //THIS_SUPER
            regex(KEYWORD, "this|super"),
            //REF
            regex(VARIABLE, "\\w+")
        ),
        //INSTANCEOF
        compound(
            token(KEYWORD, "instanceof"),
            typeCapture(),
            regex(VARIABLE, "\\w+").setOptional(true)
        ).setOptional(true)
    );
  }

  static Capture expressionCapture(){
    return makeJoin(
        compound(
            metaExpCapture(),
            regex(OPERATOR, "\\+\\+|--").setOptional(true)
        ),
        regex(OPERATOR, "(->|!=|==|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)|[=.+\\-*/%&|<>^]")
    );
  }

  static Capture statementCapture(){
    return new SelectionCapture(
        //IF
        compound(
            token(KEYWORD, "if"),
            token("("),
            expressionCapture(),
            token(")"),
            statementBlockCapture(),
            compound(
                token(KEYWORD, "else"),
                statementBlockCapture()
            ).setOptional(true)
        ),
        //SWITCH
        compound(
            token(KEYWORD, "switch"),
            token("("),
            expressionCapture(),
            token(")"),
            token("{"),
            compound(0, Integer.MAX_VALUE,
                token(KEYWORD, "case"),
                makeJoin(
                    new SelectionCapture(
                        constantLiteralCapture(),
                        regex(VARIABLE, "\\w+")
                    ),
                    token(SEPARATOR, ",")
                ),
                token("->"),
                statementBlockCapture(),
                token(SEPARATOR, ";").setOptional(true)
            ),
            token("}")
        ),
        //WHILE
        compound(
            compound(
                regex("\\w+"),
                token(":")
            ).setOptional(true),
            token(KEYWORD, "while"),
            token("("),
            expressionCapture(),
            token(")"),
            statementBlockCapture()
        ),
        //DO_WHILE
        compound(
            compound(
                regex("\\w+"),
                token(":")
            ).setOptional(true),
            token(KEYWORD, "do"),
            statementBlockCapture(),
            token(KEYWORD, "while"),
            token("("),
            expressionCapture(),
            token(")")
        ),
        //FOR
        compound(
            compound(
                regex("\\w+"),
                token(":")
            ).setOptional(true),
            token(KEYWORD, "for"),
            token("("),
            compound(
                typeCapture(),
                makeJoin(
                    compound(
                        regex(VARIABLE, "\\w+"),
                        compound(
                            token(OPERATOR, "="),
                            expressionCapture()
                        ).setOptional(true)
                    ),
                    token(SEPARATOR, ",")
                )
            ).setOptional(true),
            token(SEPARATOR, ";"),
            expressionCapture().setOptional(true),
            token(SEPARATOR, ";"),
            makeJoin(
                expressionCapture(),
                token(SEPARATOR, ",")
            ),
            token(")"),
            statementBlockCapture()
        ),
        //FOR_EACH
        compound(
            compound(
                regex("\\w+"),
                token(":")
            ).setOptional(true),
            token(KEYWORD, "for"),
            token("("),
            typeCapture(),
            regex(VARIABLE, "\\w+"),
            token(":"),
            expressionCapture(),
            token(")"),
            statementBlockCapture()
        ),
        //TRY_CATCH
        compound(
            token(KEYWORD, "try"),
            token("{"),
            lazy(JavaHighlight::statementsCapture),
            token("}"),
            token("("),
            makeJoin(
                typeCaptureNonArray(),
                token(SEPARATOR, "|")
            ),
            token(")"),
            token("{"),
            lazy(JavaHighlight::statementsCapture),
            token("}")
        ),
        //LOCAL_VARIABLE
        compound(
            annotationCapture(),
            modifiersCapture(),
            typeCapture(),
            makeJoin(
                compound(
                    regex(VARIABLE, "\\w+"),
                    compound(
                        token(OPERATOR, "="),
                        expressionCapture()
                    ).setOptional(true)
                ),
                token(SEPARATOR, ",")
            ),
            token(SEPARATOR, ";")
        ),
        //BREAK-CONTINUE
        compound(
            token(KEYWORD, "break", "continue"),
            regex("\\w+").setOptional(true)
        ),
        //RETURN-YIELD
        compound(
            token(KEYWORD, "return", "yield"),
            expressionCapture().setOptional(true)
        ),
        //THROW
        compound(
            token(KEYWORD, "throw"),
            expressionCapture()
        ),
        //EXPRESSION
        expressionCapture(),
        //CODE_BLOCK
        compound(
            compound(
                regex("\\w+"),
                token(":")
            ).setOptional(true),
            token("{"),
            lazy(JavaHighlight::statementsCapture),
            token("}")
        )
    );
  }

  static Capture statementsCapture(){
    return compound(0, Integer.MAX_VALUE,
        statementCapture(),
        token(SEPARATOR, ";").setOptional(true)
    );
  }

  public static PatternsHighlight create(){
    PatternsHighlight res = new PatternsHighlight("java");

    res.tokensSplit = Pattern.compile("\\s+");
    res.rawTokenMatcher = Pattern.compile("//.*|/\\*(\\s|.)*?\\*/");
    res.symbolMatcher = Pattern.compile(
        "(->|!=|==|<=|>=|&&|\\|\\||\\+\\+|--|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|<<=|>>=|>>>=)" +
        "|([\\d_]+(\\.[\\d_]+)?([eE][\\d_]+)?[fFdDlL]?)" +
        "|(\\\\[0-7]{3})" +
        "|(\\\\u[0-9a-fA-F]{4})" +
        "|(\\\\[0abtnvfre\\\\\"'])" +
        "|[\\\\.+\\-*/%&|!<>~^=,;:(){}\"'\\[\\]]"
    );

    res//RAW CONTEXT
        .addRawContextPattern("line_comment", block(COMMENT,
            of(token(2, "/")),
            of(line(COMMENT))
        ))
        .addRawContextPattern("javadoc",
            block(DOCS,
                of(token("/"), token(2, "*")),
                of(token("*"), token(DOCS, "/"))
            ).addChildPattern("mark", serial(regex(DOC_MARK, "@\\w+")))
        )
        .addRawContextPattern("block_comment", block(COMMENT,
            of(token(COMMENT, "/"), token(COMMENT, "*")),
            of(token(COMMENT, "*"), token(COMMENT, "/"))
        ))

        //TOKENS CONTEXT
        .addPattern("keywords", serial(-100, token(
            KEYWORD,
            "public", "protected", "private",
            "static", "final", "synchronized", "volatile", "transient",
            "strictfp", "abstract", "native", "default",
            "class", "interface", "enum",
            "extends", "implements",
            "package", "import",
            "super", "this",
            "new", "return", "instanceof",
            "throw", "throws",
            "try", "catch", "finally",
            "do", "while", "for", "switch", "case", "break", "continue",
            "if", "else",
            "int", "long", "short", "byte", "char", "boolean", "float", "double", "void",
            "null", "true", "false"
        )))
        //STATEMENT
        .addPattern("statement", serial(-200, statementCapture()))

        //STRUCT
        .addPattern("package", serial(
            token(KEYWORD, "package"),
            typeCaptureNonArray(),
            token(SEPARATOR, ";")
        ))
        .addPattern("import", serial(
            token(KEYWORD, "import"),
            token(KEYWORD, "static").setOptional(true),
            typeCaptureNonArray(),
            compound(
                token(TYPE, "."),
                token("*")
            ).setOptional(true),
            token(SEPARATOR, ";")
        ))
        .addPattern("type_decl",
            block(
                of(
                    annotationCapture(),
                    modifiersCapture(),
                    token(KEYWORD, "class", "interface", "enum"),
                    compound(
                        regex(TYPE, "\\w+"),
                        typeArgCapture().setOptional(true)
                    ),
                    compound(
                        token(KEYWORD, "extends"),
                        typeCapture()
                    ).setOptional(true),
                    compound(
                        token(KEYWORD, "implements"),
                        makeJoin(typeCapture(), token(SEPARATOR, ","))
                    ).setOptional(true),
                    token("{")
                ),
                of(token("}"))
            ).addChildPattern("constructor", serial(
                annotationCapture(),
                modifiersCapture(),
                regex(CONSTRUCTOR, "\\w+"),
                compound(
                    token("("),
                    makeJoin(
                        compound(
                            annotationCapture(),
                            token(KEYWORD, "final").setOptional(true),
                            typeCapture(),
                            regex(ARGUMENT, "\\w+")
                        ),
                        token(SEPARATOR, ",")
                    ).setOptional(true),
                    compound(
                        token(SEPARATOR, ",").setOptional(true),
                        token(KEYWORD, "final").setOptional(true),
                        annotationCapture(),
                        typeCapture(),
                        token(3, ARGUMENT, "."),
                        regex(ARGUMENT, "\\w+")
                    ).setOptional(true),
                    token(")")
                ),
                compound(
                    token("{"),
                    statementsCapture(),
                    token("}")
                )
            )).addChildPattern("inner_class", reference(res))
        )
        .addPattern("annotation_arguments",
            block(ANNOTATION,
                of(regex("@\\w+"), token("(")),
                of(token(")"))
            ).addChildPattern("arg", serial(
                regex(ARGUMENT, "\\w+"),
                token(OPERATOR, "="),
                expressionCapture()
            )).addChildPattern("value", reference(res))
        )
        .addPattern("annotation", serial(
            regex(ANNOTATION, "@\\w+")
        ))
        .addPattern("function", serial(
            annotationCapture(),
            modifiersCapture(),
            typeArgCapture().setOptional(true),
            typeCapture(),
            regex(FUNCTION, "\\w+"),
            compound(
                token("("),
                makeJoin(
                    compound(
                        annotationCapture(),
                        token(KEYWORD, "final").setOptional(true),
                        typeCapture(),
                        regex(ARGUMENT, "\\w+")
                    ),
                    token(SEPARATOR, ",")
                ).setOptional(true),
                compound(
                    token(SEPARATOR, ",").setOptional(true),
                    token(KEYWORD, "final").setOptional(true),
                    annotationCapture(),
                    typeCapture(),
                    token(3, ARGUMENT, "."),
                    regex(ARGUMENT, "\\w+")
                ).setOptional(true),
                token(")")
            ),
            compound(
                token(KEYWORD, "throws"),
                makeJoin(
                    typeCaptureNonArray(),
                    token(SEPARATOR, ",")
                )
            ).setOptional(true),
            compound(
                token("{"),
                statementsCapture(),
                token("}")
            ).setOptional(true)
        ))
        .addPattern("variable", serial(
            annotationCapture(),
            modifiersCapture(),
            typeCapture(),
            makeJoin(
                compound(
                    regex(FIELD, "\\w+"),
                    compound(
                        token(OPERATOR, "="),
                        expressionCapture()
                    ).setOptional(true)
                ),
                token(SEPARATOR, ",")
            ),
            token(SEPARATOR, ";")
        ));

    return res;
  }
}
