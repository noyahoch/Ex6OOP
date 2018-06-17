package oop.ex6.block;

import oop.ex6.variable.Variable;

import java.util.ArrayList;

public class Global extends Block {


	public Global (ArrayList<Variable> vars){
		this.parent = null;
		this.variables = vars;
		this.isMethod = false; // todo better way to realize if it is method
	}

	@Override
	public boolean checkValidity() {
		for (Variable var : variables)
			if (!var.checkValidity())
				return false;
		return true;
	}
}
