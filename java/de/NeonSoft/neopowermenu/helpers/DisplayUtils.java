package de.NeonSoft.neopowermenu.helpers;

import android.content.Context;

import android.content.res.Resources;

import android.graphics.Bitmap;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Matrix;

import android.util.DisplayMetrics;

import android.util.Log;

import android.view.Display;

import android.view.Surface;

import android.view.SurfaceControl;

import android.view.WindowManager;


public class DisplayUtils {


		public static int getPxFromDp(Resources res, int size) {


				return (int) (size * res.getDisplayMetrics().density + 0.5f);


		}


		public static int getDominantColorByPixelsSampling(Bitmap bitmap, int rows, int cols) {


// ------------------------------------

// por amostragem de pixels - by serajr

// ------------------------------------


// --------------------------------------------

// ex.: 6 linhas e 6 colunas (x = início e fim) 

// --------------------------------------------



// x-----

// ------

// ------

// ------

// ------

// ------

// -----x


// --------------------------------------------------------------------------------------

// método original: getDominantColor()

// http://dxr.mozilla.org/mozilla-central/source/mobile/android/base/gfx/BitmapUtils.java

// --------------------------------------------------------------------------------------


				int width = bitmap.getWidth();

				int height = bitmap.getHeight();

				int xPortion = width / cols;

				int yPortion = height / rows;

				int maxBin = -1;

				float[] hsv = new float[3];

				int[] colorBins = new int[36];

				float[] sumHue = new float[36];

      	float[] sumSat = new float[36];

      	float[] sumVal = new float[36];



				for (int row = 0; row <= rows; row++) {


						for (int col = 0; col <= cols; col++) {



//Log.d("rows_cols", (row > 0 ? yPortion * row : 0) + " | " + (col > 0 ? xPortion * col : 0));


// obtém o pixel da porção x e y

								int pixel = bitmap.getPixel(

										col > 0 ? (xPortion * col) - 1 : 0,

										row > 0 ? (yPortion * row) - 1 : 0);


								Color.colorToHSV(pixel, hsv);


								int bin = (int) Math.floor(hsv[0] / 10.0f);


								sumHue[bin] = sumHue[bin] + hsv[0];

								sumSat[bin] = sumSat[bin] + hsv[1];

								sumVal[bin] = sumVal[bin] + hsv[2];


								colorBins[bin]++;


								if (maxBin < 0 || colorBins[bin] > colorBins[maxBin])

										maxBin = bin;


						}

				}


				if (maxBin < 0)

						return Color.argb(255, 255, 255, 255);


				hsv[0] = sumHue[maxBin] / colorBins[maxBin];

				hsv[1] = sumSat[maxBin] / colorBins[maxBin];

				hsv[2] = sumVal[maxBin] / colorBins[maxBin];


				return Color.HSVToColor(hsv);


		}


		public static double getColorLightness(int color) {


				return 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;


		}


		public static int[] getRealScreenDimensions(Context context) {


				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

				Display display = wm.getDefaultDisplay();

				DisplayMetrics metrics = new DisplayMetrics();

				display.getRealMetrics(metrics);


				return new int[] { metrics.widthPixels, metrics.heightPixels };


		}


		public static Bitmap takeSurfaceScreenshot(Context context) {


				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

				Display display = wm.getDefaultDisplay();

				DisplayMetrics metrics = new DisplayMetrics();

				Matrix displayMatrix = new Matrix();


				Bitmap screenBitmap = null;


				display.getRealMetrics(metrics);

				float[] dims = { metrics.widthPixels, metrics.heightPixels };

				float degrees = getDegreesForRotation(display.getRotation());

				boolean requiresRotation = (degrees > 0);


				if (requiresRotation) {


// Get the dimensions of the device in its native orientation

						displayMatrix.reset();

						displayMatrix.preRotate(-degrees);

						displayMatrix.mapPoints(dims);

						dims[0] = Math.abs(dims[0]);

						dims[1] = Math.abs(dims[1]);


				}



				screenBitmap = SurfaceControl.screenshot((int) dims[0], (int) dims[1]);



// possível app que precisa de segurança rodando, ou

// o context não tem previlégios suficientes par tal 

				if (screenBitmap == null) {



// informa e retorna

						Log.i("NPM", "Cannot take surface screenshot! Skipping blur feature!!");

						return null;



				}



				if (requiresRotation) {



						// rotaciona

						Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);

						Canvas canvas = new Canvas(bitmap);

						canvas.translate(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

						canvas.rotate(360f - degrees);

						canvas.translate(-dims[0] / 2, -dims[1] / 2);

						canvas.drawBitmap(screenBitmap, 0, 0, null);

						canvas.setBitmap(null);

						screenBitmap = bitmap;



				}



// mutável

				Bitmap mutable = screenBitmap.copy(Bitmap.Config.ARGB_8888, true);



				// optimizações

				mutable.setHasAlpha(false);

				mutable.prepareToDraw();



				// retorna

				return mutable;



		}


		public static Bitmap takeSurfaceScreenshot(Context context, int downScale) {


				WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

				Display display = wm.getDefaultDisplay();

				DisplayMetrics metrics = new DisplayMetrics();

				Matrix displayMatrix = new Matrix();


				Bitmap screenBitmap = null;


				display.getRealMetrics(metrics);

				float[] dims = { metrics.widthPixels / downScale, metrics.heightPixels / downScale };

				float degrees = getDegreesForRotation(display.getRotation());

				boolean requiresRotation = (degrees > 0);


				if (requiresRotation) {


// Get the dimensions of the device in its native orientation

						displayMatrix.reset();

						displayMatrix.preRotate(-degrees);

						displayMatrix.mapPoints(dims);

						dims[0] = Math.abs(dims[0]);

						dims[1] = Math.abs(dims[1]);


				}



				screenBitmap = SurfaceControl.screenshot((int) dims[0], (int) dims[1]);



// possível app que precisa de segurança rodando, ou

// o context não tem previlégios suficientes par tal 

				if (screenBitmap == null) {


// informa e retorna

						Log.i("NPM", "Cannot take surface screenshot! Skipping blur feature!!");

						return null;


				}



				if (requiresRotation) {



// rotaciona

						Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels / downScale, metrics.heightPixels / downScale, Bitmap.Config.ARGB_8888);

						Canvas canvas = new Canvas(bitmap);

						canvas.translate(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

						canvas.rotate(360f - degrees);

						canvas.translate(-dims[0] / 2, -dims[1] / 2);

						canvas.drawBitmap(screenBitmap, 0, 0, null);

						canvas.setBitmap(null);

						screenBitmap = bitmap;



				}



				// mutável

				Bitmap mutable = screenBitmap.copy(Bitmap.Config.ARGB_8888, true);



				// optimizações

				mutable.setHasAlpha(false);

				mutable.prepareToDraw();



				// retorna

				return mutable;



		}


		private static float getDegreesForRotation(int value) {


				switch (value) {


						case Surface.ROTATION_90:

								return 90f;


						case Surface.ROTATION_180:

								return 180f;


						case Surface.ROTATION_270:

								return 270f;


				}


				return 0f;



		}

}


