/*

package block;

import main.CodeReader;
import variable.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class BlockFactory {

	private static final String METHOD_KEY = "void";
	private static final String IF_KEY = "if";
	private static final String WHILE_KEY = "while";
	private static Pattern blockPattern;
	private static Matcher blockMatcher;

	public static Block createBlock(String type, String line, CodeReader reader) throws IOException {
		if (type.contains(METHOD_KEY))
			blockPattern = Pattern.compile(METHOD_DEC);
		else
			blockPattern = Pattern.compile(CONDITIONAL);
		blockMatcher = blockPattern.matcher(line);
		if (!blockMatcher.matches())
			throw new IOException("ILLEGAL BLOCK CREATION");
		if (type.contains(METHOD_KEY))
			return createMethod(reader);
		return createConditional(reader);
	}




}

*/