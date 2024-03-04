package splat.semanticanalyzer;

import java.util.*;
import java.util.stream.Collectors;

import splat.parser.elements.declarations.Declaration;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.statements.IfThenElseStatement;
import splat.parser.elements.statements.IfThenStatement;
import splat.parser.elements.statements.ReturnStatement;
import splat.parser.elements.statements.Statement;
import splat.parser.elements.Type;
import splat.parser.elements.declarations.VariableDecl;

public class SemanticAnalyzer {

	private final ProgramAST progAST;
	
	private final Map<String, FunctionDecl> funcMap = new HashMap<>();
	private final Map<String, Type> progVarMap = new HashMap<>();
	
	public SemanticAnalyzer(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void analyze() throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// for our program functions and variables 
		checkNoDuplicateProgLabels();
		
		// This sets the maps that will be needed later when we need to
		// typecheck variable references and function calls in the 
		// program body
		setProgVarAndFuncMaps();
		
		// Perform semantic analysis on the functions
		for (FunctionDecl funcDecl : funcMap.values()) {	
			analyzeFuncDecl(funcDecl);
		}
		
		// Perform semantic analysis on the program body
		for (Statement stmt : progAST.getStmts()) {
			stmt.analyze(funcMap, progVarMap);
		}
		
	}

	private void analyzeFuncDecl(FunctionDecl funcDecl) throws SemanticAnalysisException {
		
		// Checks to make sure we don't use the same labels more than once
		// among our function parameters, local variables, and function names
		checkNoDuplicateFuncLabels(funcDecl);
		
		// Get the types of the parameters and local variables
		Map<String, Type> varAndParamMap = getVarAndParamMap(funcDecl);

		boolean hasReturnStatements = false;

		// Perform semantic analysis on the function body
		for (Statement stmt : funcDecl.getStmts()) {
			stmt.analyze(funcMap, varAndParamMap);
		}



		if (!funcDecl.getReturnType().equals(Type.Void()))
		{
			checkLastStatementReturnsValue(funcDecl.getStmts());
		}
	}
	
	
	private Map<String, Type> getVarAndParamMap(FunctionDecl funcDecl) {

		Map<String, Type> varAndParamMap = new HashMap<>();
		varAndParamMap.put(funcDecl.getLabel(), funcDecl.getReturnType());

		// add return type of the function
		List<Declaration> allDeclarations = new ArrayList<>(funcDecl.getLocalVars());
		allDeclarations.addAll(funcDecl.getParams());

		// I'm not sure that it's correct that params and local vars are both of type VariableDecl
		// but let's see how it goes

		for (Declaration decl : allDeclarations) {

			String label = decl.getLabel();

			if (decl instanceof VariableDecl)
			{
				VariableDecl variableDecl = (VariableDecl) decl;
				varAndParamMap.put(label, variableDecl.getType());
			}
			else
			{
				throw new RuntimeException("wtf?");
			}
		}

		return varAndParamMap;
	}

	private void checkLastStatementReturnsValue(List<Statement> statements) throws SemanticAnalysisException {
		var lastStmt = statements.get(statements.size() - 1);

		if (lastStmt instanceof ReturnStatement)
		{
			var returnStatement = (ReturnStatement) lastStmt;
			if (returnStatement.getExpression() == null)
			{
				throw new SemanticAnalysisException("Must return a value from " + returnStatement.getParentLabel() + "()", lastStmt);
			}
		}
		else if (lastStmt instanceof IfThenElseStatement)
		{
			var ifThenElseStatement = (IfThenElseStatement) lastStmt;
			checkLastStatementReturnsValue(ifThenElseStatement.getThenStatements());
			checkLastStatementReturnsValue(ifThenElseStatement.getElseStatements());
		}
		else
		{
			throw new SemanticAnalysisException("Expected a return-terminating statement", lastStmt);
		}
	}

	private void checkNoDuplicateFuncLabels(FunctionDecl funcDecl) 
									throws SemanticAnalysisException {
		
		// Checking for no duplicate labels among its parameters, local variables, and
		// already existing function names.
        Set<String> labels = progAST.getDecls()
				.stream()
				.filter(x -> x instanceof FunctionDecl)
				.map(Declaration::getLabel)
				.collect(Collectors.toSet());

		List<Declaration> allDeclarations = new ArrayList<>(funcDecl.getLocalVars());
		allDeclarations.addAll(funcDecl.getParams());

		for (Declaration decl : allDeclarations)
		{
			String label = decl.getLabel();

			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in function " + funcDecl.getLabel(), decl);
			} else {
				labels.add(label);
			}
		}
	}
	
	private void checkNoDuplicateProgLabels() throws SemanticAnalysisException {
		
		Set<String> labels = new HashSet<String>();
		
 		for (Declaration decl : progAST.getDecls()) {
 			String label = decl.getLabel();
 			
			if (labels.contains(label)) {
				throw new SemanticAnalysisException("Cannot have duplicate label '"
						+ label + "' in program", decl);
			} else {
				labels.add(label);
			}
			
		}
	}
	
	private void setProgVarAndFuncMaps() {
		
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel();
			
			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);
				
			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				progVarMap.put(label, varDecl.getType());
			}
		}
	}
}
