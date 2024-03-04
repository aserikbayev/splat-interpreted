package splat.parser.elements.expressions.literals;

import splat.executor.BoolValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;

import java.util.Map;

public class BoolLiteralExpression extends LiteralExpression {
    public BoolLiteralExpression(Token tok, Object value) {
        super(tok, value);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return Type.Boolean();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new BoolValue(this.value);
    }
}
