package block;

import main.CodeReader;
import variable.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class SecondBlockFactory {
    private static final String METHOD_DEC = "(void )" + Method.VALID_METHOD_NAME
            + "\\(([\\w ,]*)\\) *\\{ *";
    private static final String CONDITIONAL = "(if|while)\\(([\\w \\|&]*\\)) *\\{ *";
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

    /**
     * Creates a new conditional (if or while) according to the line and checks its validity
     *
     * @throws IOException if line is invalid
     */
    private static Block createConditional(CodeReader reader) throws IOException {
        Block decBlock = reader.getCurrentBlock();
        if (decBlock == null)
            throw new IOException("CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE");
        try {
            Block cond = new Conditional(blockMatcher.group(2), decBlock);
            boolean valid = cond.checkValidity();
            if (!valid)
                throw new IOException("INVALID BOOLEAN CONDITION");
            return cond;
        } catch (Exception e) {
            throw new IOException("INVALID IF WHILE DECLARATION");
        }
    }


    /**
     * Reads a method declaration and creates Block objects according to it.
     * Adds the blocks to the blocks arraylist.
     *
     * @throws IOException if line is invalid
     */
    private static Block createMethod(CodeReader reader) throws IOException {
        Block decBlock = reader.getCurrentBlock();
        try {
            if (decBlock.isMethod())
                throw new IOException("DECLARED METHOD WITHIN METHOD");
            String methodName = blockMatcher.group(2);
            for (Method method : reader.getMethods())
                if (method.getName().equals(methodName))
                    throw new IOException("METHOD OVERLOADING IS NOT SUPPORTED");

            ArrayList<Variable> params = new ArrayList<>();
            String[] varDec = blockMatcher.group(3).split(",");
            for (String declaration : varDec)
                reader.createVars(declaration, params);

            Block createdMethod = new Method(methodName, decBlock, params);
            if (!createdMethod.checkValidity())
                throw new IOException("INVALID METHOD DECLARATION");
            return createdMethod;
        } catch (Exception e) {
            throw new IOException("INVALID METHOD DECLARATION");
        }
    }

}