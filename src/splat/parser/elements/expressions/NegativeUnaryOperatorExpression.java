package splat.parser.elements.expressions;

import splat.executor.ExecutionException;
import splat.executor.IntValue;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.Objects;

public class NegativeUnaryOperatorExpression extends UnaryOperatorExpression {
    public NegativeUnaryOperatorExpression(Token tok, Expression arg1) {
        super(tok, arg1);
    }


    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type arg1Type = arg1.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Type, Type.Integer()))
        {
            throw new SemanticAnalysisException("Expected type of arg was Integer. " + arg1Type + " was found instead", arg1);
        }

        return Type.Integer();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value arg1Value = arg1.evaluate(funcMap, varAndParamMap);

        Type arg1Type = arg1Value.getType();
        if (!Objects.equals(arg1Type, Type.Integer()))
        {
            throw new ExecutionException("Expected type of arg was Integer. " + arg1Type + " was found instead", arg1);
        }
        return new IntValue(-((int) arg1Value.getValue()));
    }
}
