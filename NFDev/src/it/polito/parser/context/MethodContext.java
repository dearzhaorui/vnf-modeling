package it.polito.parser.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.parser.Variable;

public class MethodContext {

	private String methodName;

	private Context context;
	
	private Map<String, List<Variable>> methodVariables;
	
	private List<ReturnSnapshot> returnSnapshots;
	private List<TableEntryContext> entryValues;

	public MethodContext(String methodeName, Context context) {
		
		assert methodeName != null;
		assert context != null;
		
		this.methodName = methodeName;
		this.context = context;
		this.methodVariables = new HashMap<>();
		this.returnSnapshots = new ArrayList<>();
		this.entryValues = new ArrayList<>();
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public Context getContext(){
		return context;
	}
	
	public void addMethodVariable(Variable methodVariable){
		List<Variable> list = methodVariables.get(methodVariable.getName());
		
		if(list==null){
			list = new ArrayList<Variable>();
			list.add(methodVariable);
			methodVariables.put(methodVariable.getName(), list);
		}else
			list.add(methodVariable);
		
	}

	public List<Variable> getMethodVariable(String methodVariableName) {
		return methodVariables.get(methodVariableName);
	}
	
	public Collection<List<Variable>> getMethodVariables(){
		return methodVariables.values();
	}
	
	public Map<String, List<Variable>> getMethodVariablesMap(){
		return methodVariables;
	}

	public List<ReturnSnapshot> getReturnSnapshots() {
		return returnSnapshots;
	}

	public boolean addReturnSnapshots(ReturnSnapshot returnSnapshot) {
		return returnSnapshots.add(returnSnapshot);
	}
	
	public List<TableEntryContext> getEntryValues(){
		return entryValues;
	}
	
	public boolean addEntryValues(TableEntryContext entry){
		return entryValues.add(entry);
	}

}
