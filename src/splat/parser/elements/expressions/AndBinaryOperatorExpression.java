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

public class AndBinaryOperatorExpression extends BinaryOperatorExpression {
    public AndBinaryOperatorExpression(Token tok, Expression arg1, Expression arg2) {
        super(tok, arg1, arg2);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type arg1Type = arg1.analyzeAndGetType(funcMap, varAndParamMap);
        Type arg2Type = arg2.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Type, Type.Boolean()))
        {
            throw new SemanticAnalysisException("Expected type of arg1 was Boolean. " + arg1Type + " was found instead", arg1);
        }

        if (!Objects.equals(arg2Type, Type.Boolean()))
        {
            throw new SemanticAnalysisException("Expected type of arg1 was Boolean. " + arg1Type + " was found instead", arg1);
        }

        return Type.Boolean();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value arg1Value = arg1.evaluate(funcMap, varAndParamMap);
        Value arg2Value = arg2.evaluate(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Value.getType(), Type.Boolean()))
        {
            throw new ExecutionException("Expected type: " + Type.Boolean(), arg1);
        }

        if (!Objects.equals(arg2Value.getType(), Type.Boolean()))
        {
            throw new ExecutionException("Expected type: " + Type.Boolean(), arg2);
        }

        return new BoolValue((boolean) arg1Value.getValue() && (boolean) arg2Value.getValue());
    }
}
