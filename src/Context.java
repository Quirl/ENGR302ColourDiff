import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Context implements Iterable<Map.Entry<String, Integer>>{
	private Map<String, Integer> nameToColour;
	public Context(String contextFile) throws IOException{
		File f = new File(contextFile);
		if(!f.exists()){
			throw new IllegalArgumentException("Context file doesn't exist");
		}
		
		try(DataInputStream str = new DataInputStream(new FileInputStream(f))){
			int numReferences = str.readInt();
			if(numReferences <= 0){
				throw new IllegalArgumentException("Invalid number of references in Context file");
			}
	
			nameToColour = new HashMap<String, Integer>();
			for(int i = 0;i < numReferences;i ++){
				String name = str.readUTF();
				int colour = str.readInt();
				nameToColour.put(name, colour);
			}
		}
	}
	
	public int size(){
		return nameToColour.size();
	}
	
	public Context(){
		nameToColour = new HashMap<String, Integer>();
	}
	
	public void addNameMap(String name, int c){
		nameToColour.put(name, c);
	}
	
	public void write(File f) throws IOException{
		if(!f.exists()){
			f.createNewFile();
		}
		try(DataOutputStream str = new DataOutputStream(new FileOutputStream(f))){
			str.writeInt(nameToColour.size());
			for(Map.Entry<String, Integer> entry : nameToColour.entrySet()){
				str.writeUTF(entry.getKey());
				str.writeInt(entry.getValue());
			}
		}
	}

	@Override
	public Iterator<Entry<String, Integer>> iterator() {
		return nameToColour.entrySet().iterator();
	}
}
