package block;
import variable.Variable;
import variables.Variable;

import java.util.ArrayList;


public abstract class Block {
	String name;
	Block parent;
	ArrayList<Variable> variables;
	boolean isMethod;

	abstract boolean checkValidity ();
	public Block getParent(){
		return this.parent;
	}

	public boolean isMethod(){
		return this.isMethod;
	}
	

}
