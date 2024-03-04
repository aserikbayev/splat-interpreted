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

public class WhileLoopStatement extends Statement {
    private final Expression expression;
    private final List<Statement> statements;

    public WhileLoopStatement(Token tok, Expression expr, List<Statement> loopBody) {
        super(tok);
        this.expression = expr;
        this.statements = loopBody;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        var expressionType = expression.analyzeAndGetType(funcMap, varAndParamMap);
        if (!Objects.equals(expressionType, Type.Boolean()))
        {
            throw new SemanticAnalysisException("Type of expression must be Boolean", expression);
        }

        for (Statement x : statements) {
            x.analyze(funcMap, varAndParamMap);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
        while ((boolean) expression.evaluate(funcMap, varAndParamMap).getValue())
        {
            for (var stmt: statements) {
                stmt.execute(funcMap, varAndParamMap);
            }
        }
    }
}
