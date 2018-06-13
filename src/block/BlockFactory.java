package block;

import main.CommandFactory;
import variable.Variable;
import variable.VariableFactory;

import java.util.ArrayList;

public class BlockFactory {
	ArrayList<Variable> parameters;
	public Block blockFactory(String type, String name, String[]parameters) throws Exception {
		if (type.equals("void")){
			for (String parameter:parameters) {
				CommandFactory.createVars (parameter);

			}
		}

	}
}
