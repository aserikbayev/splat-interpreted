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

public class EqualBinaryOperatorExpression extends BinaryOperatorExpression {
    public EqualBinaryOperatorExpression(Token tok, Expression arg1, Expression arg2) {
        super(tok, arg1, arg2);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type arg1Type = arg1.analyzeAndGetType(funcMap, varAndParamMap);
        Type arg2Type = arg2.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Type, arg2Type))
        {
            throw new SemanticAnalysisException("Argument types do not match. Arg1 type: " + arg1Type + ". Arg2 type: " + arg2Type, this);
        }

        return Type.Boolean();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value arg1Value = arg1.evaluate(funcMap, varAndParamMap);
        Value arg2Value = arg2.evaluate(funcMap, varAndParamMap);

        Type arg1Type = arg1Value.getType();
        Type arg2Type = arg2Value.getType();

        if (!Objects.equals(arg1Type, arg2Type))
        {
            throw new ExecutionException("Argument types do not match. Arg1 type: " + arg1Type + ". Arg2 type: " + arg2Type, this);
        }

        return new BoolValue(arg1Value.getValue() == arg2Value.getValue());
    }
}
