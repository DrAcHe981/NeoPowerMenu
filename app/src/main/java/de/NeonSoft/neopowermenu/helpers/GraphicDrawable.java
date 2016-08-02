package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;

/**
 * @author DrAcHe981
 * @datetime 26 Apr 2016, 23:14
 */
public class GraphicDrawable extends ShapeDrawable {

		private final Context context;
    private final Paint textPaint;
    private final Paint borderPaint;
    private static final float SHADE_FACTOR = 0.9f;
		private final Bitmap graphic;
    private final int color;
    private final RectShape shape;
    private final int height;
    private final int width;
    private final int fontSize;
    private final float radius;
    private final int borderThickness;

    private GraphicDrawable(Builder builder) {
        super(builder.shape);

				context = builder.context;
				
        // shape properties
        shape = builder.shape;
        height = builder.height;
        width = builder.width;
        radius = builder.radius;

        // graphic and color
				graphic = builder.graphic;
        color = builder.color;
				
        // text paint settings
        fontSize = builder.fontSize;
        textPaint = new Paint();
        textPaint.setColor(builder.textColor);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(builder.isBold);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(builder.font);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStrokeWidth(builder.borderThickness);

        // border paint settings
        borderThickness = builder.borderThickness;
        borderPaint = new Paint();
        borderPaint.setColor(getDarkerShade(color));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderThickness);

        // drawable paint color
        Paint paint = getPaint();
        paint.setColor(color);

    }

    private int getDarkerShade(int color) {
        return Color.rgb((int)(SHADE_FACTOR * Color.red(color)),
												 (int)(SHADE_FACTOR * Color.green(color)),
												 (int)(SHADE_FACTOR * Color.blue(color)));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect r = getBounds();


        // draw border
        if (borderThickness > 0) {
            drawBorder(canvas);
        }

        int count = canvas.save();
        canvas.translate(r.left, r.top);

        // draw graphic
				Rect r2 = new Rect();
				int px = (int) helper.convertDpToPixel(20,context);
				r2.set(r.left+px,r.top+px,r.right-px,r.bottom-px);
				if(graphic != null) canvas.drawBitmap(graphic,null,r2,null);
				
        canvas.restoreToCount(count);

    }

    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset(borderThickness/2, borderThickness/2);

        if (shape instanceof OvalShape) {
            canvas.drawOval(rect, borderPaint);
        }
        else if (shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, radius, radius, borderPaint);
        }
        else {
            canvas.drawRect(rect, borderPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    public static IShapeBuilder builder() {
        return new Builder();
    }

    public static class Builder implements IConfigBuilder, IShapeBuilder, IBuilder {

				public Context context;
				
				private Bitmap graphic;

        private int color;

        private int borderThickness;

        private int width;

        private int height;

        private Typeface font;

        private RectShape shape;

        public int textColor;

        private int fontSize;

        private boolean isBold;

        private boolean toUpperCase;

        public float radius;
				
        private Builder() {
						context = null;
						graphic = null;
            color = Color.GRAY;
            textColor = Color.WHITE;
            borderThickness = 0;
            width = -1;
            height = -1;
            shape = new RectShape();
            font = Typeface.create("sans-serif-light", Typeface.NORMAL);
            fontSize = -1;
            isBold = false;
            toUpperCase = false;
        }

				public IConfigBuilder setContext(Context context) {
						this.context = context;
						return this;
				}
				
        public IConfigBuilder width(int width) {
            this.width = width;
            return this;
        }

        public IConfigBuilder height(int height) {
            this.height = height;
            return this;
        }

        public IConfigBuilder textColor(int color) {
            this.textColor = color;
            return this;
        }

        public IConfigBuilder withBorder(int thickness) {
            this.borderThickness = thickness;
            return this;
        }

        public IConfigBuilder useFont(Typeface font) {
            this.font = font;
            return this;
        }

        public IConfigBuilder fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public IConfigBuilder bold() {
            this.isBold = true;
            return this;
        }

        public IConfigBuilder toUpperCase() {
            this.toUpperCase = true;
            return this;
        }

        @Override
        public IConfigBuilder beginConfig() {
            return this;
        }

        @Override
        public IShapeBuilder endConfig() {
            return this;
        }

        @Override
        public IBuilder rect() {
            this.shape = new RectShape();
            return this;
        }

        @Override
        public IBuilder round() {
            this.shape = new OvalShape();
            return this;
        }

        @Override
        public IBuilder roundRect(int radius) {
            this.radius = radius;
            float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
            this.shape = new RoundRectShape(radii, null, null);
            return this;
        }

        @Override
        public GraphicDrawable buildRect(Bitmap graphic, int color) {
            rect();
            return build(graphic, color);
        }

        @Override
        public GraphicDrawable buildRoundRect(Bitmap graphic, int color, int radius) {
            roundRect(radius);
            return build(graphic, color);
        }

        @Override
        public GraphicDrawable buildRound(Bitmap graphic, int color) {
            round();
            return build(graphic, color);
        }

        @Override
        public GraphicDrawable build(Bitmap graphic, int color) {
            this.color = color;
            this.graphic = graphic;
            return new GraphicDrawable(this);
        }
    }

    public interface IConfigBuilder {
				public IConfigBuilder setContext(Context context);
				
        public IConfigBuilder width(int width);

        public IConfigBuilder height(int height);

        public IConfigBuilder textColor(int color);

        public IConfigBuilder withBorder(int thickness);

        public IConfigBuilder useFont(Typeface font);

        public IConfigBuilder fontSize(int size);

        public IConfigBuilder bold();

        public IConfigBuilder toUpperCase();

        public IShapeBuilder endConfig();
    }

    public static interface IBuilder {

        public GraphicDrawable build(Bitmap graphic, int color);
    }

    public static interface IShapeBuilder {

        public IConfigBuilder beginConfig();

        public IBuilder rect();

        public IBuilder round();

        public IBuilder roundRect(int radius);

        public GraphicDrawable buildRect(Bitmap graphic, int color);

        public GraphicDrawable buildRoundRect(Bitmap graphic, int color, int radius);

        public GraphicDrawable buildRound(Bitmap graphic, int color);
    }
}
