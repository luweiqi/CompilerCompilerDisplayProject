package com.stupidcoder.generated.tokens;

public class TokenWord implements IToken{
    private static final Map<String, Integer> keyWords;
    
    static {
        keyWords = new HashMap<>();
        init();
    }
    
    private int type;
    
    @Override
    public int type() {
        return type;
    }
    
    @Override
    public IToken fromLexeme(String lexeme) {
        type = keyWords.getOrDefault(lexeme, TokenTypes.ID);
        return this;
    }

    private static void init()() {
        keyWords.put("for", TokenTypes.KEYWORD_FOR);
        keyWords.put("while", TokenTypes.KEYWORD_WHILE);
    }
}