package com.pommert.jedidiah.fractalviewerjava.fractals.mandelbrot1;

import java.util.Random;

import com.pommert.jedidiah.fractalviewerjava.fractals.FractalGenerator;
import com.pommert.jedidiah.fractalviewerjava.fractals.GenerationFailedException;
import com.pommert.jedidiah.fractalviewerjava.output.Colour;
import com.pommert.jedidiah.fractalviewerjava.output.Out;

public class Mandelbrot1Generator extends FractalGenerator {

	float xmin = -3;
	float ymin = -1.4f;
	float w = 5;
	float h = 1.8f;

	float percentDone = 0;

	@Override
	public String getName() {
		return "Mandelbrot1";
	}

	@Override
	public String generate(int width, int height, int seed, String[] args)
			throws GenerationFailedException {
		// h = (((float) height) / ((float) width)) * w;
		// ymin = -(h / 2f);

		if (args.length < 3) {
			throwUsage("");
		}

		float cx = Float.parseFloat(args[0]);
		float cy = Float.parseFloat(args[1]);
		w = Float.parseFloat(args[2]);
		h = (float) ((0.595294 * w * height) / width) * 1.6f;

		xmin = -(w / 2f) + cx;
		ymin = -(h / 2f) + cy;

		String colorScheme = "Hue";

		// Maximum number of iterations for each point on the complex plane
		int maxiterations = 100;

		float hm = 3.3f, bm = 16;

		boolean crossHairs = false;

		boolean box = false;
		float bcx = 0;
		float bcy = 0;
		float bw = 0.5f;
		float bh = 0.26f;

		if (args.length > 3) {
			for (int index = 3; index < args.length; index++) {
				if (args[index].equalsIgnoreCase("-h")) {
					if (args.length > index + 1) {
						h = Float.parseFloat(args[++index]);
					} else {
						throwUsage("-h is followed by the generation plain's height.\n");
					}
				} else if (args[index].equalsIgnoreCase("-it")) {
					if (args.length > index + 1) {
						maxiterations = Integer.parseInt(args[++index]);
					} else {
						throwUsage("-it is followed by the max number of iterations.\n");
					}
				} else if (args[index].equalsIgnoreCase("-ch")) {
					crossHairs = true;
				} else if (args[index].equalsIgnoreCase("-b")) {
					if (args.length > index + 3) {
						box = true;
						bcx = Float.parseFloat(args[++index]);
						bcy = Float.parseFloat(args[++index]);
						bw = Float.parseFloat(args[++index]);
						bh = (float) ((0.595294 * bw * height) / width) * 1.6f;
						if (args.length > index + 1) {
							if (args[index + 1].startsWith("-"))
								continue;
							bh = Float.parseFloat(args[++index]);
						}
					} else {
						throwUsage("-b is followed by <box center x>, <box center y>, <box width>, and sometimes <box height>.\n");
					}
				} else if (args[index].equalsIgnoreCase("-c")) {
					if (args.length > index + 1) {
						colorScheme = args[++index];
						if (colorScheme.equalsIgnoreCase("hue")) {
							if (args.length > index + 1) {
								if (args[index + 1].startsWith("-"))
									continue;
								hm = Float.parseFloat(args[++index]);
								if (args.length > index + 1) {
									if (args[index + 1].startsWith("-"))
										continue;
									bm = Float.parseFloat(args[++index]);
								}
							}
						}
					} else {
						throwUsage("-c is followed by the color scheme and optional arguments.\n");
					}
				}
			}
		}

		colorScheme = colorScheme.toLowerCase();

		if (!findIn(colorScheme, "segment", "sriped", "random", "hue")) {
			colorScheme = "hue";
		}

		Random rng = new Random(seed);
		int randomOffset = rng.nextInt();

		System.out.printf("Mandelbrot1 Generator settings:\n"
				+ "generated plane xmin: %s,\ngenerated plane ymin %s,\n"
				+ "generated plane width: %s,\n"
				+ "generated plane height: %s,\nmax iterations: %d,\n"
				+ "color scheme: %s,\nhue multiplier: %s,\n"
				+ "brightness multiplier: %s,\nseed: %d,\n"
				+ "random offset: %d.\n", xmin, ymin, w, h, maxiterations,
				colorScheme, hm, bm, seed, randomOffset);

		System.out.println("Starting drawing...");

		// Calculate amount we increment x,y for each pixel
		double dx = w / (width);
		double dy = h / (height);

		float pmax = width * height;
		float pcurrent = 0;

		// x goes from xmin to xmax
		// y goes from ymin to ymax
		// Start y
		double x;
		double y = ymin;

		for (int j = 0; j < height; j++) {
			// Start x
			x = xmin;
			for (int i = 0; i < width; i++) {

				// Now we test, as we iterate z = z^2 + cm does z tend towards
				// infinity?
				// a is the real part of z, b is the imaginary part of z.
				float a = (float) x;
				float b = (float) y;
				// Complex z = new Complex(x, y);

				int n = 0;
				while (n < maxiterations) {
					float aa = a * a;
					float bb = b * b;
					float twoab = 2.0f * a * b;
					// Notice how a = aa - bb + x instead of a = aa - bb + ca?
					// And how b = twoab + y instead of b = twoab + cb? This is
					// the difference between a mandelbrot set and a julia set.
					a = aa - bb + ((float) x);
					b = twoab + ((float) y);

					// z = z.multiply(z).add(new Complex(x, y));
					// double re = z.getReal();
					// double im = z.getImaginary();

					// Infinty in our finite world is simple, let's just
					// consider it 16
					if (aa + bb > 16.0) {
						break; // Bail
					}
					n++;
				}

				// We color each pixel based on how long it takes to get to
				// infinity
				float maxbx = 0, minbx = 0, maxby = 0, minby = 0;
				if (box) {
					maxbx = bcx + (bw / 2);
					minbx = bcx - (bw / 2);
					maxby = bcy + (bh / 2);
					minby = bcy - (bh / 2);
				}
				if (box
						&& ((y <= maxby && maxby < (y + dy) && minbx <= x && x <= maxbx)
								|| (y <= minby && minby < (y + dy)
										&& minbx <= x && x <= maxbx)
								|| (x <= maxbx && maxbx < (x + dx)
										&& minby <= y && y <= maxby) || (x <= minbx
								&& minbx < (x + dx) && minby <= y && y <= maxby))) {
					Out.setPixel(i, j, new Colour(255));
				} else if (crossHairs
						&& ((x <= 0 && 0 < (x + dx)) || (y <= 0 && 0 < (y + dy)))) {
					Out.setPixel(i, j, new Colour(255, 0, 0));
				} else if (n >= maxiterations) {
					// If we never got there, let's pick the color black
					Out.setPixel(i, j, new Colour(0));
				} else {
					if (colorScheme.equalsIgnoreCase("segment")) {
						// Segment Color Scheme
						Out.setPixel(i, j, new Colour(
								(((n * 18) >> 0) % 0xF) * 16,
								(((n * 18) >> 4) % 0xF) * 16,
								(((n * 18) >> 8) % 0xF) * 16));
					}

					else if (colorScheme.equalsIgnoreCase("striped")) {
						// Striped Color Scheme
						Out.setPixel(i, j, new Colour((n * 16 % 256), (int) a,
								(int) b));
					}

					else if (colorScheme.equalsIgnoreCase("random")) {
						// Random Band Color Scheme
						rng.setSeed(n + randomOffset);
						Out.setPixel(i, j,
								new Colour(rng.nextInt(222), rng.nextInt(222),
										rng.nextInt(222)));
					}

					else {
						// Hue Color Scheme
						Out.setPixel(
								i,
								j,
								Colour.fromHSB((n * hm % 256f) / 256f, 1.0f, (n
										* bm % 256f) / 256f));
					}
				}
				x += dx;
				pcurrent = i + j * width;
				percentDone = (pcurrent / pmax) * 100;
			}
			y += dy;
		}

		return "Mandelbrot1 "
				+ cx
				+ "x "
				+ cy
				+ "y "
				+ w
				+ "w "
				+ h
				+ "h "
				+ maxiterations
				+ "it "
				+ (crossHairs ? "ch " : "")
				+ (box ? "box(" + bcx + "," + bcy + ")(" + bw + "x" + bh + ") "
						: "")
				+ "cs:"
				+ colorScheme
				+ (colorScheme.equalsIgnoreCase("random") ? " seed:" + seed
						: "")
				+ (colorScheme.equalsIgnoreCase("hue") ? " " + hm + "hm " + bm
						+ "bm" : "");
	}

	public float percentDone() {
		return percentDone;
	}

	private static void throwUsage(String message)
			throws GenerationFailedException {
		throw new GenerationFailedException(
				message
						+ "Usage: Mandelbrot1 <center x> <center y> <generated plane width>[ -h <generated plane height>][ -it <max iterations>][ -ch][ -b <box center x> <box center y> <box width>[ <box height>]][ -c <color scheme>[ <args>]]\n"
						+ "The -ch option forces the generator to put a red cross hair at 0,0.\n"
						+ "The -b option forces the generator to put a white box outline at <box center x> and <box center y>,\n"
						+ "\twith a width of <box center width>, and a height of <box center height>,\n"
						+ "\tor a calculated height based on the image width and height if not box height is specified.\n"
						+ "Color Schemes:\n"
						+ "Segment: bands of red fading into green and eventually into white.\n"
						+ "Hue: (default) bands with colors determined by the HSB Hue scale.\n"
						+ "\tHas optional arguments of <hue multiplier> and <brightness multiplier>.\n"
						+ "Striped: Stripes flowing down the bands of colors. This effect is caused by\n"
						+ "\tthe use of both halves of the complex number in the equation for the color.\n"
						+ "Random: Stripes have random colors. Use the seed for a constant color selection.");
	}

	private static boolean findIn(String str, String... in) {
		for (String to : in) {
			if (to.equalsIgnoreCase(str))
				return true;
		}
		return false;
	}
}
