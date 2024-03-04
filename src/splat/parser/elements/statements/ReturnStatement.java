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

public class ReturnStatement extends Statement {
    private final Expression expression;
    private final String parentLabel;

    public ReturnStatement(Token tok, Expression expression, String parentLabel) {
        super(tok);
        this.expression = expression;
        this.parentLabel = parentLabel;
    }

    public ReturnStatement(Token tok, String parentLabel) {
        super(tok);
        expression = null;
        this.parentLabel = parentLabel;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        if (Objects.equals(parentLabel, "program"))
        {
            throw new SemanticAnalysisException("Return statements can only be used in the body statements of a function definition", this);
        }

        boolean isReturningValue = expression != null;
        Type parentFunctionReturnType = funcMap.get(parentLabel).getReturnType();
        boolean parentFunctionReturnsValue = !Objects.equals(parentFunctionReturnType, Type.Void());
        if (isReturningValue && parentFunctionReturnsValue)
        {
            Type returnedType = expression.analyzeAndGetType(funcMap, varAndParamMap);
            if (!returnedType.equals(parentFunctionReturnType))
            {
                throw new SemanticAnalysisException("Return type was " + returnedType + " when the " + "parentLabel()" + " was declared to return " + parentFunctionReturnType, expression);
            }
        }

        if (!isReturningValue && parentFunctionReturnsValue)
        {
            throw new SemanticAnalysisException("Must return a value from " + parentLabel + "()", this);
        }

        if (isReturningValue && !parentFunctionReturnsValue)
        {
            throw new SemanticAnalysisException("Cannot return a value from " + parentLabel + "()", this);
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        if (expression == null) // void-return
        {
            throw new ReturnFromCall(null);
        }
        Value value = expression.evaluate(funcMap, varAndParamMap);
        throw new ReturnFromCall(value);
    }

    public String getParentLabel() {
        return parentLabel;
    }
}
