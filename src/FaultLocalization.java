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
		for(int i=36;i<66;i++){
			File directoryPath = new File(path+"/"+i+"/b");
			System.out.println(directoryPath);
			File[] files = directoryPath.listFiles();
			for(File file: files){
				HashMap<ASTNode, Double> entropyResults = EntropyGenerator.simpleAggregation(model, file, "/Users/ashleychen/Desktop/lang/lang"+i);
			}
		}
	}
}
