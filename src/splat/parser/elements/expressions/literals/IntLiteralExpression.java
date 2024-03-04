package splat.parser.elements.expressions.literals;

import splat.executor.IntValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;

import java.util.Map;

public class IntLiteralExpression extends LiteralExpression {
    public IntLiteralExpression(Token tok, Object value) {
        super(tok, value);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return Type.Integer();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        return new IntValue(this.value);
    }
}
