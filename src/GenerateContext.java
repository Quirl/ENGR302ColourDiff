import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class GenerateContext {
	public static void main(String[] args) throws IOException{
		if(args.length < 3){
			System.err.println("Usage: java GenerateContext <config_file>  <output_file> <reference_imgs>");
			System.exit(1);
		}
		
		ConfigFile file = new ConfigFile(args[0]);
		if(file.size() != 1){
			System.err.println("Config file is expected to contain one rectangle. Using first");
		}
		
		Context context = new Context();
		
		for(int i = 2;i < args.length;i ++){
			File inFile = new File(args[i]);
			if(!inFile.exists()){
				System.err.println("File " + args[i] + " doesnt exist");
				System.exit(2);
			}
			
			BufferedImage img = ImageIO.read(inFile);
			Rectangle rect = file.get(0);
			BufferedImage subImage = img.getSubimage(rect.x, rect.y, rect.width, rect.height);
			HSBImage hsbImage = new HSBImage(subImage);
			HSBColor medianColor = hsbImage.medianColor();
			int c = Color.HSBtoRGB(medianColor.h, medianColor.s, medianColor.b);
			String name = inFile.getName();
			name = name.substring(0, name.lastIndexOf('.'));
			context.addNameMap(name, c);
		}
		
		context.write(new File(args[1]));
	}
}
