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

public class PrintStatement extends Statement {
    private final Expression expressionToPrint;
    public PrintStatement(Token tok, Expression expressionToPrint) {
        super(tok);
        this.expressionToPrint = expressionToPrint;
    }

    public Expression getExpressionToPrint() {
        return expressionToPrint;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        expressionToPrint.analyzeAndGetType(funcMap, varAndParamMap);
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
        Value value = expressionToPrint.evaluate(funcMap, varAndParamMap);
        System.out.print(value.getValue());
    }
}
