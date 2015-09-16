import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class ContextToImages {
	public static void main(String[] args) throws IOException{
		String contextFile = args[0];
		String outputFolder = args[1];
		new File(outputFolder).mkdir();
		Context context = new Context(contextFile);
		for(Map.Entry<String, Integer> colours : context){
			BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)img.getGraphics();
			g.setColor(new Color(colours.getValue()));
			g.fillRect(0, 0, 50, 50);
			ImageIO.write(img, "png", new File(outputFolder, colours.getKey() + ".png"));
		}
	}
}
