package stupidcoder.compile.grammar;

import stupidcoder.common.Production;
import stupidcoder.common.symbol.Symbol;

public interface IPriorityAccess {
    int compare(Symbol s, Production p);
}
