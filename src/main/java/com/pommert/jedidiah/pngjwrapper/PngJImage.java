package com.pommert.jedidiah.pngjwrapper;

import java.io.InputStream;
import java.io.OutputStream;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;

import com.pommert.jedidiah.fractalviewerjava.output.Colour;

public class PngJImage {
	public Colour[][] pixels;
	public ImageInfo info;
	protected float percentDone = 0;

	public PngJImage() {
	}

	public PngJImage(int rows, int cols, int bitdepth, boolean alpha) {
		pixels = new Colour[rows][cols];
		info = new ImageInfo(cols, rows, bitdepth, alpha);
	}

	public PngJImage(int rows, int cols, int bitdepth, boolean alpha,
			boolean grayscale, boolean indexed) {
		pixels = new Colour[rows][cols];
		info = new ImageInfo(rows, cols, bitdepth, alpha, grayscale, indexed);
	}

	public PngReader load(InputStream stream) {
		percentDone = 0;
		PngReader reader = new PngReader(stream);
		info = reader.imgInfo;
		pixels = new Colour[info.rows][info.cols];
		for (int y = 0; y < info.rows; y++) {
			IImageLine line = reader.readRow(y);
			pixels[y] = readImageLine(line, info);
			percentDone = (((float) y) / ((float) info.rows)) * 100f;
		}
		return reader;
	}

	public PngWriter save(OutputStream stream) {
		percentDone = 0;
		PngWriter writer = new PngWriter(stream, info);
		for (int y = 0; y < info.rows; y++) {
			IImageLine line = writeImageLine(pixels[y], info);
			writer.writeRow(line, y);
			percentDone = (((float) y) / ((float) info.rows)) * 100f;
		}
		return writer;
	}

	public void setPixel(int x, int y, Colour color) {
		pixels[y][x] = color;
	}

	public Colour getPixel(int x, int y) {
		return pixels[y][x];
	}

	/**
	 * Convert line data into an array of Color[]
	 * 
	 * @param line
	 *            the IImageLine to be parse
	 * @param info
	 *            the ImageInfo describing the image
	 * @return the array of Color[]
	 */
	public static Colour[] readImageLine(IImageLine line, ImageInfo info) {
		Colour[] data = new Colour[info.cols];
		int[] rawData = null;
		if (line instanceof ImageLineInt) {
			rawData = ((ImageLineInt) line).getScanline();
		} else if (line instanceof ImageLineByte) {
			byte[] b = ((ImageLineByte) line).getScanline();
			rawData = new int[b.length];
			for (int i = 0; i < b.length; i++) {
				rawData[i] = b[i];
			}
		}
		if (info.channels == 4) {
			for (int i = 0; i < info.cols; i++) {
				data[i] = new Colour(rawData[i * info.channels], rawData[i
						* info.channels + 1], rawData[i * info.channels + 2],
						rawData[i * info.channels + 3]);
			}
		} else if (info.channels == 3) {
			for (int i = 0; i < info.cols; i++) {
				data[i] = new Colour(rawData[i * info.channels], rawData[i
						* info.channels + 1], rawData[i * info.channels + 2]);
			}
		} else if (info.channels == 2) {
			for (int i = 0; i < info.cols; i++) {
				data[i] = new Colour(rawData[i * info.channels], rawData[i
						* info.channels], rawData[i * info.channels], rawData[i
						* info.channels + 1]);
			}
		} else if (info.channels == 1) {
			for (int i = 0; i < info.cols; i++) {
				data[i] = new Colour(rawData[i], rawData[i], rawData[i]);
			}
		} else {
			throw new RuntimeException("Unknown Channel Format: "
					+ info.channels);
		}
		return data;
	}

	/**
	 * Convert an array of Color[] into an ImageLineInt.
	 * 
	 * @param pixels
	 *            the array to be converted.
	 * @param info
	 *            data on things like the number of columns and the number of
	 *            channels.
	 * @return an ImageLineInt containing the pixel data.
	 */
	public static ImageLineInt writeImageLine(Colour[] pixels, ImageInfo info) {
		ImageLineInt line = null;
		int[] raw = new int[info.cols * info.channels];
		int c = info.channels;
		if (c == 4) {
			for (int i = 0; i < info.cols; i++) {
				if (pixels[i] == null)
					continue;
				raw[i * c] = pixels[i].getRed();
				raw[i * c + 1] = pixels[i].getGreen();
				raw[i * c + 2] = pixels[i].getBlue();
				raw[i * c + 3] = pixels[i].getAlpha();
			}
		} else if (c == 3) {
			for (int i = 0; i < info.cols; i++) {
				if (pixels[i] == null)
					continue;
				raw[i * c] = pixels[i].getRed();
				raw[i * c + 1] = pixels[i].getGreen();
				raw[i * c + 2] = pixels[i].getBlue();
			}
		} else if (c == 2) {
			for (int i = 0; i < info.cols; i++) {
				if (pixels[i] == null)
					continue;
				raw[i * c] = (pixels[i].getRed() + pixels[i].getGreen() + pixels[i]
						.getBlue()) / 3;
				raw[i * c + 1] = pixels[i].getAlpha();
			}
		} else if (c == 1) {
			for (int i = 0; i < info.cols; i++) {
				if (pixels[i] == null)
					continue;
				raw[i * c] = (pixels[i].getRed() + pixels[i].getGreen() + pixels[i]
						.getBlue()) / 3;
			}
		}
		line = new ImageLineInt(info, raw);
		return line;
	}

	public float percentDone() {
		return percentDone;
	}
}
