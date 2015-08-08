package com.stevenlr.waffle2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Waffle2Game {

	int viewportWidth() default 1280;
	int viewportHeight() default 720;
	int resolution() default 1;
	String title() default "Waffle 2 Game";
	boolean showFps() default false;
}
