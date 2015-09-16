import java.awt.Color;

public class HSBColor {
	public float h, s, b;
	public HSBColor(float _h, float _s, float _b){
		h = _h;
		s = _s;
		b = _b;
	}
	
	public HSBColor(int rgb){
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = (rgb >> 0) & 0xFF;
		float[] data = Color.RGBtoHSB(red, green, blue, null);
		h = data[0];
		s = data[1];
		b = data[2];
	}
	
	public HSBColor differenceFrom(HSBColor other) {
		return new HSBColor(this.h - other.h
				, this.s - other.s
				, this.b - other.b);
	}
	public float floatDifferenceFrom(HSBColor other) {
		return Math.abs(this.h - other.h) + Math.abs(this.s - other.s) + Math.abs(this.b - other.b);
	}
}
