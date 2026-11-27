package tn.forsa.app.util;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import tn.forsa.app.R;

/**
 * Role: Lightweight, pure-Android animation helper using ViewPropertyAnimator,
 * ValueAnimator, and ActivityOptions (no external libraries).
 */
public class AnimationHelper {

    /**
     * Smoothly animates an integer counter from 0 to targetValue (e.g. dashboard stats).
     */
    public static void animateCount(final TextView textView, int targetValue) {
        if (textView == null) {
            return;
        }
        if (targetValue <= 0) {
            textView.setText("0");
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(550);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.start();
    }

    /**
     * Staggers a smooth fade-in and upward slide across dashboard cards.
     */
    public static void staggerFadeSlideUp(View... views) {
        for (int i = 0; i < views.length; i++) {
            View v = views[i];
            if (v == null) {
                continue;
            }
            v.setAlpha(0f);
            v.setTranslationY(28f);
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(i * 75L)
                    .setDuration(360L)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    /**
     * Gives a subtle tactile pulse when tapping a card or chip.
     */
    public static void pulseTap(final View view) {
        if (view == null) {
            return;
        }
        view.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(85)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(110)
                                .start();
                    }
                })
                .start();
    }

    /**
     * Starts an Activity with a smooth slide-in-right transition.
     */
    public static void startActivityAnimated(Context context, Intent intent) {
        Bundle options = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.slide_in_right,
                R.anim.slide_out_left
        ).toBundle();
        context.startActivity(intent, options);
    }
}
