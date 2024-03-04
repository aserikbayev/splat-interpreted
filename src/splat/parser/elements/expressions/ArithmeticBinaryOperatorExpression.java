package splat.parser.elements.expressions;

import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.Objects;

public abstract class ArithmeticBinaryOperatorExpression extends BinaryOperatorExpression {
    public ArithmeticBinaryOperatorExpression(Token tok, Expression arg1, Expression arg2) {
        super(tok, arg1, arg2);
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type arg1Type = arg1.analyzeAndGetType(funcMap, varAndParamMap);
        Type arg2Type = arg2.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(arg1Type, Type.Integer()))
        {
            throw new SemanticAnalysisException("Expected type of arg1 was Integer. " + arg1Type + " was found instead", arg1);
        }

        if (!Objects.equals(arg2Type, Type.Integer()))
        {
            throw new SemanticAnalysisException("Expected type of arg1 was Integer. " + arg1Type + " was found instead", arg1);
        }

        return Type.Integer();
    }
}
