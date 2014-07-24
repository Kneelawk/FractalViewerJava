package com.pommert.jedidiah.fractalviewerjava.fractals.julia1;

import com.pommert.jedidiah.fractalviewerjava.fractals.FractalGenerator;
import com.pommert.jedidiah.fractalviewerjava.fractals.GenerationFailedException;
import com.pommert.jedidiah.fractalviewerjava.output.Colour;
import com.pommert.jedidiah.fractalviewerjava.output.Out;

public class Julia1Generator extends FractalGenerator {

	float percentDone = 0;

	@Override
	public String getName() {
		return "Julia1";
	}

	@Override
	public String generate(int width, int height, int seed, String[] args)
			throws GenerationFailedException {
		if (args.length < 1)
			throwUsage("");
		float w = Float.parseFloat(args[0]);

		float mx = 0;
		float my = 0;

		int maxIterations = 300;

		float ca = -0.7f;
		float cb = 0.27015f;

		float h = (8f * w * height) / (9f * width);

		boolean crossHairs = false;

		boolean box = false;
		float bcx = 0;
		float bcy = 0;
		float bw = 0.5f;
		float bh = 0.26f;

		if (args.length > 1) {
			for (int index = 1; index < args.length; index++) {
				if (args[index].equalsIgnoreCase("-ch")) {
					crossHairs = true;
				} else if (args[index].equalsIgnoreCase("-ci")) {
					checkLength(args, index,
							"-ci is followed by the constant's imaginary part.\n");
					cb = Float.parseFloat(args[++index]);
				} else if (args[index].equalsIgnoreCase("-cr")) {
					checkLength(args, index,
							"-cr is followed by the constant's real part.\n");
					ca = Float.parseFloat(args[++index]);
				} else if (args[index].equalsIgnoreCase("-cx")) {
					checkLength(args, index,
							"-cx is followed by the generation plane's center x.\n");
					mx = Float.parseFloat(args[++index]);
				} else if (args[index].equalsIgnoreCase("-cy")) {
					checkLength(args, index,
							"-cy is followed by te generation plane's center y.\n");
					my = Float.parseFloat(args[++index]);
				} else if (args[index].equalsIgnoreCase("-it")) {
					checkLength(args, index,
							"-it is followed by the max iterations.\n");
					maxIterations = Integer.parseInt(args[++index]);
				} else if (args[index].equalsIgnoreCase("-b")) {
					checkLength(
							args,
							index + 2,
							"-b is followed by <box center x>, <box center y>, <box width>, and sometimes <box height>.\n");
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
				} else if (args[index].equalsIgnoreCase("-h")) {
					checkLength(args, index,
							"-h is followed by the generation plane's height.\n");
					h = Float.parseFloat(args[++index]);
				}
			}
		}

		System.out.printf("Julia1 Generator settings:\n"
				+ "generation plane width: %s,\n"
				+ "generation plane height: %s,\n"
				+ "generation plane x center: %s,\n"
				+ "generation plane y center: %s,\nmax iterations: %d,\n"
				+ "real part of complex constant: %s,\n"
				+ "imaginary part of complex constant: %s.\n", w, h, mx, my,
				maxIterations, ca, cb);

		float xmin = -(w / 2) + mx;
		float ymin = -(h / 2) + my;
		float dx = w / width;
		float dy = h / height;

		float pmax = width * height;
		float pcurrent = 0;

		// a is real, b is imaginary
		float a, b;

		float x;
		float y = ymin;
		for (int j = 0; j < height; j++) {
			x = xmin;
			for (int i = 0; i < width; i++) {
				a = x;
				b = y;

				int n;

				for (n = 0; n < maxIterations; n++) {
					float aa = a * a;
					float bb = b * b;
					// float aaa = aa * a;
					// float bbb = bb * b;
					// float threeabb = 3.0f * a * bb;
					// float threeaab = 3.0f * aa * a;
					float twoab = 2.0f * a * b;

					a = aa - bb + ca;
					b = twoab + cb;

					// a = aaa - threeabb + ca;
					// b = threeaab - bbb;

					if ((aa + bb) > 4)
						break;
				}

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
				} else if (n < maxIterations) {
					Out.setPixel(i, j, Colour.fromHSB((n * 1.1f % 256f) / 256f,
							1.0f, (n * 6f % 256f) / 256f));
				} else {
					Out.setPixel(i, j, new Colour(0));
				}

				x += dx;
				pcurrent = i + j * width;
				percentDone = (pcurrent / pmax) * 100;
			}
			y += dy;
		}
		return "Julia1 "
				+ w
				+ "w "
				+ h
				+ "h "
				+ mx
				+ "x "
				+ my
				+ "y "
				+ (crossHairs ? "ch " : "")
				+ (box ? "box(" + bcx + "," + bcy + ")(" + bw + "x" + bh + ") "
						: "") + maxIterations + "it " + ca + "cr " + cb + "ci";
	}

	public float percentDone() {
		return percentDone;
	}

	private static void checkLength(String[] args, int index, String message)
			throws GenerationFailedException {
		if (args.length <= index + 1) {
			throwUsage(message);
		}
	}

	private static void throwUsage(String message)
			throws GenerationFailedException {
		throw new GenerationFailedException(
				message
						+ "Usage: Julia1 <generated plane width>[ -h <generated plane height>][ -cx <center x>][ -cy <center y>][ -it <max iterations>][ -ch][ -b <box center x> <box center y> <box width>[ <box height]][ -cr <constant real>][ -ci <constant imaginary>]\n"
						+ "The -ch option forces the generator to put a red cross hair at 0,0.\n"
						+ "The -b option forces the generator to put a white box outline at <box center x> and <box center y>,\n"
						+ "\twith a width of <box center width>, and a height of <box center height>,\n"
						+ "\tor a calculated height based on the image width and height if not box height is specified.");
	}
}
