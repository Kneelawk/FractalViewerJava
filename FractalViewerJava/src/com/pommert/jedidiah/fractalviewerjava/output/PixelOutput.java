package com.pommert.jedidiah.fractalviewerjava.output;


public interface PixelOutput {
	public abstract void init(int width, int height);

	public abstract void setPixel(int x, int y, Colour color);
	
	public abstract float percentDone();

	public abstract void finish(String name);
}
