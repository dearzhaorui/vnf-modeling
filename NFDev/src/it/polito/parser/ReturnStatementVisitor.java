package it.polito.parser;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.QualifiedName;

import it.polito.nfdev.lib.RoutingResult.Action;

public class ReturnStatementVisitor extends ASTVisitor {
	
	private Action action;
	private String packetName;
	private String interfaceName;
	
	public ReturnStatementVisitor() {
		this.action = Action.UNKNOW;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getPacketName() {
		return packetName;
	}
	
	public String getInterfaceName(){
		return interfaceName;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {   // new RoutingResult
		if(!node.getType().toString().equals(Constants.ROUTING_RESULT_CLASS))
		{
			System.err.println("Error in the Return statement at line ");
			return false;
		}
		@SuppressWarnings("unchecked")
		List<Expression> arg = node.arguments();
		if(arg.size() != 3)
		{
			System.err.println("Wrong number of arguments provided to the "+Constants.ROUTING_RESULT_CLASS+" class");
			return false;
		}
		String interfaceName = arg.get(2).toString();
		String packetName = arg.get(1).toString();
		Expression action = arg.get(0);
		ReturnResult r1 = new ReturnResult();
		action.accept(r1);
		this.interfaceName = interfaceName;
		this.packetName = packetName;
		this.action = r1.getResult();
		return true;
	}
	
	private class ReturnResult extends ASTVisitor {
		
		private Action result;
		
		@Override
		public boolean visit(QualifiedName node) {
			
			switch(node.getName().toString())
			{
				case "FORWARD":
					this.result = Action.FORWARD;
					break;
				case "DROP":
					this.result = Action.DROP;
					break;
				default:
					System.err.println("Unrecognized action");
					this.result = Action.UNKNOW;
			}
			return true;
		}
		
		public Action getResult() { return this.result; }
		
	}

}
