package block;

import variable.Variable;

import java.util.ArrayList;


public abstract class Block {
	Block parent;
	ArrayList<Variable> variables;
	public ArrayList<Block> blocks;

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

	public void addVariable(Variable var){
		this.variables.add(var);
	}


	public void addBlock(Block block){ this.blocks.add(block);} //todo Noya added


	/**
 	 * @param name a name of a variable
	 * @param block a block to start with checking.
	 * @return the value of variable with the corresponding name if exsists. null otherwise;
	 */
	public String valueOfVar(String name, Block block){ // todo is the block is necessary ?
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
