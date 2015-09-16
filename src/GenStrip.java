import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A Utility class which recreates a strip image based on what the 
 * algorithm sees on the strip
 */
public class GenStrip {
	public static void main(String[] args) throws IOException{
		if(args.length < 4){
			System.err.println("Usage: java GenStrip <white_location_config> <colour_location_config> <image> <output_file>");
			System.exit(1);
		}
		
		String whiteLocationFile = args[0];
		String colourLocationFile = args[1];
		String imgFile = args[2];
		String outputFile = args[3];
		ConfigFile colourLocations = new ConfigFile(colourLocationFile);
		
		Rectangle nitrateRect = colourLocations.get(0);
		Rectangle nitriteRect = colourLocations.get(1);
		
		BufferedImage img = ImageIO.read(new File(imgFile));
		WhiteBalance balance = new WhiteBalance(whiteLocationFile);
		BufferedImage whiteBalanced = balance.balance(img);
		HSBImage nitrate = new HSBImage(whiteBalanced.getSubimage(nitrateRect.x, nitrateRect.y, nitrateRect.width, nitrateRect.height));
		HSBImage nitrite = new HSBImage(whiteBalanced.getSubimage(nitriteRect.x, nitriteRect.y, nitriteRect.width, nitriteRect.height));
		
		HSBColor medianNitrate = nitrate.medianColor();
		HSBColor medianNitrite = nitrite.medianColor();
		Color nitrateColor = new Color(Color.HSBtoRGB(medianNitrate.h, medianNitrate.s, medianNitrate.b));
		Color nitriteColor = new Color(Color.HSBtoRGB(medianNitrite.h, medianNitrite.s, medianNitrite.b));
		
		BufferedImage outputImg = new BufferedImage(200, 50, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gg = (Graphics2D)outputImg.getGraphics();
		gg.setColor(nitrateColor);
		gg.fillRect(0, 0, 50, 50);
		gg.setColor(nitriteColor);
		gg.fillRect(100, 0, 50, 50);
		
		ImageIO.write(outputImg, "png", new File(outputFile));
	}
}
