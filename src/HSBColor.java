import java.awt.Color;

/**
 * An Color in HSB space
 */
public class HSBColor {
	//The components of this HSB colour
	public float h, s, b;
	
	/**
	 * Constructs an HSB Colour from the given components
	 * @param _h The Hue of this HSB Colour
	 * @param _s The Saturation of this HSB Colour
	 * @param _b The Brightness of this HSB Colour
	 */
	public HSBColor(float _h, float _s, float _b){
		h = _h;
		s = _s;
		b = _b;
	}
	
	/**
	 * Constructs an HSB Colour from the given RGB Colour as an int
	 * @param rgb An int representation of the RGB Colour
	 */
	public HSBColor(int rgb){
		//Some bit shifting to extract the components
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = (rgb >> 0) & 0xFF;
		float[] data = Color.RGBtoHSB(red, green, blue, null);
		h = data[0];
		s = data[1];
		b = data[2];
	}
	
	/**
	 * Returns the difference between this HSB Colour and the other
	 * @param other The HSBColour to take the difference between
	 * @return The difference between this HSBColour and the given one
	 */
	public HSBColor differenceFrom(HSBColor other) {
		return new HSBColor(this.h - other.h, this.s - other.s, this.b - other.b);
	}
	
	public float floatDifferenceFrom(HSBColor other) {
		return Math.abs(this.h - other.h) + Math.abs(this.s - other.s) + Math.abs(this.b - other.b);
	}
}
