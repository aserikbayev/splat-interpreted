package splat.parser.elements.statements;

import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.Declaration;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.declarations.VariableDecl;
import splat.parser.elements.expressions.Expression;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VoidFunctionCallStatement extends Statement {
    private final String label;
    private final List<Expression> args;

    public VoidFunctionCallStatement(Token tok, String label, List<Expression> args) {
        super(tok);
        this.label = label;
        this.args = args;
    }

    public List<Expression> getArguments() {
        return args;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        var functionDecl = funcMap.get(label);

        if (functionDecl == null)
        {
            throw new SemanticAnalysisException(label + "() is not declared", this);
        }

        if (!Objects.equals(functionDecl.getReturnType(), Type.Void()))
        {
            throw new SemanticAnalysisException("non-void-typed function " + label + "() is used called as a statement", this);
        }

        if (functionDecl.getParams().size() != args.size())
        {
            throw new SemanticAnalysisException(label + "() takes " + functionDecl.getParams().size() + " arguments but " + args.size() + " were given", this);
        }

        for (int i = 0; i < args.size(); i++)
        {
            var functionParameter = (VariableDecl) functionDecl.getParams().get(i);
            var functionArgument  = args.get(i);

            var paramType = functionParameter.getType();
            var argType = functionArgument.analyzeAndGetType(funcMap, varAndParamMap);
            if (!Objects.equals(paramType, argType))
            {
                throw new SemanticAnalysisException("Argument type " + argType + "does not match the function parameter type " + paramType, functionArgument);
            }
        }
    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {
        FunctionDecl functionDecl = funcMap.get(label);

        Map<String, Value> functionVarMap = new HashMap<>();
        SetMap(funcMap, varAndParamMap, functionVarMap);

        try {

            // Go through and execute each of the statements
            for (Statement stmt : functionDecl.getStmts()) {
                stmt.execute(funcMap, functionVarMap);
            }

            // We should never have to catch this exception here, since the
            // main program body cannot have returns
        } catch (ReturnFromCall ex) {
            System.err.println("Returned from " + label + "()");
        }
    }

    private void SetMap(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap, Map<String, Value> functionVarMap) throws ExecutionException {
        var functionDecl = funcMap.get(label);

        for (Declaration decl : functionDecl.getLocalVars()) {

            String label = decl.getLabel();

            if (decl instanceof VariableDecl) {
                //VariableDecl varDecl = (VariableDecl)decl;
                functionVarMap.put(label, null);
            }
        }

        for (int i = 0; i < args.size(); i++)
        {
            var functionParameter = (VariableDecl) functionDecl.getParams().get(i);
            var functionArgument  = args.get(i);

            var argValue = functionArgument.evaluate(funcMap, varAndParamMap);
            functionVarMap.put(functionParameter.getLabel(), argValue);
        }
    }
}
