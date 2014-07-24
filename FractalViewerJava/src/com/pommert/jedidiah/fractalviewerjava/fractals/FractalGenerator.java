package com.pommert.jedidiah.fractalviewerjava.fractals;

import java.util.HashMap;

import com.pommert.jedidiah.fractalviewerjava.fractals.julia1.Julia1Generator;
import com.pommert.jedidiah.fractalviewerjava.fractals.mandelbrot1.Mandelbrot1Generator;

public abstract class FractalGenerator {
	public static HashMap<String, FractalGenerator> fractalGenerators = createFractalGenerators();

	private static HashMap<String, FractalGenerator> createFractalGenerators() {
		HashMap<String, FractalGenerator> gen = new HashMap<String, FractalGenerator>();
		afgthm(gen, new Mandelbrot1Generator());
		afgthm(gen, new Julia1Generator());
		return gen;
	}

	// AFGTHM (Add Fractal Generator To Hash Map)
	private static void afgthm(HashMap<String, FractalGenerator> map,
			FractalGenerator gen) {
		map.put(gen.getName(), gen);
	}

	public static FractalGenerator getFractalGenerator(String name) {
		return fractalGenerators.get(name);
	}

	public abstract String getName();

	public abstract String generate(int width, int height, int seed, String[] args) throws GenerationFailedException;

	public float percentDone() {
		return 50;
	}
}
