import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class Algorithm {
	
	public static void main(String[] args) throws IOException{
		String whiteBalanceConfig = args[0];
		String colourConfig = args[1];
		String nitriteContextFile = args[2];
		String nitrateContextFile = args[3];
		String imageFile = args[4];
		
		WhiteBalance balance = new WhiteBalance(whiteBalanceConfig);
		ConfigFile colourLocations = new ConfigFile(colourConfig);
		
		ColorRecog nitrateRecog = new ColorRecog(colourLocations.get(0), nitrateContextFile);
		ColorRecog nitriteRecog = new ColorRecog(colourLocations.get(1), nitriteContextFile);
		
		BufferedImage img = ImageIO.read(new File(imageFile));
		BufferedImage whiteBalancedImg = balance.balance(img);
		
		Map<String, Double> nitrateAnalysis = nitrateRecog.processImage(whiteBalancedImg);
		Map<String, Double> nitriteAnalysis = nitriteRecog.processImage(whiteBalancedImg);
		
		System.out.println("Nitrate Analysis");
		for(Map.Entry<String, Double> nitrateClass : nitrateAnalysis.entrySet()){
			System.out.println(nitrateClass.getKey() + " -> " + nitrateClass.getValue());
		}
		
		System.out.println();
		
		System.out.println("Nitrite Analysis");
		for(Map.Entry<String, Double> nitriteClass : nitriteAnalysis.entrySet()){
			System.out.println(nitriteClass.getKey() + " -> " + nitriteClass.getValue());
		}
	}
}
