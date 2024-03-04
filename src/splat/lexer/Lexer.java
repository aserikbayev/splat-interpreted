package splat.lexer;

import java.io.*;
import java.util.*;

public class Lexer {
	private final BufferedReader reader;
	private int line;
	private int column;
	private int currentChar;

	public Lexer(File file) throws IOException {

		this.reader = new BufferedReader(new FileReader(file));
		this.line = 1;
		this.column = 1;
	}

	public List<Token> tokenize() throws LexException, IOException {
		List<Token> tokens = new ArrayList<>();

		try {
			StringBuilder token = new StringBuilder();

			currentChar = reader.read();
			do
			{
				switch ((char) currentChar)
				{
					// "singular" token
					case '(':
					case ')':
					case ';':
					case ',':
					case '+':
					case '-':
					case '*':
					case '/':
					case '%':
						// write whatever was before
						writeTokenAndReset(token, tokens, line, column);

						// add current "singular" token
						token.append((char) currentChar);
						writeTokenAndReset(token, tokens, line, column);

						advanceReaderAndIncrementColumn();
						break;
					// whitespaces
					case ' ':
					case '\t':
					case '\n':
						// write whatever was before
						writeTokenAndReset(token, tokens, line, column);

						if (currentChar == '\n')
						{
							// newline logic (\r is ignored, see default case)
							column=0;
							line++;
						}

						advanceReaderAndIncrementColumn();
						break;
					// string literals
					case '"':
						// write whatever was before
						writeTokenAndReset(token, tokens, line, column);

						// read till closing
						handleStringLiteral(token, tokens);
						break;
					// single colon or walrus
					case ':':
						// write whatever was before
						writeTokenAndReset(token, tokens, line, column);

						// now add the colon
						token.append((char) currentChar);

						advanceReaderAndIncrementColumn();

						// walrus
						if (currentChar == '=')
						{
							token.append((char) currentChar);
							writeTokenAndReset(token, tokens, line, column);

							advanceReaderAndIncrementColumn();
						}
						else if (currentChar == ':') // invalid op
						{
							throw new LexException("Repeated colon", line, column);
						}
						else
						{
							// ':' followed by anything else -> just write the ':'
							writeTokenAndReset(token, tokens, line, column);
							// not advancing because already did
						}
						break;
					// relational operators. Single or composite
					case '<':
					case '>':
					case '=':
						// write whatever was before
						writeTokenAndReset(token, tokens, line, column);

						token.append((char) currentChar);
						char firstChar = (char) currentChar;

						advanceReaderAndIncrementColumn();

						char secondChar = (char) currentChar;

						if (secondChar != '=' && secondChar != '<' && secondChar != '>') // single
						{
							if (firstChar == '=') // single equal is not in spec
							{
								throw new LexException("Not in grammar", line, column);
							}
							else
							{
								writeTokenAndReset(token, tokens, line, column);
								continue;
							}
						}

						String[] validTwoCharOps = {"<=", ">=", "=="};

						token.append((char) currentChar); // add second char to token
						String currentOp = token.toString();
						boolean valid = false;
						for (String validTwoCharOp: validTwoCharOps)
						{
							if (validTwoCharOp.equals(currentOp))
							{
								valid = true;
								break;
							}
						}

						if (!valid)
						{
							throw new LexException("Not in grammar", line, column);
						}

						writeTokenAndReset(token, tokens, line, column);
						advanceReaderAndIncrementColumn();

						break;
					default:
						// alphanumeric + underscore
						if (isAlphaNumOrUnderscore(currentChar))
						{
							token.append((char) currentChar);
							advanceReaderAndIncrementColumn();
							continue;
						}

						if (currentChar == '\r')
						{
							currentChar = reader.read();
							// no column increment \r is usually coupled with \n and they together are considered as a single column
							continue;
						}

						if (currentChar == -1) // ignore EOF here
						{
							continue;
						}

						throw new LexException("Symbol not in grammar", line, column);
				}
			}
			while (currentChar != -1);
		} finally {
			reader.close();
		}

//		System.out.println(file.getAbsolutePath());
//		System.out.println(Arrays.toString(tokens.toArray()));
		return tokens;
	}

	private void advanceReaderAndIncrementColumn() throws IOException {
		currentChar = reader.read();
		column++;
	}

	private static boolean isAlphaNumOrUnderscore(int ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}

	private static void writeTokenAndReset(StringBuilder token, List<Token> tokens, int line, int column) {
		// write whatever was before
		if (token.length() > 0)
		{
			tokens.add(new Token(token.toString(), line, column - token.length()));
			token.delete(0, token.length());
		}
	}

	private void handleStringLiteral(StringBuilder token, List<Token> tokens) throws IOException, LexException {
		do {
			token.append((char) currentChar);
			advanceReaderAndIncrementColumn();
			if (currentChar == '\n')
			{
				throw new LexException("Multiline strings are not supported", line, column);
			}
		} while (currentChar != '"' && currentChar != -1);

		if (currentChar == -1)
		{
			throw new LexException("Unclosed quote", line, column);
		}
		else // ch = '"' - closing quote
		{
			token.append((char) currentChar);
			writeTokenAndReset(token, tokens, line, column);
		}
		advanceReaderAndIncrementColumn();
	}
}
