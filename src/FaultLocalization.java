import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class FaultLocalization {
	public static void main(String[] args) throws SerializationException, IOException{
		String path = "/Users/ashleychen/Desktop/EntropyLocalization/Copies/Lang";
		TSGrammar<TSGNode> model = EntropyGenerator.importModel("/Users/ashleychen/Desktop/models_defects4j/lang"+16+"b.tsg");
		for(int i=36;i<37;i++){
			File bDirectoryPath = new File(path+"/"+i+"/b");
			File fDirectoryPath = new File(path+"/"+i+"/f");
			System.out.println(fDirectoryPath);
			File[] bFiles = bDirectoryPath.listFiles();
			File[] fFiles = fDirectoryPath.listFiles();
			for(int fileIndex=0;fileIndex<bDirectoryPath.length();fileIndex++){
				HashMap<ASTNode, Double> entropyResults = EntropyGenerator.simpleAggregation(model, bFiles[fileIndex], "/Users/ashleychen/Desktop/lang/lang"+i);
				String[] arguments = {bFiles[fileIndex].getAbsolutePath(),fFiles[fileIndex].getAbsolutePath()};
				DiffCode.getDiffASTs(arguments);
			}
		}
	}
}
