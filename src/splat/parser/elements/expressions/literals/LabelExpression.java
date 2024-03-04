package splat.parser.elements.expressions.literals;

import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.expressions.Expression;

import java.util.Map;

public class LabelExpression extends Expression {
    private final String label;

    public LabelExpression(Token tok, String value) {
        super(tok);
        this.label = value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) {
        return varAndParamMap.get(label);
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        var value = varAndParamMap.get(this.label);
        if (value == null)
        {
            throw new ExecutionException("Cannot use uninitialized label " + this.label, this);
        }
        return value;
    }
}
