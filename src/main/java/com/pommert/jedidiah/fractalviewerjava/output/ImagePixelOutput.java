package com.pommert.jedidiah.fractalviewerjava.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ar.com.hjg.pngj.PngWriter;

import com.pommert.jedidiah.pngjwrapper.PngJImage;

public class ImagePixelOutput implements PixelOutput {

	PngJImage image;

	@Override
	public void init(int width, int height) {
		image = new PngJImage(height, width, 8, true);
	}

	@Override
	public void setPixel(int x, int y, Colour color) {
		image.setPixel(x, y, color);
	}

	@Override
	public void finish(String name) {
		String filename = "./Fractal " + name;
		File saveTo = new File(filename + ".png");
		for (int i = 2; saveTo.exists(); i++) {
			saveTo = new File(filename + " #" + i + ".png");
		}
		try {
			System.out.println("Saving to file: " + saveTo.getCanonicalPath());
			PngWriter writer = image.save(new FileOutputStream(saveTo));
			writer.end();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public float percentDone() {
		return image.percentDone();
	}
}
