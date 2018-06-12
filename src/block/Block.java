package block;
import variable.Variable;

import java.util.ArrayList;


public abstract class Block {
	Block parent;
	ArrayList<Variable> variables;
	boolean isMethod;

	abstract boolean checkValidity();

	public void setParent(Block parent){
		this.parent = parent;
	}

	public Block getParent(){
		return this.parent;
	}

	public ArrayList<Variable> getVariables(){
		return this.variables;
	}

	public boolean isMethod(){
		return this.isMethod;
	}

	/**
 	 * @param name a name of a variable
	 * @param block a block to start with checking.
	 * @return the value of variable with the corresponding name if exsists. null otherwise;
	 */
	static String valueOfVar(String name, Block block){
		Block currentBlock = block;
		while (currentBlock != null) {
			for (Variable var : currentBlock.getVariables())
				if (var.getName() == name)
					return var.getValue();
			currentBlock = currentBlock.getParent();
		}
		return null;
	 }
	

}
