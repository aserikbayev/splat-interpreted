package splat.parser.elements.expressions.literals;

import splat.lexer.Token;
import splat.parser.elements.expressions.Expression;

public abstract class LiteralExpression extends Expression {
    protected final Object value;

    public LiteralExpression(Token tok, Object value) {
        super(tok);
        this.value = value;
    }
}
