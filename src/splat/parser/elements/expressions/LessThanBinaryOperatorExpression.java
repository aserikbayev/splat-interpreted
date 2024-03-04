package splat.parser.elements.expressions;

import splat.executor.BoolValue;
import splat.executor.ExecutionException;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.Objects;

public class LessThanBinaryOperatorExpression extends IntegerComparisonBinaryOperator {
    public LessThanBinaryOperatorExpression(Token tok, Expression arg1, Expression arg2) {
        super(tok, arg1, arg2);
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value arg1Value = arg1.evaluate(funcMap, varAndParamMap);
        Value arg2Value = arg2.evaluate(funcMap, varAndParamMap);

        Type arg1Type = arg1Value.getType();
        Type arg2Type = arg2Value.getType();

        if (!Objects.equals(arg1Type, Type.Integer()))
        {
            throw new ExecutionException("Expected type of arg1 was Integer. " + arg1Type + " was found instead", arg1);
        }

        if (!Objects.equals(arg2Type, Type.Integer()))
        {
            throw new ExecutionException("Expected type of arg1 was Integer. " + arg1Type + " was found instead", arg1);
        }

        return new BoolValue(((int) arg1Value.getValue() < (int)arg2Value.getValue()));
    }
}
