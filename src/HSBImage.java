import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;


public class HSBImage {
	private HSBColor[] data;
	public HSBImage(BufferedImage img){
		int[] colours = new int[img.getWidth() * img.getHeight()];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), colours, 0, img.getWidth());
		data = new HSBColor[colours.length];
		for(int i = 0;i < colours.length;i ++){
			int r = (colours[i] >> 16) & 0xFF;
			int g = (colours[i] >> 8) & 0xFF;
			int b = (colours[i] >> 0) & 0xFF;
			float[] vals = new float[3];
			Color.RGBtoHSB(r, g, b, vals);
			data[i] = new HSBColor(vals[0], vals[1], vals[2]);
		}
	}

	public float[] medianColor(){
		float[] h =	new float[data.length];
		float[] s =	new float[data.length];
		float[] b =	new float[data.length];
		for(int i = 0;i < data.length;i ++){
			h[i] = data[i].h;
			s[i] = data[i].s;
			b[i] = data[i].b;
		}
		Arrays.sort(h);
		Arrays.sort(s);
		Arrays.sort(b);
		float[] out = new float[3];
		out[0] = h[data.length / 2];
		out[1] = s[data.length / 2];
		out[2] = b[data.length / 2];
		return out;
	}
}
