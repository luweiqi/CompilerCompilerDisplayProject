package stupidcoder.compile.lex;

public interface IDfaSetter {
    void setAccepted(int i, String token);
    void setGoTo(int start, int input, int target);
    void setStartState(int i);
    void setDfaStatesCount(int count);
}
