package com.melody.how.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {

    public static AnimationSet getAnimationSet(Animation... animations) {
        AnimationSet set = new AnimationSet(true);
        for (Animation animation : animations)
            set.addAnimation(animation);
        return set;
    }

    public static Animation getAlphaAnimation(float startAlpha, float toAlpha, int millisSecond) {
        Animation animation = new AlphaAnimation(startAlpha, toAlpha);
        animation.setDuration(millisSecond);
        return animation;
    }

    public static Animation getScaleAnimation(float fromX, float toX, float fromY, float toY, int millisSecond) {
        Animation animation = new ScaleAnimation(fromX, toX, fromY, toY);
        animation.setDuration(millisSecond);
        return animation;
    }

    public static Animation getTranlateAnmation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta,
                                                int millisSecond) {
        Animation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(millisSecond);
        return animation;
    }
}
