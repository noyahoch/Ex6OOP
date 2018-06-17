package oop.ex6.block;

import oop.ex6.variable.*;

import java.util.ArrayList;


public abstract class Block {
	Block parent;
	ArrayList<Variable> variables;
	boolean isMethod;

	abstract public boolean checkValidity();

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
