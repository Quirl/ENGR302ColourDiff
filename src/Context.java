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

/**
 * A Context which represents a Number of Class->Colour pairs, one for each possible
 * value of a Chemical
 *
 */
public class Context implements Iterable<Map.Entry<String, Integer>>{
	private Map<String, Integer> nameToColour;
	
	/**
	 * Reads the Context from the given file
	 * @param contextFile The file name of the file to read
	 * @throws IOException On standard IO errors
	 */
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
	
	/**
	 * Constructs an empty context, ready for adding new classes
	 */
	public Context(){
		nameToColour = new HashMap<String, Integer>();
	}
	
	/**
	 * Returns the number of Class->Colour pairs in this Context
	 * @return the number of Class->Colour pairs in this Context
	 */
	public int size(){
		return nameToColour.size();
	}
	
	/**
	 * Adds a Class to this context, associated with the given Colour
	 * @param name The name of the class
	 * @param c An int representation of the Colour of the given class
	 */
	public void addNameMap(String name, int c){
		nameToColour.put(name, c);
	}
	
	/**
	 * Writes this Context file to the given output file
	 * @param f the file to write to
	 * @throws IOException On standard IO errors
	 */
	public void write(File f) throws IOException{
		try(DataOutputStream str = new DataOutputStream(new FileOutputStream(f))){
			str.writeInt(nameToColour.size());
			for(Map.Entry<String, Integer> entry : nameToColour.entrySet()){
				str.writeUTF(entry.getKey());
				str.writeInt(entry.getValue());
			}
		}
	}

	/**
	 * Returns an iterator over all the Class->Colour pairs in this Context
	 * @return an iterator over all the Class->Colour pairs in this Context
	 */
	@Override
	public Iterator<Entry<String, Integer>> iterator() {
		return nameToColour.entrySet().iterator();
	}
}
