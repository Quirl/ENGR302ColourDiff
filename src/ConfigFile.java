import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A configuration file which is essentially a list
 * of white space separated (x, y, width, height) tuples
 * @author colin
 *
 */
public class ConfigFile implements Iterable<Rectangle>{
	
	private List<Rectangle> rectangles;
	
	/**
	 * Reads the given file as a configuration file, parsing all the
	 * Rectangles inside
	 * @param configFileName The file name for the configuration file to read
	 * @throws IOException For standard IO errors
	 */
	public ConfigFile(String configFileName) throws IOException{
		rectangles = new ArrayList<Rectangle>();
		try(BufferedReader in = new BufferedReader(new FileReader(configFileName))){
			while(in.ready()){
				String[] lineData = in.readLine().split(" ");
				if(lineData.length == 0)continue;
				if(lineData.length != 4)throw new IllegalArgumentException("Malformed line in config file");
				//Java8 stream stuff to parse the line
				int[] rectData = Arrays.stream(lineData).mapToInt(Integer::parseInt).toArray();
				Rectangle rect = new Rectangle(rectData[0], rectData[1], rectData[2], rectData[3]);
				rectangles.add(rect);
			}
		}
	}
	
	/**
	 * Returns the ith rectangle in this config file
	 * @param i The index of the Rectangle to get
	 * @return The ith rectangle in this config file
	 */
	public Rectangle get(int i){
		if(i < 0 || i >= size()){
			throw new IllegalArgumentException("Invalid index " + i);
		}
		return rectangles.get(i);
	}
	
	/**
	 * Returns the number of Rectangles in this config file
	 * @return the number of Rectangles in this config file
	 */
	public int size(){
		return rectangles.size();
	}

	/**
	 * Returns an Iterator over the Rectangles in this ConfigFile
	 * @return an Iterator over the Rectangles in this ConfigFile
	 */
	@Override
	public Iterator<Rectangle> iterator() {
		return rectangles.iterator();
	}
}
