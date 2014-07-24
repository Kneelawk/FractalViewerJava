package com.pommert.jedidiah.fractalviewerjava;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

import com.pommert.jedidiah.fractalviewerjava.fractals.FractalGenerator;
import com.pommert.jedidiah.fractalviewerjava.fractals.GenerationFailedException;
import com.pommert.jedidiah.fractalviewerjava.output.Out;

public class FractalViewerJava {
	public static FractalGenerator gen;

	protected static boolean running = false;

	public static void main(String[] args) {
		try {
			// add the current directory to the java library path
			try {
				addLibraryDir(new File(FractalViewerJava.class
						.getProtectionDomain().getCodeSource().getLocation()
						.getPath()).getParent());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			String engine = null, addName = null;
			int seed = 0, width = 1280, height = 720;
			boolean hasSeed = false;
			String[] extraArgs = new String[0];

			// if the first argument is "--list" then print a list of the
			// available fractal generators
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("--list")) {
					System.out.println(Arrays
							.toString(
									FractalGenerator.fractalGenerators.keySet()
											.toArray(new String[0]))
							.replace("[", "").replace("]", ""));
					return;
				}
			}

			// print the program usage if the number of arguments is less than 3
			if (args.length < 3) {
				throwUsage("");
			}

			// parse the image width and height
			width = Integer.parseInt(args[0]);
			height = Integer.parseInt(args[1]);

			// parse additional arguments like the generator type, name, and
			// seed
			if (args.length > 2) {
				for (int index = 2; index < args.length; index++) {
					if (args[index].equalsIgnoreCase("-s")) {
						if (args.length > index + 1) {
							seed = Integer.parseInt(args[++index]);
							hasSeed = true;
						} else {
							throwUsage("-s is followed by a seed number!\n");
						}
					} else if (args[index].equalsIgnoreCase("-n")) {
						if (args.length > index + 1) {
							addName = args[++index];
						} else {
							throwUsage("-n is followed by a name!\n");
						}
					} else {
						engine = args[index];
						if (args.length > index + 1) {
							extraArgs = Arrays.copyOfRange(args, ++index,
									args.length);
						}
						break;
					}
				}
			}

			// print some info on the generator arguments
			System.out
					.printf("Fractal Engine: %s, width: %d, height %d, name: %s, has seed: %b, seed: %d, engine spacific args: %s\n",
							engine, width, height, addName, hasSeed, seed,
							Arrays.toString(extraArgs));
			// init Out (adds outputs like the image output to the list of
			// outputs)
			Out.init(width, height);
			// get the fractal generator with the specified engine name
			gen = FractalGenerator.getFractalGenerator(engine);
			if (gen == null) {
				System.err.println("No fractal engine called: " + engine);
				return;
			}

			// start an info thread (prints percent done generating and saving)
			running = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (running) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(gen.percentDone() + "% generating, "
								+ Out.percentDone() + "% saving");
					}
				}
			}).start();

			// generate the fractal
			System.out.println("Generating...");
			String name;
			try {
				name = gen.generate(width, height,
						(hasSeed ? 0 : new Random().nextInt()), extraArgs);
			} catch (GenerationFailedException e) {
				running = false;
				System.err.println("Fractal generation failed: "
						+ e.getMessage());
				if (e.getCause() != null) {
					System.err.print("Caused by: ");
					e.getCause().printStackTrace(System.err);
				}
				return;
			} catch (Throwable t) {
				running = false;
				throw new RuntimeException(t);
			}
			System.out.println("Done generating.");

			// collect garbage
			System.out.println("Collecting garbage...");
			System.gc();
			System.out.println("Done collecting garbage.");

			// save the image
			System.out.println("Saving...");
			Out.finish((addName != null && !addName.equals("") ? addName + " "
					: "") + name + " " + width + "x" + height);

			// stop the info thread
			running = false;
			System.out.println("Done.");
		} catch (IllegalArgumentException iae) {
			System.err.println(iae.toString());
		}
	}

	public static void addLibraryDir(String s) throws IOException {
		try {
			// This enables the java.library.path to be modified at runtime
			// From a Sun engineer at
			// http://forums.sun.com/thread.jspa?threadID=707176
			//
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
			System.setProperty("java.library.path",
					System.getProperty("java.library.path")
							+ File.pathSeparator + s);
		} catch (IllegalAccessException e) {
			throw new IOException(
					"Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException(
					"Failed to get field handle to set library path");
		}
	}

	private static void throwUsage(String message) {
		throw new IllegalArgumentException(
				message
						+ "\nUsage: --list\tList fractal engines.\n"
						+ "Usage: <width> <height> [-n <name>] [-s <seed>] <fractal engine> [<engine spacific arguments>]");
	}
}
