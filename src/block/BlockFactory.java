package block;

import main.CommandFactory;
import variable.Variable;
import variable.VariableFactory;

import java.io.IOException;
import java.util.ArrayList;

public class BlockFactory {
	ArrayList<Variable> parameters;

	private static final String METHOD_DEC = "(void )" + Method.VALID_METHOD_NAME
			+ "\\(([\\w ,]*)\\) *{ *";
	private static final String CONDITIONAL = "(if|while)\\(([\\w \\|&]*\\)) *{ *";

	public Block createBlock(String type, String line, Block decBlock)  {


	}


	/**
	 * Creates a new conditional (if or while) according to the line and checks its validity
	 * @param line the line to check
	 * @throws IOException if line is invalid
	 */
	private static void createConditional (String line, Block decBlock) throws IOException{
		if (decBlock == null)
			throw new IOException("CANNOT DECLARE CONDITIONAL IN GLOBAL SCOPE");
		try{
			Block cond = new Conditional(m.group(2), decBlock);
			boolean valid = cond.checkValidity();
			if (!valid)
				throw new IOException("INVALID BOOLEAN CONDITION");
		} catch (Exception e){
			throw new IOException("INVALID IF WHILE DECLARATION");
		}
	}



	/**
	 * Reads a method declaration and creates Block objects according to it.
	 * Adds the blocks to the blocks arraylist.
	 *
	 * @param line a line representing a method declaration
	 * @throws IOException if line is invalid
	 */
	private static void createMethod(String line, Block defBlock) throws IOException {
		try{
			if (defBlock.isMethod())
				throw new IOException("DECLARED METHOD WITHIN METHOD");
			String methodName = m.group(2);
			for (Method method : methods)
				if (method.getName().equals(methodName))
					throw new IOException("METHOD OVERLOADING IS NOT SUPPORTED");

			ArrayList<Variable> params = new ArrayList<>();
			String[] varDec = m.group(3).split(",");
			for (String declaration : varDec)
				createVars(declaration, params);

			Block createdMethod = new Method(methodName, defBlock, params);
			defBlock = createdMethod;
			methods.add(createdMethod);
			if (!createdMethod.checkValidity())
				throw new IOException("INVALID METHOD DECLARATION");
		} catch (Exception e){
			throw new IOException("INVALID METHOD DECLARATION");
		}
	}



}
