package blocks;
import variables.Variables;
import java.util.ArrayList;


public abstract class Block {
	String name;
	Block father;
	ArrayList<Variables> variables;

	abstract boolean checkValidity ();
	

}
