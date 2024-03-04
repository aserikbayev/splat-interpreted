package splat.parser.elements.declarations;

import splat.lexer.Token;
import splat.parser.elements.ASTElement;

public abstract class Declaration extends ASTElement {

	protected final String label;

	public Declaration(Token tok, String label) {
		super(tok);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
