package splat.parser.elements.statements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.expressions.Expression;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IfThenElseStatement extends Statement {

    private final Expression expression;
    private final List<Statement> thenStatements;
    private final List<Statement> elseStatements;

    public IfThenElseStatement(Token tok, Expression expr, List<Statement> thenStatements, List<Statement> elseStatements) {
        super(tok);
        this.expression = expr;
        this.thenStatements = thenStatements;
        this.elseStatements = elseStatements;
    }

    public Expression getExpression() {
        return expression;
    }

    public List<Statement> getThenStatements() {
        return thenStatements;
    }

    public List<Statement> getElseStatements() {
        return elseStatements;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        var expressionType = expression.analyzeAndGetType(funcMap, varAndParamMap);
        if (!Objects.equals(expressionType, Type.Boolean()))
        {
            throw new SemanticAnalysisException("Type of expression must be Boolean", expression);
        }

        for (Statement x : thenStatements) {
            x.analyze(funcMap, varAndParamMap);
        }

        for (Statement x : elseStatements) {
            x.analyze(funcMap, varAndParamMap);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
        Value value = expression.evaluate(funcMap, varAndParamMap);
        if ((boolean) value.getValue())
        {
            for (var stmt: thenStatements) {
                stmt.execute(funcMap, varAndParamMap);
            }
        }
        else
        {
            for (var stmt: elseStatements) {
                stmt.execute(funcMap, varAndParamMap);
            }
        }
    }
}
