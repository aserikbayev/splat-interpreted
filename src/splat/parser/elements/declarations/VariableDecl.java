package splat.parser.elements.declarations;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private final splat.parser.elements.Type type;

	public VariableDecl(Token tok, String label, splat.parser.elements.Type type) {
		super(tok, label);
		this.type = type;
	}

	public splat.parser.elements.Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "VariableDecl{" +
				"label='" + label + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
