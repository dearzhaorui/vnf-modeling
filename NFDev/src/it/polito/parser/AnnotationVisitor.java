package it.polito.parser;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

public class AnnotationVisitor extends ASTVisitor {
	
	private boolean foundAnnotation;
	private String annotation;
	private String[] fieldsType;
	
	public AnnotationVisitor() {
		foundAnnotation = false;
	}
	
	public boolean foundAnnotation() {
		return foundAnnotation;
	}
	
	public String getAnnotation() {
		return annotation;
	}
	
	public String[] getFieldsType() {
		return fieldsType;
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		System.out.println("\tFound normal annotation -> " + node.getTypeName().getFullyQualifiedName().toString());
		@SuppressWarnings("unchecked")
		List<MemberValuePair> list = node.values();
		for(MemberValuePair pair : list)
		{
			if(pair.getName().toString().equalsIgnoreCase("fields"))
			{
				String[] fields = pair.getValue().toString()
										.replace("\"", "")
										.replace("{", "")
										.replace("}", "")
										.split(",");
				//for(String s : fields)
				//System.out.println("Found field " + s);
				this.fieldsType = fields;
			}
		}
		annotation = node.getTypeName().toString();
		foundAnnotation = true;
		return false;
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		System.out.println("\tFound marker annotation -> " + node.getTypeName().getFullyQualifiedName().toString());
		annotation = node.getTypeName().toString();
		foundAnnotation = true;
		return false;
	}
	
	@Override
	public boolean visit(SingleMemberAnnotation node){
		System.out.println("\tFound single member annotation -> " + node.getTypeName().getFullyQualifiedName());
		annotation = node.getTypeName().toString();
		foundAnnotation = true;
		return false;
	}

}
