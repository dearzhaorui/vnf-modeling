package it.polito.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

import it.polito.parser.context.Context;
import it.polito.parser.context.StatementContext;
import it.polito.parser.context.TableEntryContext;

public class ExpressionVisitor extends ASTVisitor {
	
	private List<MyExpression> predicates;
	private int nestingLevel;
	private StatementContext context;
	
	public ExpressionVisitor(int nestingLevel, StatementContext context) {
		predicates = new ArrayList<MyExpression>();
		this.nestingLevel = nestingLevel;
		this.context = context;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		StringBuilder builder = new StringBuilder();
		switch(node.getName().toString())
		{
			case Constants.DATA_DRIVEN:
				context.getMethodContext().getContext().setDataDriven(true);
				break;
		
			case Constants.SET_TYPES:
				
				Context ctx = context.getMethodContext().getContext();
				node.accept(new ASTVisitor() {
					
					public boolean visit(MethodInvocation node){
						
						
						for(ASTNode ast_node : (List<ASTNode>)node.arguments()){
							builder.delete(0, builder.length());
							ast_node.accept(new ASTVisitor(){
								
								public boolean visit(QualifiedName node){
									
									builder.append(node.getName().getFullyQualifiedName());
									return false;
								}					
							});
							
							String value = builder.toString();
							ctx.tableTypes.add(builder.toString());						
						}				
						return false;
					}
					
				});
				
				break;
				
			case Constants.SET_FIELD_METHOD:
				
				
				node.getExpression().accept(new ASTVisitor() {
					
					public boolean visit(SimpleName node){
						builder.append(node.getFullyQualifiedName());
						return false;
					}
				});
				// Example -> Verifier.Packet.setPacketField(PacketType.PACKET_OUT, PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
				if(node.arguments().size() != 2)
				{
					System.err.println("[ERROR] Wrong number of arguments passed to the "+Constants.SET_FIELD_METHOD+" method!");
					return false;
				}
				
				//System.out.println("Method Invocation -> " + ASTNode.nodeClassForType(node.getNodeType()).getSimpleName() + " Expression -> " + node.getExpression().toString());
				/*if(!node.getExpression().toString().equals("Verifier.Packet"))
				{
					System.err.println("[ERROR] The "+Constants.SET_FIELD_METHOD_NAME+"() method must be called by means of Verifier.Network!");
					return false;
				}*/
				//Expression packet = (Expression) node.arguments().get(0);
				Expression field = (Expression) node.arguments().get(0);
				Expression value = (Expression) node.arguments().get(1);
				MyExpression expression = new MyExpression(field, value, nestingLevel);
				expression.setPacketName(builder.toString());
				//if(packet.toString().equals("PacketType.PACKET_OUT"))
				predicates.add(expression);
				break;
			case Constants.ENTRY_SETTER:
				int position = -1;
				if(node.arguments().size()!=2){
					System.out.println("[ERROR] Wrong number of arguments passed to the "+Constants.ENTRY_SETTER+" method!");
					return false;
				}
				
				Expression fieldEntry = (Expression) node.arguments().get(0);
				Expression valueEntry = (Expression) node.arguments().get(1);
				
				fieldEntry.accept(new ASTVisitor() {
					
					public boolean visit(NumberLiteral node){
						builder.append(node.getToken());
						return false;
					}
				});
				
				position = Integer.parseInt(builder.toString());
				builder.delete(0, builder.length());
				valueEntry.accept(new ASTVisitor() {
					public boolean visit(MethodInvocation node){
						@SuppressWarnings("unchecked")
						List<Expression> list =  (List<Expression>)(node.arguments());
						if(!list.isEmpty()){
							Expression exp =(Expression) node.arguments().get(0);
							exp.accept(this);
							
						}else{
							builder.append(Constants.PLACE_HOLDER);
						}
						return false;
					}
					
					public boolean visit(SimpleName node){
						builder.append(node.getFullyQualifiedName());
						return false;
					}
					
					public boolean visit(QualifiedName node){
						builder.append(node.getName().getFullyQualifiedName());
						return false;
					}
				});
				
				
				MyExpression expressionEntry = new MyExpression(fieldEntry, valueEntry, -1);
				
				TableEntryContext entry = new TableEntryContext(context.getConditions(), builder.toString(), position);
				entry.setExpression(expressionEntry);
				context.getMethodContext().addEntryValues(entry);
				
				break;	
			case Constants.SATISFY_METHOD_NAME:
				//Verifier.Packet.satisfy(PacketType.PACKET_IN, Verifier.EXPR.FROM_INTERNAL);
				if(node.arguments().size() != 2)
				{
					System.err.println("[ERROR] Wrong number of arguments passed to the "+Constants.SATISFY_METHOD_NAME+" method!");
					return false;
				}
				if(!node.getExpression().toString().equals("Verifier.Packet"))
				{
					System.err.println("[ERROR] The "+Constants.SATISFY_METHOD_NAME+"() method must be called by means of Verifier.Packet!");
					return false;
				}
				break;
			case Constants.SEND_METHOD_NAME:
				//Verifier.Network.sendPacket(Verifier.EXPR.TO_EXTERNAL);
				if(node.arguments().size() != 1)
				{
					System.err.println("[ERROR] Wrong number of arguments passed to the "+Constants.SEND_METHOD_NAME+" method!");
					return false;
				}
				if(!node.getExpression().toString().equals("Verifier.Network"))
				{
					System.err.println("[ERROR] The "+Constants.SEND_METHOD_NAME+"() method must be called by means of Verifier.Network!");
					return false;
				}
				//System.out.println("Recognized sendPacket() method");
				break;
			default:
				// Just do nothing
				break;
		}
		return false;
	}
	
	public List<MyExpression> getPredicates() { return predicates; }
	
}
