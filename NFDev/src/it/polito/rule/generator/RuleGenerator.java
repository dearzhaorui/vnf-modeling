package it.polito.rule.generator;


import org.eclipse.jdt.core.dom.QualifiedName;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.jdt.core.dom.ASTVisitor;

import it.polito.nfdev.jaxb.ExpressionObject;
import it.polito.nfdev.jaxb.ExpressionResult;
import it.polito.nfdev.jaxb.LONot;
import it.polito.nfdev.jaxb.LogicalUnit;
import it.polito.nfdev.jaxb.ObjectFactory;
import it.polito.parser.IfElseBranch;
import it.polito.parser.IfElseBranch.Branch;
import it.polito.parser.MyExpression;
import it.polito.parser.context.ReturnSnapshot;

public class RuleGenerator {
	
	private ReturnSnapshot returnSnapshot;
	private ObjectFactory factory;
	private RuleContext ruleContext;
	private Map<String,LogicalUnit> units;
	private List<ExpressionObject> expressions;
	private String field;
	private String fileNameXml;
	private String fileNameTxt;
	private boolean verbose = false;
	private boolean isDataDriven = false;
	private int tableSize = 0;
	private List<String> tableTypes;
	
	public RuleGenerator(String name, boolean verbose){
		this.factory = new ObjectFactory();
		this.units = new HashMap<String,LogicalUnit>();
		this.expressions = new ArrayList<>();
		this.verbose = verbose;
		
		fileNameXml = "./xsd/Rule_"+name+".xml" ;
		fileNameTxt = "./xsd/txt/Rule_"+name+".txt";
	}
	
	
	public void setSnapshot(ReturnSnapshot returnSnapshot){
		assert returnSnapshot!=null;
		
		this.returnSnapshot = returnSnapshot;
		this.ruleContext = new RuleContext(factory, returnSnapshot);
		this.tableSize = returnSnapshot.getMethodContext().getContext().tableSize;
		this.isDataDriven = returnSnapshot.getMethodContext().getContext().isDataDriven();
		this.tableTypes = returnSnapshot.getMethodContext().getContext().tableTypes;
		
		if(tableTypes.size() != tableSize)
			throw new RuntimeException("Error: tableSize and tableTypes.size() must be of the same size: " + tableSize + " - "+ tableTypes.size());
	}
	
	public void generateRule(){
		
		if(ruleContext==null || returnSnapshot==null){
			System.err.println("ruleContext or returnSnapshot can not be null!");
			return;
		}			
		List<String> fields = new ArrayList<String>();
		boolean negated = false;	
		
	
		ConditionVisitor visitor = new ConditionVisitor(ruleContext);
		for(IfElseBranch branch : returnSnapshot.getConditions()){
			if(branch.getBranch()==Branch.IF)
				negated=false;
			else
				negated = true;
			
			branch.getStatement().accept(visitor);
			ExpressionObject temp =visitor.getExpression();
			if(temp!=null){
				
				if(negated){
					if(temp.getNot()!=null)
						ruleContext.setLastExpression(temp.getNot().getExpression());
					else{
						LONot not = factory.createLONot();
						not.setExpression(temp);
						ExpressionObject exp = factory.createExpressionObject();
						exp.setNot(not);
						ruleContext.setLastExpression(exp);
					}
					
				}
				else
					ruleContext.setLastExpression(temp);
			}
			visitor.clean();
		}
		
		visitor.clean();
		for(IfElseBranch branch : returnSnapshot.getPreviousConditions()){
			
			branch.getStatement().accept(visitor);
			ExpressionObject temp = visitor.getExpression();
			if(temp!=null){
	
				if(temp.getNot()!=null)
					ruleContext.setLastExpression(temp.getNot().getExpression());
				else{
					LONot not = factory.createLONot();
					not.setExpression(temp);
					ExpressionObject exp = factory.createExpressionObject();
					exp.setNot(not);
					ruleContext.setLastExpression(exp);
								
				}
		
			}
			visitor.clean();
		}
		
		if(verbose){
			if(!returnSnapshot.getReturnPredicates().isEmpty()){
					
					for(MyExpression expression : returnSnapshot.getReturnPredicates()){
						if(expression.getPacketName().compareTo(returnSnapshot.getPacketName())!=0)
							continue;
						
						expression.getField().accept(new ASTVisitor(){
							public boolean visit(QualifiedName node){
								field = node.getName().getFullyQualifiedName();
								return false;
							}
						});
						
						expression.getValue().accept(new ReturnExpressionVisitor(ruleContext, field));
						fields.add(field);
						field = null;
					}
				}
			
				ruleContext.setExitPacketConditions(fields);
		}
	

		ExpressionObject tempResult = ruleContext.getResult();
		List<LogicalUnit> tempList = ruleContext.getLogicaUnits();
		
		for(LogicalUnit temp : tempList){
			if(!units.containsKey(temp.getName())){
				units.put(temp.getName(), temp);
			}
		}
		
		expressions.add(tempResult);
	}
	
	public void saveRule(){
	
		ExpressionResult result = factory.createExpressionResult();
		result.getLogicalExpressionResult().addAll(expressions);
		result.getNodeOrPacketOrTime().addAll(units.values());
		result.setTableSize(tableSize);
		result.setDataDriven(isDataDriven);
		result.getTableFields().addAll(tableTypes);
		Collections.sort(result.getNodeOrPacketOrTime(), new Comparator<LogicalUnit>() {

			@Override
			public int compare(LogicalUnit o1, LogicalUnit o2) {
				return o1.getName().compareTo(o2.getName());	
			}
		});
		
		try {
			JAXBContext context = JAXBContext.newInstance("it.polito.nfdev.jaxb");
			JAXBElement<ExpressionResult> element = factory.createResult(result);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.example.org/LogicalExpressions LogicalExpressions.xsd");
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File("./xsd/LogicalExpressions.xsd"));
			marshaller.setSchema(schema);
			
			marshaller.marshal(element, new File(fileNameXml));
		
			FileWriter writer = new FileWriter(new File(fileNameTxt));
			HumanReadableRule rule = new HumanReadableRule(element.getValue());
			writer.write(rule.generateRule());
			writer.flush();
			writer.close();
		
		} catch (JAXBException e) {
		
			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	

}
