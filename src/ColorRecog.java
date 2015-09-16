import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColorRecog {
	
	private Rectangle colourLocation;
	private Context context;
	
	public ColorRecog(Rectangle colourLoc, String contextFile) throws IOException{
		colourLocation = colourLoc;
		context = new Context(contextFile);
	}
	
	private Map<String, Double> estimateFromImage(HSBColor image) {
		Map<String, Double> distances = new HashMap<String, Double>();
		for(Map.Entry<String, Integer> nameColours : context){
			String name = nameColours.getKey();
			int rgbColor = nameColours.getValue();
			HSBColor hsbColor = new HSBColor(rgbColor);
			HSBColor diff = image.differenceFrom(hsbColor);
			double dist = Math.sqrt(diff.h * diff.h + diff.s * diff.s + diff.b * diff.b);
			distances.put(name, dist);
		}
		
		return distances;
	}

	public Map<String, Double> processImage(BufferedImage img) throws IOException {
		int x, y, w, h;
		x = colourLocation.x;
		y = colourLocation.y;
		w = colourLocation.width;
		h = colourLocation.height;
		BufferedImage subImage = img.getSubimage(x, y, w, h);
		HSBImage hsbSubImage = new HSBImage(subImage);
		HSBColor median = hsbSubImage.medianColor();
		
		return estimateFromImage(median);
	}
}
