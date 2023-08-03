package com.stupidcoder.cc.lex;

import com.stupidcoder.cc.util.input.ILexerInput;

public class NFARegexParser {
    private final ILexerInput input;
    private NFABuilder group;

    private NFARegexParser(ILexerInput input) {
        this.input = input;
    }

    public static NFABuilder parse(ILexerInput input) {
        return new NFARegexParser(input).parseExpr();
    }

    private NFABuilder parseExpr() {
        NFABuilder expr = new NFABuilder();
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case '(' -> group = parseExpr();
                case ')' -> {
                    if (input.available()) {
                        byte next = input.next();
                        //如果接下来是闭包符号，则交给循环处理。如果不是，则进行连接
                        if (next == '*' || next == '+' || next == '?') {
                            input.retract();
                        } else {
                            if (expr.isEmpty()) {
                                expr = group;
                            } else {
                                expr.and(group);
                            }
                        }
                    }
                    return expr;
                }
                case '|' -> expr.or(parseExpr());
                case '*' -> expr.and(group.star());
                case '+' -> expr.and(group.plus());
                case '?' -> expr.and(group.quest());
                default -> expr = parseSeq();
            }
        }
        return expr;
    }

    public NFABuilder parseSeq() {
        NFABuilder seq = new NFABuilder();
        NFABuilder lastAtom = null;
        LOOP:
        while (input.available()) {
            byte b = input.next();
            switch (b) {
                case '(':
                case ')':
                case '|':
                    input.retract();
                    break LOOP;
                case '*':
                    if (lastAtom != null) {
                        lastAtom.star();
                        seq.and(lastAtom);
                        lastAtom = null;
                    }
                    break;
                case '+':
                    if (lastAtom != null) {
                        lastAtom.plus();
                        seq.and(lastAtom);
                        lastAtom = null;
                    }
                    break;
                case '?':
                    if (lastAtom != null) {
                        lastAtom.quest();
                        seq.and(lastAtom);
                        lastAtom = null;
                    }
                    break;
                default:
                    input.retract();
                    if (lastAtom != null) {
                        seq.and(lastAtom);
                    }
                    lastAtom = parseAtom();
                    break;
            }
        }
        return seq;
    }

    private NFABuilder parseAtom() {
        if (input.available()) {
            byte b = input.next();
            ICharPredicate predicate = b == '[' ?
                    parseClazz() :
                    ICharPredicate.single(b);
            return new NFABuilder().andAtom(predicate);
        }
        return null;
    }

    private ICharPredicate parseClazz() {
        ICharPredicate result = null;
        int state = 0;
        byte b1 = 0;
        LOOP:
        while (input.available()) {
            byte b = input.next();
            switch (state) {
                case 0 -> {
                    if (b == ']') {
                        break LOOP;
                    }
                    b1 = b;
                    state = 1;
                }
                case 1 -> {
                    if (b == '-') {
                        state = 2;
                    } else if (b == ']') {
                        break LOOP;
                    } else {
                        result = ICharPredicate.or(result, ICharPredicate.single(b1));
                        state = 0;
                    }
                }
                case 2 -> {
                    if (b == ']') {
                        result = ICharPredicate.or(result, ICharPredicate.single('-'));
                        break LOOP;
                    } else {
                        result = ICharPredicate.or(
                                result,
                                ICharPredicate.ranged(b1, b));
                    }
                    state = 0;
                }
                default -> throw new IllegalStateException("???");
            }
        }
        return result;
    }
}