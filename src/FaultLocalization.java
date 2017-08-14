import java.io.File;
import java.io.IOException;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class FaultLocalization {
	public static void main(String[] args) throws SerializationException, IOException{
		String path = "/Users/ashleychen/Desktop/EntropyLocalization/Copies/Lang";
		for(int i=1;i<10;i++){
			File directoryPath = new File(path+"/"+i+"/b");
			System.out.println(directoryPath);
			File[] files = directoryPath.listFiles();
			for(File file: files){
				String[] arguments = {"/Users/ashleychen/Desktop/models_defects4j/lang"+16+"b.tsg",
										file.getAbsolutePath(),
										"/Users/ashleychen/Desktop/lang/lang"+i};
				EntropyGenerator.main(arguments);
			}
		}
	}
}
