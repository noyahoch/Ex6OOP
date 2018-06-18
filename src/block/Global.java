package block;

import variable.*;

import java.util.ArrayList;


/**
 * A class representing the global block of code.
 */
public class Global extends Block {

	public Global (ArrayList<Variable> vars) {
		this.parent = null;
		this.variables = vars;
		this.isMethod = false;
	}

	@Override
	public boolean checkValidity() {
		for (Variable var : variables) {
			if (!var.checkValidity()) {
				return false;
			}
		}
		return true;
	}

}
