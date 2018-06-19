package block;

import variable.Variable;
import java.util.ArrayList;

/**
 * An abstract class representing a block of code.
 */

public abstract class Block {

	Block parent;
	ArrayList<Variable> variables;
	boolean isMethod;

	/**Checks if the block is valid in sjava.
	 * @return true iff the block is valid.
	 */
	abstract public boolean checkValidity();

	/**
	 * @return the block's parent block
	 */
	public Block getParent(){
		return this.parent;
	}

	/**
	 * @return the list of the variables declared in the block
	 */
	public ArrayList<Variable> getVariables(){
		return this.variables;
	}

	/**
	 * @return true iff the block is a method
	 */
	public boolean isMethod(){
		return this.isMethod;
	}

	/**
	 * @param name a name of a variable
	 * @return the variable with the corresponding name if exists; null otherwise.
	 */
	public Variable findVar(String name){
		Block currentBlock = this;
		while (currentBlock != null) {
			for (Variable var : currentBlock.getVariables())
				if (var.getName().equals(name))
					return var;
			currentBlock = currentBlock.getParent();
		}
		return null;
	}

	/**
 	 * @param name a name of a variable
	 * @return the value of variable with the corresponding name if exists; null otherwise.
	 */
	public String valueOfVar(String name) {
		Variable var = findVar(name);
		if (var!=null)
			return var.getValue();
		return null;
	}
}
