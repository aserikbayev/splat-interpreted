package splat.parser.elements.statements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.expressions.Expression;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;
import java.util.Objects;

public class AssignmentStatement extends Statement {
    private final String lhs;
    private final Expression rhs;
    public AssignmentStatement(Token tok, String label, Expression expression) {
        super(tok);
        lhs = label;
        rhs = expression;
    }

    public String getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        var lhsType = varAndParamMap.get(lhs);

        if (lhsType == null)
        {
            throw new SemanticAnalysisException("Cannot assign to undeclared variable " + lhs, this);
        }

        var rhsType = rhs.analyzeAndGetType(funcMap, varAndParamMap);

        if (!Objects.equals(lhsType, rhsType))
        {
            throw new SemanticAnalysisException("Cannot assign a value of type " + rhsType + " to " + lhs + " of type " + lhsType, this);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {
        Value rhsValue = rhs.evaluate(funcMap, varAndParamMap);
        varAndParamMap.replace(lhs, rhsValue);
    }
}
