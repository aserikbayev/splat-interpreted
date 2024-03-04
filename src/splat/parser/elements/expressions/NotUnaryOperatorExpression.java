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

public class NotUnaryOperatorExpression extends UnaryOperatorExpression {
    public NotUnaryOperatorExpression(Token tok, Expression arg1) {
        super(tok, arg1);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type arg1Type = arg1.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Type, Type.Boolean()))
        {
            throw new SemanticAnalysisException("Expected type of arg1 was Boolean. " + arg1Type + " was found instead", arg1);
        }

        return Type.Boolean();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value arg1Value = arg1.evaluate(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Value.getType(), Type.Boolean()))
        {
            throw new ExecutionException("Expected type: " + Type.Boolean(), arg1);
        }

        return new BoolValue(!(boolean) arg1Value.getValue());
    }
}
