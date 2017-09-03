import org.eclipse.jdt.core.dom.ASTNode;

public class FaultExpression {
	private ASTNode node;
	private int position;
	private double entropy;
	private int changeID;
	
	FaultExpression(ASTNode node){
		node = node;
	}
	
	void setPosition(int pos){
		position = pos;
	}
	
	void setEntropy(double en){
		entropy = en;
	}
	
	void setChangeID(int id){
		changeID = id;
	}
	
	ASTNode getNode(){
		return node;
	}
	
	int getPosition(){
		return position;
	}
	
	double getEntropy(){
		return entropy;
	}
	
	int getChangeID(){
		return changeID;
	}
}
