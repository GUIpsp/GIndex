package net.guipsp.gindex;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IndexAndInstanceSubclasses {
	String value();
}
