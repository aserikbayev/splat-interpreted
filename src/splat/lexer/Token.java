package splat.lexer;

public class Token {
    private final int line;
    private final int column;
    private final String value;

    public Token(String value, int line, int column) {
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public Token(char value, int line, int column) {
        this.line = line;
        this.column = column;
        this.value = "" + value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "line=" + line +
                ", column=" + column +
                ", value='" + value + '\'' +
                '}';
    }
}
