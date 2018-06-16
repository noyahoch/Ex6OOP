package block;

import variable.Variable;

import java.util.ArrayList;

public class Global extends Block {
	private ArrayList<Variable> vars;


	public Global (ArrayList<Variable> vars){
		this.parent = null;
		this.vars = vars;
		this.isMethod = false; // todo better way to realize if it is method
	}

	@Override
	public boolean checkValidity() {
		for (Variable var : vars)
			if (!var.checkValidity()){
				return false;}
		return true;
	}
}
