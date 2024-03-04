package splat.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import splat.parser.elements.Type;
import splat.parser.elements.declarations.Declaration;
import splat.parser.elements.declarations.FunctionDecl;
import splat.parser.elements.ProgramAST;
import splat.parser.elements.declarations.VariableDecl;
import splat.parser.elements.statements.Statement;

public class Executor {

	private ProgramAST progAST;
	
	private Map<String, FunctionDecl> funcMap = new HashMap<>();
	private Map<String, Value> progVarMap = new HashMap<>();
	
	public Executor(ProgramAST progAST) {
		this.progAST = progAST;
	}

	public void runProgram() throws ExecutionException {

		// This sets the maps that will be needed for executing function 
		// calls and storing the values of the program variables
		setMaps();
		
		try {
			
			// Go through and execute each of the statements
			for (Statement stmt : progAST.getStmts()) {
				stmt.execute(funcMap, progVarMap);
			}
			
		// We should never have to catch this exception here, since the
		// main program body cannot have returns
		} catch (ReturnFromCall ex) {
			System.out.println("Internal error!!! The main program body "
					+ "cannot have a return statement -- this should have "
					+ "been caught during semantic analysis!");
			
			throw new ExecutionException("Internal error -- fix your "
					+ "semantic analyzer!", -1, -1);
		}
	}
	
	private void setMaps() {
		for (Declaration decl : progAST.getDecls()) {

			String label = decl.getLabel();

			if (decl instanceof FunctionDecl) {
				FunctionDecl funcDecl = (FunctionDecl)decl;
				funcMap.put(label, funcDecl);

			} else if (decl instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl)decl;
				if (Objects.equals(varDecl.getType(), Type.Boolean()))
				{
					progVarMap.put(label, new BoolValue(false));
				}
				else if (Objects.equals(varDecl.getType(), Type.Integer()))
				{
					progVarMap.put(label, new IntValue(0));
				}
			}
		}
	}

}
