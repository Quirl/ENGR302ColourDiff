import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConfigFile implements Iterable<Rectangle>{
	
	private List<Rectangle> rectangles;
	
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
	
	public Rectangle get(int i){
		return rectangles.get(i);
	}
	
	public int size(){
		return rectangles.size();
	}

	@Override
	public Iterator<Rectangle> iterator() {
		return rectangles.iterator();
	}
}
