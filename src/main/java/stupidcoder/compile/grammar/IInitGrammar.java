package stupidcoder.compile.grammar;

public interface IInitGrammar {
    /**
     * 向语法分析器构造器中注册语法
     * @param registry 产生式注册器
     */
    void init(IProductionRegistry registry);
}
