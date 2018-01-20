package de.NeonSoft.neopowermenu.helpers;

/**
 * Created by naman on 18/03/15.
 */

import android.animation.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.*;
import android.os.*;
import android.util.*;
import android.view.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.*;

public class CircularRevealView extends ViewGroup {

    public static final int ANIMATION_REVEAL = 0;
    public static final int ANIMATION_HIDE = 2;

    private static final float SCALE = 8f;

    private View inkView;
    private int inkColor;
    private ShapeDrawable circle;
    private ViewPropertyAnimator animator;

    public CircularRevealView(Context context) {
        this(context, null);
    }

    public CircularRevealView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularRevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            //return;
        }

        inkView = new View(context);
        addView(inkView);

        circle = new ShapeDrawable(new OvalShape());

        helper.setBackground(inkView, circle);
        inkView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        inkView.layout(left, top, left + inkView.getMeasuredWidth(), top + inkView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        final float circleSize = (float) Math.sqrt(width * width + height * height) * 2f;
        final int size = (int) (circleSize / SCALE);
        final int sizeSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        inkView.measure(sizeSpec, sizeSpec);
    }

    public void redrawRevealView() {

    }

    public void reveal(final int x, final int y, final int color, Animator.AnimatorListener listener) {
        final int duration = getResources().getInteger(R.integer.rcv_animationDurationReveal);
        reveal(x, y, color, 0, duration, listener);
    }

    public void reveal(final int x, final int y, final int color, final int startRadius, long duration, final Animator.AnimatorListener listener) {
        if (color == inkColor) {
            return;
        }
        inkColor = color;

        if (animator != null) {
            animator.cancel();
        }

        circle.getPaint().setColor(color);
        inkView.setVisibility(View.VISIBLE);

        final float startScale = startRadius * 2f / inkView.getHeight();
        final float finalScale = calculateScale(x, y) * SCALE;

        prepareView(inkView, x, y, startScale);
        animator = inkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundColor(color);
                inkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(animator, ANIMATION_REVEAL);
        animator.start();
    }

    public void hide(final int x, final int y, final int color, final Animator.AnimatorListener listener) {
        final int duration = getResources().getInteger(R.integer.rcv_animationDurationHide);
        hide(x, y, color, 0, duration, listener);
    }

    public void hide(final int x, final int y, final int color, final int endRadius, final long duration, final Animator.AnimatorListener listener) {
        inkColor = Color.TRANSPARENT;

        if (animator != null) {
            animator.cancel();
        }

        inkView.setVisibility(View.VISIBLE);
        setBackgroundColor(color);

        final float startScale = calculateScale(x, y) * SCALE;
        final float finalScale = endRadius * SCALE / inkView.getWidth();

        prepareView(inkView, x, y, startScale);

        animator = inkView.animate().scaleX(finalScale).scaleY(finalScale).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (listener != null) {
                    listener.onAnimationStart(animator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inkView.setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (listener != null) {
                    listener.onAnimationCancel(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (listener != null) {
                    listener.onAnimationRepeat(animator);
                }
            }
        });
        prepareAnimator(animator, ANIMATION_HIDE);
        animator.start();
    }

    public ViewPropertyAnimator prepareAnimator(ViewPropertyAnimator animator, int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animator.withLayer();
        }
        animator.setInterpolator(BakedBezierInterpolator.getInstance());
        return animator;
    }

    private void prepareView(View view, int x, int y, float scale) {
        final int centerX = (view.getWidth() / 2);
        final int centerY = (view.getHeight() / 2);
        view.setTranslationX(x - centerX);
        view.setTranslationY(y - centerY);
        view.setPivotX(centerX);
        view.setPivotY(centerY);
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    /**
     * calculates the required scale of the ink-view to fill the whole view
     *
     * @param x circle center x
     * @param y circle center y
     * @return
     */
    private float calculateScale(int x, int y) {
        final float centerX = getWidth() / 2f;
        final float centerY = getHeight() / 2f;
        final float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        final float deltaX = centerX - x;
        final float deltaY = centerY - y;
        final float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        final float scale = 0.5f + (distance / maxDistance) * 0.5f;
        return scale;
    }
}
