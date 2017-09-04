import org.eclipse.jdt.core.dom.ASTNode;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class FaultExpression {
	private ASTNode node;
	private int position;
	private double entropy;
	private int changeID;
	
	FaultExpression(ASTNode n){
		node = n;
		position = node.getStartPosition();
	}
	
	FaultExpression(SourceCodeChange change){
		node = change.getChangedEntity().getOriginalNode();
		position = change.getChangedEntity().getStartPosition();
	}
	
	@Override
	public String toString(){
		return node.toString();
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
