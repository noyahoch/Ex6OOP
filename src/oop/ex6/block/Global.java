package oop.ex6.block;

import oop.ex6.variable.*;
import java.util.ArrayList;


/**
 * A class representing the global block of code.
 * Inherits from Block class
 */
public class Global extends Block {
	private ArrayList<Method> methods;

	/**
	 * Constructs a new global block
	 */
	public Global () {
		this.parent = null;
		this.variables = new ArrayList<>();
		this.isMethod = false;
		this.methods = new ArrayList<>();
	}

	@Override
	public boolean checkValidity() {
		for (Variable var : variables)
			if (!var.checkValidity())
				return false;
		return true;
	}

	public ArrayList<Variable> getVariables (){
		return this.variables;
	}

	public ArrayList<Method> getMethods() {
		return methods;
	}
}
