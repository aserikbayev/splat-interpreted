package splat.parser.elements.expressions;

import splat.lexer.Token;

public abstract class UnaryOperatorExpression extends Expression {
    protected final Expression arg1;
    public UnaryOperatorExpression(Token tok, Expression arg1) {
        super(tok);
        this.arg1 = arg1;
    }
}
