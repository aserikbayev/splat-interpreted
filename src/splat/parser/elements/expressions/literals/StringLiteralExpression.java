package splat.parser.elements.expressions.literals;

import splat.executor.StringValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;

import java.util.Map;

public class StringLiteralExpression extends LiteralExpression {
    public StringLiteralExpression(Token tok, Object value) {
        super(tok, value);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return Type.String();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new StringValue(this.value);
    }
}
