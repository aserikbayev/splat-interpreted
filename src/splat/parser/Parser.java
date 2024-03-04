package splat.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import splat.lexer.Token;
import splat.parser.elements.*;
import splat.parser.elements.expressions.*;
import splat.parser.elements.declarations.Declaration;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.declarations.VariableDecl;
import splat.parser.elements.expressions.literals.*;
import splat.parser.elements.statements.*;

public class Parser {

	private final List<Token> tokens;
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Compares the next token to an expected value, and throws
	 * an exception if they don't match.  This removes the front-most
	 * (next) token  
	 * 
	 * @param expected value of the next token
	 * @throws ParseException if the actual token doesn't match what 
	 * 			was expected
	 */
	private void checkNext(String expected) throws ParseException {

		Token tok = tokens.remove(0);
		
		if (!tok.getValue().equals(expected)) {
			throw new ParseException("Expected '"+ expected + "', got '" 
					+ tok.getValue()+ "'.", tok);
		}
	}
	
	/**
	 * Returns a boolean indicating whether or not the next token matches
	 * the expected String value.  This does not remove the token from the
	 * token list.
	 * 
	 * @param expected value of the next token
	 * @return true iff the token value matches the expected string
	 */
	private boolean peekNext(String expected) {
		return tokens.get(0).getValue().equals(expected);
	}
	
	/**
	 * Returns a boolean indicating whether or not the token directly after
	 * the front most token matches the expected String value.  This does 
	 * not remove any tokens from the token list.
	 * 
	 * @param expected value of the token directly after the next token
	 * @return true iff the value matches the expected string
	 */
	private boolean peekTwoAhead(String expected) {
		return tokens.get(1).getValue().equals(expected);
	}
	
	
	/*
	 *  <program> ::= program <decls> begin <stmts> end ;
	 */
	public ProgramAST parse() throws ParseException {
		
		try {
			// Needed for 'program' token position info
			Token startTok = tokens.get(0);
			
			checkNext("program");
			
			List<Declaration> decls = parseDecls();
			
			checkNext("begin");
			
			List<Statement> stmts = parseStmts();
			
			checkNext("end");
			checkNext(";");
	
			return new ProgramAST(decls, stmts, startTok);
			
		// This might happen if we do a tokens.get(), and nothing is there!
		} catch (IndexOutOfBoundsException ex) {
			
			throw new ParseException("Unexpectedly reached the end of file.", -1, -1);
		}
	}
	
	/*
	 *  <decls> ::= (  <decl>  )*
	 */
	private List<Declaration> parseDecls() throws ParseException {
		
		List<Declaration> decls = new ArrayList<>();
		
		while (!peekNext("begin")) {
			Declaration decl = parseDecl();
			decls.add(decl);
		}
		
		return decls;
	}
	
	/*
	 * <decl> ::= <var-decl> | <func-decl>
	 */
	private Declaration parseDecl() throws ParseException {

		if (peekTwoAhead(":")) {
			var varDecl =  parseVarDecl();
			checkNext(";");
			return varDecl;
		} else if (peekTwoAhead("(")) {
			return parseFuncDecl();
		} else {
			Token tok = tokens.get(0);
			throw new ParseException("Declaration expected", tok);
		}
	}
	
	/*
	 * <func-decl> ::= <label> ( <params> ) : <ret-type> is 
	 * 						<loc-var-decls> begin <stmts> end ;
	 */
	private FunctionDecl parseFuncDecl() throws ParseException {
		Token functionName = tokens.remove(0);
		if(!isLabel(functionName.getValue()))
		{
			throw new ParseException("Label was expected. But " + functionName.getValue() + " was found instead", functionName);
		}

		List<Declaration> params;
		List<Declaration> localVariables = new ArrayList<>();
		Token functionReturnType;
		List<Statement> stmts;

		params = parseParams();

		checkNext(":");

		functionReturnType = tokens.remove(0);

		checkNext("is");

		// by the lang spec there are no local functions, only local variables
		while (!peekNext("begin")) {
			Declaration decl = parseVarDecl();
			checkNext(";");
			localVariables.add(decl);
		}

		checkNext("begin");

		stmts = parseStmts(functionName.getValue());

		checkNext("end");
		checkNext(";");

        return new FunctionDecl(tokens.get(0), functionName.getValue(), parseFuncReturnType(functionReturnType), params, localVariables, stmts);
	}

	private Type parseFuncReturnType(Token functionReturnType) throws ParseException {
		switch (functionReturnType.getValue())
		{
			case Type.String:
				return Type.String();
			case Type.Boolean:
				return Type.Boolean();
			case Type.Integer:
				return Type.Integer();
			case Type.Void:
				return Type.Void();
			default:
				throw new ParseException("Invalid function return type: " + functionReturnType.getValue(), functionReturnType);
		}
	}

	private Type parseType(Token token) throws ParseException {
		switch (token.getValue())
		{
			case Type.String:
				return Type.String();
			case Type.Boolean:
				return Type.Boolean();
			case Type.Integer:
				return Type.Integer();
			default:
				throw new ParseException("Invalid type: " + token.getValue(), token);
		}
	}

	private List<Declaration> parseParams() throws ParseException {
		checkNext("(");
		List<Declaration> params = new ArrayList<>();
		while (!peekNext(")"))
		{
			params.add(parseVarDecl());
			if (!peekNext(","))
			{
				break;
			}
			checkNext(",");
		}
		checkNext(")");
		return params;
	}

	/*
	 * <var-decl> ::= <label> : <type> ;
	 */
	private VariableDecl parseVarDecl() throws ParseException {
		Token variableName = tokens.remove(0);

		if (!isLabel(variableName.getValue()))
		{
			throw new ParseException("Label was expected. But " + variableName.getValue() + " was found instead", variableName);
		}

		checkNext(":");
		Token variableType = tokens.remove(0);
        return new VariableDecl(tokens.get(0), variableName.getValue(), parseType(variableType));
	}

	private List<Statement> parseStmts() throws ParseException {
		return parseStmts("program");
	}

	/*
	 * <stmts> ::= (  <stmt>  )*
	 */
	private List<Statement> parseStmts(String parentLabel) throws ParseException {
		List<Statement> statements = new ArrayList<>();
		while (!peekNext("end") && !peekNext("else"))
		{
			if (peekNext("while"))
			{
				var tok = tokens.remove(0);
				Expression expr = parseExpression();
				checkNext("do");
				List<Statement> loopBody = parseStmts(parentLabel);
				checkNext("end");
				checkNext("while");
				checkNext(";");
				statements.add(new WhileLoopStatement(tok, expr, loopBody));
			}
			else if (peekNext("if"))
			{
				var tok = tokens.remove(0);
				Expression expr = parseExpression();
				checkNext("then");
				List<Statement> thenStatements = parseStmts(parentLabel);
				List<Statement> elseStatements = null;
				if (peekNext("else"))
				{
					checkNext("else");
					elseStatements = parseStmts(parentLabel);
				}
				checkNext("end");
				checkNext("if");
				checkNext(";");
				if (elseStatements == null)
				{
					statements.add(new IfThenStatement(tok, expr, thenStatements));
				}
				else
				{
					statements.add(new IfThenElseStatement(tok, expr, thenStatements, elseStatements));
				}
			}
			else if (peekNext("print"))
			{
				var tok = tokens.remove(0);
				Expression expr = parseExpression();
				checkNext(";");
				var printStatement = new PrintStatement(tok, expr);
				statements.add(printStatement);
			}
			else if (peekNext("print_line"))
			{
				var tok = tokens.remove(0);
				checkNext(";");
				Expression expr = new StringLiteralExpression(new Token("\n", tok.getLine(), tok.getColumn()), "\n");
				var printStatement = new PrintStatement(tok, expr);
				statements.add(printStatement);
			}
			else if (peekNext("return"))
			{
				var returnToken = tokens.remove(0);
				if (peekNext(";"))
				{
					statements.add(new ReturnStatement(returnToken, parentLabel));
				}
				else
				{
					Expression expression = parseExpression();
					statements.add(new ReturnStatement(returnToken, expression, parentLabel));
				}
				checkNext(";");
			}
			else if (peekTwoAhead("("))
			{
				var functionName = tokens.remove(0);

				if (!isLabel(functionName.getValue()))
				{
					throw new ParseException("Label was expected. But " + functionName.getValue() + " was found instead", functionName);
				}

				// todo: extract method
				checkNext("(");

				List<Expression> args = new ArrayList<>();
				while(!peekNext(")"))
				{
					args.add(parseExpression());
					if (!peekNext(","))
					{
						break;
					}
					checkNext(",");
				}

				checkNext(")");
				// end todo: extract method

				checkNext(";");

				var statement = new VoidFunctionCallStatement(functionName, functionName.getValue(), args);
				statements.add(statement);
			}
			else
			{
				var label = tokens.remove(0);

				if (!isLabel(label.getValue()))
				{
					throw new ParseException("Label was expected. But " + label.getValue() + " was found instead", label);
				}

				if (peekNext(":="))
				{
					checkNext(":=");
					Expression expression = parseExpression();
					checkNext(";");
					statements.add(new AssignmentStatement(label, label.getValue(), expression));
				}
				else
				{
					throw new ParseException("Unknown syntax", tokens.remove(0));
				}
			}
		}
		return statements;
	}

	private Expression parseExpression() throws ParseException {

//		if (peekNext("(") && peekTwoAhead("("))
//		{
//			var openParen = tokens.remove(0);
//			return new NestedExpression(openParen, parseExpression());
//		}

		if (peekNext("("))
		{
			var expressionStart = tokens.remove(0);
			// check for unary op
			// if unary parse unary
			if (peekNext("not") || peekNext("-"))
			{
				var unaryOpTok = tokens.remove(0);
				var argExpression = parseExpression();
				var unaryOpExpression = unaryOpTok.getValue().equals("not")
						? new NotUnaryOperatorExpression(unaryOpTok, argExpression)
						: new NegativeUnaryOperatorExpression(unaryOpTok, argExpression);
				checkNext(")");
				return unaryOpExpression;
			}
			else
			{
				Expression arg1Expression = parseExpression();
				var binaryOpToken = tokens.remove(0);
				Expression arg2Expression = parseExpression();
				checkNext(")");
                return createBinaryOperatorExpression(binaryOpToken, arg1Expression, arg2Expression);
			}
		}
		else
		{
			// non void call
			if (peekTwoAhead("("))
			{
				var label = tokens.remove(0);

				if (!isLabel(label.getValue()))
				{
					throw new ParseException("Label was expected. But " + label.getValue() + " was found instead", label);
				}

				// todo: extract method
				checkNext("(");

				List<Expression> args = new ArrayList<>();
				while(!peekNext(")"))
				{
					args.add(parseExpression());
					if (!peekNext(","))
					{
						break;
					}
					checkNext(",");
				}

				checkNext(")");
				// end todo: extract method

				return new NonVoidFunctionCallExpression(label, label.getValue(), args);
			}

			var literalOrLabel = tokens.remove(0);

			try
			{
				// Works for now, I guess
				if (Integer.parseUnsignedInt(literalOrLabel.getValue()) >= 0)
				{
					return new IntLiteralExpression(literalOrLabel, Integer.parseUnsignedInt(literalOrLabel.getValue()));
				}
			} catch (NumberFormatException ignored)
			{

			}

			if ("true".equals(literalOrLabel.getValue()) || "false".equals(literalOrLabel.getValue()))
			{
				return new BoolLiteralExpression(literalOrLabel, Boolean.parseBoolean(literalOrLabel.getValue()));
			}
			if (literalOrLabel.getValue().startsWith("\""))
			{
				// strip the quotes when saving the value
				return new StringLiteralExpression(literalOrLabel, literalOrLabel.getValue().substring(1, literalOrLabel.getValue().length()-1));
			}


			if (isLabel(literalOrLabel.getValue()))
			{
				return new LabelExpression(literalOrLabel, literalOrLabel.getValue());
			}

			throw new ParseException("Literal or label was expected", literalOrLabel);
		}
	}

	private boolean isLabel(String s) {

		List<String> keywords = Arrays.asList("Boolean", "String", "Integer", "and", "or", "not", "while", "if", "then", "else", "end", "program", "begin", "is", "return", "do", "print", "print_line", "true", "false", "void");

		if (keywords.contains(s))
		{
			return false;
		}

		boolean firstIsDigit = s.charAt(0) >= '0' && s.charAt(0) <= '0';

		return !firstIsDigit && s.chars().allMatch(Parser::isAlphaNumOrUnderscore);
	}

	private static boolean isAlphaNumOrUnderscore(int ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}

	private Expression createBinaryOperatorExpression(Token token, Expression arg1Expression, Expression arg2Expression) throws ParseException {
		switch (token.getValue())
		{
			case "+":
				return new PlusBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "-":
				return new MinusBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "*":
				return new MultiplyBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "/":
				return new DivideBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "%":
				return new ModuloBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case ">":
				return new GreaterThanBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "<":
				return new LessThanBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case ">=":
				return new GreaterThanEqualBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "<=":
				return new LessThanEqualBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "==":
				return new EqualBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "and":
				return new AndBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			case "or":
				return new OrBinaryOperatorExpression(token, arg1Expression, arg2Expression);
			default:
				throw new ParseException("Invalid syntax. Expected a known valid operator but received " + token.getValue() + ".", token);
		}
	}
}
