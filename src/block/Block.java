package block;
import variable.Variable;
import java.util.ArrayList;


public abstract class Block {
	String name;
	Block father;
	ArrayList<Variable> variables;

	abstract boolean checkValidity ();
	

}
