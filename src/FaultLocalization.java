import java.io.File;
import java.io.FilenameFilter;
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
		for(int i=1;i<2;i++){
			File bDirectoryPath = new File(path+"/"+i+"/b");
			File fDirectoryPath = new File(path+"/"+i+"/f");
			System.out.println(fDirectoryPath);
			FilenameFilter javaFilter = new FilenameFilter() {
		        @Override
		        public boolean accept(File dir, String name) {
		            return name.toLowerCase().endsWith(".java");
		        }
		    };
			File[] bFiles = bDirectoryPath.listFiles(javaFilter);
			File[] fFiles = fDirectoryPath.listFiles(javaFilter);
			for(int fileIndex=0;fileIndex<bFiles.length;fileIndex++){
				EntropyGenerator eg = new EntropyGenerator(model, bFiles[fileIndex], "/Users/ashleychen/Desktop/lang/lang"+i);
				HashMap<ASTNode, Double> entropyResults = eg.getEntropy();
				
				String[] arguments = {bFiles[fileIndex].getAbsolutePath(),fFiles[fileIndex].getAbsolutePath()};
				HashMap<Integer,ASTNode> changeASTs = DiffCode.getDiffASTs(arguments);
				
				for (HashMap.Entry<Integer, ASTNode> entry : changeASTs.entrySet()) {
					System.err.println(entry);
				    //System.out.println(entropyResults.containsKey(value));
				    /*
					for(HashMap.Entry<ASTNode, Double> tree: entropyResults.entrySet()){
				    	if(tree.getKey().getStartPosition() == entry.getKey()){
				    		System.out.println("Found");
				    	}
				    }
				    */
				}
				for(HashMap.Entry<ASTNode, Double> entry : entropyResults.entrySet()){
					System.out.println(entry.getKey().getStartPosition());
				}
			}
		}
	}
}
