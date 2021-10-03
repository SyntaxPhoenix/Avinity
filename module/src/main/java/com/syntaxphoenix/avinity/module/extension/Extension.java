package com.syntaxphoenix.avinity.module.extension;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(TYPE)
public @interface Extension {

    public Class<? extends IExtension>[] points() default {};

}
