package com.pommert.jedidiah.fractalviewerjava.output;

import java.util.ArrayList;

import com.pommert.jedidiah.fractalviewerjava.FractalViewerJava;
import com.pommert.jedidiah.fractalviewerjava.output.lwjgl.LWJGLPixelOutput;

public class Out {
	private static ArrayList<PixelOutput> outList;

	public static void init(int width, int height) {
		outList = new ArrayList<PixelOutput>();
		addPixelOutput(new ImagePixelOutput(), width, height);
		if (FractalViewerJava.display) {
			addPixelOutput(new LWJGLPixelOutput(), width, height);
		}
	}

	public static void addPixelOutput(PixelOutput out, int width, int height) {
		out.init(width, height);
		outList.add(out);
	}

	public static void setPixel(int x, int y, Colour color) {
		for (PixelOutput out : outList) {
			out.setPixel(x, y, color);
		}
	}

	public static float percentDone() {
		float avrg = 0;
		for (PixelOutput out : outList) {
			avrg += out.percentDone();
		}
		return avrg / outList.size();
	}

	public static void finish(String name) {
		for (PixelOutput out : outList) {
			out.finish(name);
		}
	}
}
