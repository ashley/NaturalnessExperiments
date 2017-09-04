import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
		localExperiment(model);
	}
	
	public static void localExperiment(TSGrammar<TSGNode> model) throws IOException, SerializationException{
		//String[] arguments = {"testfiles/ClassWithComments.java","testfiles/ClassWithComments2.java"};
		String[] arguments = {"testfiles/TestLeft.java","testfiles/TestRight.java"};
		List<FaultExpression> expressions = DiffCode.getDiffASTs(arguments);
		EntropyGenerator en = new EntropyGenerator(model,expressions);
		writeCSV(expressions, "/Users/ashleychen/Desktop/testing.csv");
	}
	
	public static void writeCSV(List<FaultExpression> exp, String filename) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("Entropy, ChangeID" + "\n");
		for(FaultExpression s: exp){
			sb.append(s.getEntropy() + " ," + s.getChangeID());
			sb.append("\n");
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(filename);
			 bw = new BufferedWriter(fw);
			bw.write(sb.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(bw !=null)
					bw.close();
				if(fw!=null)
					fw.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static void defects4jExperiment(String path, TSGrammar<TSGNode> model) throws SerializationException, IOException{
		//Prototype for one bug in lang
		int i = 1;	
		File bDirectoryPath = new File(path+"/"+i+"/b");
		File fDirectoryPath = new File(path+"/"+i+"/f");
		
		//Accept java files
		FilenameFilter javaFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".java");
			}
		};
		File[] bFiles = bDirectoryPath.listFiles(javaFilter);
		File[] fFiles = fDirectoryPath.listFiles(javaFilter);
		
		//files changed in first bug
		for(int fileIndex=0;fileIndex<bFiles.length;fileIndex++){
			EntropyGenerator eg = new EntropyGenerator(model, bFiles[fileIndex], "/Users/ashleychen/Desktop/lang/lang"+i);
			HashMap<ASTNode, Double> entropyResults = eg.getEntropy();
			
			String[] arguments = {bFiles[fileIndex].getAbsolutePath(),fFiles[fileIndex].getAbsolutePath()};
			List<FaultExpression> changeASTs = DiffCode.getDiffASTs(arguments);
		}
	}
}
