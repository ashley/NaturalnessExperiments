import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

public class DiffCode {
	public static void main(String[] args) throws IOException{
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(new File(args[0]), new File(args[1]));
		ArrayList<SourceCodeChange> uniqueChange = new ArrayList<SourceCodeChange>();
		for(SourceCodeChange change: distiller.getSourceCodeChanges()){
			if(!change.getChangeType().toString().substring(0,7).equals("COMMENT")){
				String change_name = change.toString();
				if(change_name.substring(0,6).equals("Update")){
					System.out.println(change.getChangedEntity().getOriginalNode());
					System.out.println(change.getChangedEntity().getStartPosition());
					uniqueChange.add(change);
				}
			}
		}
		
		List<ASTNode> stmts = EntropyGenerator.parseAST(new File(args[0]));
		for(SourceCodeChange change: uniqueChange){
			for(ASTNode node: stmts){
				if(change.getChangedEntity().getStartPosition() == node.getStartPosition()){
					ASTNode ast = change.getChangedEntity().getOriginalNode();
					ASTNode stmt = node;
					System.out.println(ast.equals(stmt));
					System.out.println(node);
				}
			}
		}
		
		
	}
}
