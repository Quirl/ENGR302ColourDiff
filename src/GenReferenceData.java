import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;


public class GenReferenceData {

	private static final List<Rectangle> readConfigFile(String name)
			throws IOException {
		List<Rectangle> boxes = new ArrayList<Rectangle>();
		try (BufferedReader read = new BufferedReader(new FileReader(name))) {
			String line;
			while ((line = read.readLine()) != null) {
				String[] data = line.split("\\s");
				System.out.println(Arrays.toString(data));
				if (data.length != 4) {
					throw new RuntimeException("Invalid definition in config file");
				}
				int x = Integer.parseInt(data[0]);
				int y = Integer.parseInt(data[1]);
				int w = Integer.parseInt(data[2]);
				int h = Integer.parseInt(data[3]);
				Rectangle r = new Rectangle(x, y, w, h);
				boxes.add(r);
			}
		}
		return boxes;
	}

	public static final HSBColor[] getTrims(BufferedImage img, List<Rectangle> trims){
		HSBColor[] data = new HSBColor[trims.size()];
		for(int i = 0;i < trims.size();i ++){
			Rectangle r = trims.get(i);
			BufferedImage subImg = img.getSubimage(r.x, r.y, r.width, r.height);
			data[i] = new HSBImage(subImg).medianColor();
		}
		return data;
	}

	public static void main(String[] args) throws IOException{
		if(args.length != 4){
			System.err.println("Usage: GenReferenceData <config_file> <reference_strip> <reference_value_location> <output_file>");
		}
		List<Rectangle> sources = readConfigFile(args[0]);
		BufferedImage referenceStrip = ImageIO.read(new File(args[1]));
		HSBColor[] referenceMedians = getTrims(referenceStrip, sources);
		File[] references = new File(args[2]).listFiles();
		Map<String, HSBColor[]> dists = new HashMap<String, HSBColor[]>();
		for(File f : references){
			BufferedImage source = ImageIO.read(f);
			HSBColor[] medians = getTrims(source, sources);
			for(int i = 0;i < medians.length;i ++){
				medians[i] = referenceMedians[i].differenceFrom(medians[i]);
			}
			dists.put(f.getName().substring(0, f.getName().indexOf(".")), medians);
		}
		new Context(sources, dists).write(new File(args[3]));
	}
}
