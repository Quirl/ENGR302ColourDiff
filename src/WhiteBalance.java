import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * A simple (Testing) white balancing implementation that doesnt seem to work
 * very well.
 *
 */
public class WhiteBalance {
	private ConfigFile whiteSpots;
	
	public WhiteBalance(String configFile) throws IOException{
		whiteSpots = new ConfigFile(configFile);
	}

	/**
	 * Calculates the median RGB color in an image over all the channels
	 * @param img The image to find the median colour in
	 * @return the median colour from the image
	 */
	private Color getMedianColor(BufferedImage img){
		int pointCount = img.getWidth() * img.getHeight();

		int[] rgbs = new int[pointCount];
		int[] reds = new int[pointCount];
		int[] greens = new int[pointCount];
		int[] blues = new int[pointCount];
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgbs, 0, img.getWidth());
		for(int i = 0;i < rgbs.length;i ++){
			int rgb = rgbs[i];
			reds[i]   = (rgb >> 16) & 0xFF;
			greens[i] = (rgb >> 8) & 0xFF;
			blues[i]  = (rgb >> 0) & 0xFF;
		}

		Arrays.sort(reds);
		Arrays.sort(greens);
		Arrays.sort(blues);
		
		//Find the average of the interquartile range
		int r = 0, g = 0, b = 0;
		int lowerQuartile = (int)Math.floor(pointCount/4.0);
		int upperQuartile = (int)Math.floor(3.0*(pointCount/4.0));
		System.out.println(img.getWidth() * img.getHeight() + " " + lowerQuartile + " " + upperQuartile);
		int quartileRange = upperQuartile - lowerQuartile + 1;
		for (int i = lowerQuartile; i <= upperQuartile; i++){
			r += reds[i];
			g += greens[i];
			b += blues[i];
		}
		System.out.println(r);
		return new Color(r/quartileRange, g/quartileRange, b/quartileRange);
	}

	/**
	 * Returns the distance between a given point and a rectangle
	 * @param p the point
	 * @param r the rectangle
	 * @return the distance between the point and the rectangle
	 */
	private double distanceFromRectangle(Point p, Rectangle r){
		Point2D topLeft = new Point2D.Double(r.getMinX(), r.getMinY());
		Point2D topRight = new Point2D.Double(r.getMaxX(), r.getMinY());
		Point2D bottomLeft = new Point2D.Double(r.getMinX(), r.getMaxY());
		Point2D bottomRight = new Point2D.Double(r.getMaxX(), r.getMaxY());
		
		Line2D[] sidesOfRectangle = new Line2D.Double[]{
			new Line2D.Double(topLeft, topRight),
			new Line2D.Double(topRight, bottomRight),
			new Line2D.Double(bottomRight, bottomLeft),
			new Line2D.Double(bottomLeft, topLeft)
		};
		
		double minDistance = Double.POSITIVE_INFINITY;
		for(Line2D side : sidesOfRectangle){
			double distance = side.ptLineDist(p);
			if(distance < minDistance){
				minDistance = distance;
			}
		}

		return minDistance;
	}

	/**
	 * Returns a (hopefully) white balanced version of the given image, assuming that the colour
	 * in the given rectangles is supposed to be white
	 * @param fullImage the image to process
	 * @param whiteSpots the supposedly white spots of the image
	 * @return the white-balanced image
	 */
	public BufferedImage balance(BufferedImage fullImage){
		System.out.printf("Image Dimensions: %dx%d\n", fullImage.getWidth(), fullImage.getHeight());
		List<Color> subImageMedians = new ArrayList<Color>(whiteSpots.size());

		//Find the medians for all the white spots
		for(Rectangle rect : whiteSpots){
			BufferedImage subImage = fullImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
			Color median = getMedianColor(subImage);
			subImageMedians.add(median);
			System.out.printf("Found median color: %s\n", median);
		}

		int[] fullRGB = new int[fullImage.getWidth() * fullImage.getHeight()];
		fullImage.getRGB(0, 0, fullImage.getWidth(), fullImage.getHeight(), fullRGB, 0, fullImage.getWidth());

		for(int y = 0;y < fullImage.getHeight();y ++){
			for(int x = 0;x < fullImage.getWidth();x ++){
				List<Double> distances = new ArrayList<Double>(whiteSpots.size());
				//If we have more than one white spot, work out how much each should affect this pixel
				if(whiteSpots.size() > 1){
					for(Rectangle spot : whiteSpots){
						distances.add(distanceFromRectangle(new Point(x, y), spot));
					}

					double sum = distances.stream().mapToDouble(i->i).sum();
					for(int i = 0;i < distances.size();i ++){
						distances.set(i, distances.get(i) / sum);
					}
				}
				else{
					distances.add(1.);
				}

				//Calculate the white corrected value
				double weightedRed = 0;
				double weightedGreen = 0;
				double weightedBlue = 0;

				for(int i = 0;i < distances.size();i ++){
					weightedRed += distances.get(i) * subImageMedians.get(i).getRed();
					weightedGreen += distances.get(i) * subImageMedians.get(i).getGreen();
					weightedBlue += distances.get(i) * subImageMedians.get(i).getBlue();
				}
				int index = y * fullImage.getWidth() + x;
				int oldRed   = (fullRGB[index] >> 16) & 0xFF;
				int oldGreen = (fullRGB[index] >> 8) & 0xFF;
				int oldBlue  = (fullRGB[index] >> 0) & 0xFF;
				int newRed   = (int)Math.min(oldRed * (255. / weightedRed), 255);
				int newGreen = (int)Math.min(oldGreen * (255. / weightedGreen), 255);
				int newBlue   = (int)Math.min(oldBlue * (255. / weightedBlue), 255);
				int newCol = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
				fullRGB[index] = newCol;
			}
		}

		BufferedImage newImg = new BufferedImage(fullImage.getWidth(), fullImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		newImg.setRGB(0, 0, newImg.getWidth(), newImg.getHeight(), fullRGB, 0, newImg.getWidth());

		return newImg;
	}

	public static void main(String[] args){
		if(args.length != 2){
			System.err.println("Usage: java WhiteBalance <config_file> <image>");
			System.exit(1);
		}

		String configFile = args[0];
		String imageFile = args[1];
		WhiteBalance balance = null;
		
		try{
			balance = new WhiteBalance(configFile);
		}
		catch(IOException e){
			System.err.println("Failed to read config file");
			System.exit(2);
		}
		catch(IllegalArgumentException e){
			System.err.println("Malformed Config File");
			System.exit(3);
		}

		BufferedImage img = null;
		try{
			img = ImageIO.read(new File(imageFile));
		}
		catch(IOException e){
			System.err.println("Failed to read image file");
			System.exit(4);
		}

		BufferedImage outImage = balance.balance(img);
		try{
			ImageIO.write(outImage, "png", new File("output.png"));
		}
		catch(IOException e){
			System.err.println("Failed to write output file");
			System.exit(5);
		}
	}
}
