import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ColorRecog {

	private static Context readDat(String fName) throws IOException {
		Map<String, float[][]> references = new HashMap<String, float[][]>();
		try (DataInputStream str = new DataInputStream(new FileInputStream(
				fName))) {
			int numBoxes = str.readInt();
			int numReferences = str.readInt();

			System.out.println(numBoxes + " sources");
			System.out.println(numReferences + " classes");
			System.out.println("\nSources: ");
			List<Rectangle> boxes = new ArrayList<Rectangle>(numBoxes);
			for (int i = 0; i < numBoxes; i++) {
				Rectangle r = new Rectangle(str.readInt(), str.readInt(), str.readInt(), str.readInt());
				System.out.println(r);
				boxes.add(r);
			}

			System.out.println();

			for (int z = 0; z < numReferences; z++) {
				float[][] data = new float[numBoxes][3];
				String name = str.readUTF();
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < 3; j++) {
						data[i][j] = str.readFloat();
					}
				}
				references.put(name, data);
			}
			return new Context(boxes, references);
		}
	}

	private static final Map<String, Float> estimateFromImage(float[][] images,
			Context context) throws IOException {
		Map<String, Float> dists = new HashMap<String, Float>();
		for (Map.Entry<String, float[][]> clas : context.diffs.entrySet()) {
			float dist = 0;
			for (int i = 0; i < clas.getValue().length; i++) {
				for (int j = 0; j < clas.getValue()[0].length; j++) {
					dist += Math.pow(clas.getValue()[i][j] - images[i][j], 2);
				}
			}
			dists.put(clas.getKey(), dist);
		}

		return dists;
	}

	private static final Map<String, Float> processImage(BufferedImage startImage, BufferedImage endImage, Context context) throws IOException {
		List<Rectangle> sources = context.sections;
		HSBImage[] startSubImages = new HSBImage[sources.size()];
		HSBImage[] endSubImages = new HSBImage[sources.size()];
		float[][] allDiffs = new float[startSubImages.length][3];
		for (int i = 0; i < startSubImages.length; i++) {
			Rectangle r = sources.get(i);
			startSubImages[i] = new HSBImage(startImage.getSubimage(r.x, r.y, r.width, r.height));
			endSubImages[i] = new HSBImage(endImage.getSubimage(r.x, r.y, r.width, r.height));
			float[] reference = startSubImages[i].medianColor();
			float[] actual = endSubImages[i].medianColor();
			for (int j = 0; j < 3; j++) {
				allDiffs[i][j] = reference[j] - actual[j];
			}
		}

		return estimateFromImage(allDiffs, context);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.err
					.println("Usage: ColorRecog <data_file> <original_image_file> <tested_image_file>");
			return;
		}

		final String configFileName = args[0];
		final String startFileName = args[1];
		final String endFileName = args[2];

		System.out.println("Processing image " + endFileName);
		System.out.println("Using Reference " + startFileName);
		System.out.println("Context: " + configFileName);

		Context data = readDat(configFileName);
		BufferedImage startImage = ImageIO.read(new File(startFileName));
		BufferedImage endImage = ImageIO.read(new File(endFileName));
		Map<String, Float> dists = processImage(startImage, endImage, data);
		double sum = 0;
		for (String s : dists.keySet()) {
			dists.put(s, (float) (1. / dists.get(s)));
		}

		for (double d : dists.values()) {
			sum += d;
		}

		for (String s : dists.keySet()) {
			dists.put(s, (float) (dists.get(s) / sum));
		}

		dists = sortByValue(dists);
		System.out.println("Class Probabilities: ");
		double expected = 0;
		for(Map.Entry<String, Float> dist : dists.entrySet()){
			expected += Double.parseDouble(dist.getKey()) * dist.getValue();
			System.out.println(dist.getKey() + " - " + dist.getValue() + "%");
		}

		System.out.println("Expected: " + expected);
	}
}
