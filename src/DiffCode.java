import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

public class DiffCode {
	public static HashMap<Integer, ASTNode> getDiffASTs(String[] args) throws IOException{
		//CD process
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(new File(args[0]), new File(args[1]));
		
		//CD information
		List<FaultExpression> uniqueChanges = new ArrayList<FaultExpression>();
		
		for(SourceCodeChange change: distiller.getSourceCodeChanges()){
			//Check if change is not a comment //TODO Figure out what to do with these comments
			if(!change.getChangeType().toString().substring(0,7).equals("COMMENT")){
				//Check if change is an update (only checking changes in syntax for now)
				if(change.toString().substring(0,6).equals("Update")){
					System.out.println("originalNode: " + change.getChangedEntity().getOriginalNode());
					System.out.println("startingPosition: "  +change.getChangedEntity().getStartPosition());
					uniqueChanges.add(new FaultExpression(change));
				}
			}
		}
		
		CompilationUnit cu = createAST(args[0]);
		
		//FaultLocalization information
		List<FaultExpression> filestmts = EntropyGenerator.convertAST(new File(args[0]));
		//Stored information
		HashMap<Integer, ASTNode> filteredStmts = new HashMap<Integer, ASTNode>();
		
		//For every change
		for(FaultExpression stmt : uniqueChanges){
			for(FaultExpression fileStmt: filestmts){
				if(stmt.getPosition() == fileStmt.getPosition()){
					//FIXME fileStmt's node is sometimes node for some reason
					System.out.println("found: " + fileStmt);
					//int num = cu.getLineNumber(startingPositions.get(i));
					//filteredStmts.put(num, node);
					break;
				}
			}
		}
		return filteredStmts;
		
		
		
		
	}
	
	public static CompilationUnit createAST(String filePath) throws IOException{
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		char[] source = ReadFileToCharArray(filePath);
	    // Parse the class as a compilation unit.
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource(source); // give your java source here as char array
	    parser.setResolveBindings(true);

	    // Return the compiled class as a compilation unit
	    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
	    return cu;
	}
	
	public static char[] ReadFileToCharArray(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString().toCharArray();	
	}
}
