package splat.parser.elements.declarations;

import splat.lexer.Token;
import splat.parser.elements.Type;
import splat.parser.elements.statements.Statement;

import java.util.List;

public class FunctionDecl extends Declaration {

	private final List<Declaration> params;
	private final List<Declaration> localVars;
	private final List<Statement> stmts;
	private final Type returnType;

	public FunctionDecl(Token tok, String label, Type returnType, List<Declaration> params, List<Declaration> localVars, List<Statement> stmts) {
		super(tok, label);
		this.params = params;
		this.localVars = localVars;
		this.stmts = stmts;
		this.returnType = returnType;
	}

	public List<Declaration> getParams() {
		return params;
	}

	public List<Declaration> getLocalVars() {
		return localVars;
	}

	public List<Statement> getStmts() {
		return stmts;
	}

	public Type getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		return "FunctionDecl{" +
				"params=" + params +
				", localVars=" + localVars +
				", stmts=" + stmts +
				", returnType='" + returnType + '\'' +
				", label='" + label + '\'' +
				'}';
	}
}
