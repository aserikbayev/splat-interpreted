package splat.parser.elements.expressions;

import splat.lexer.Token;

public abstract class BinaryOperatorExpression extends Expression {
    protected final Expression arg1;
    protected final Expression arg2;

    public BinaryOperatorExpression(Token tok, Expression arg1, Expression arg2) {
        super(tok);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }
}
