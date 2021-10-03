package com.syntaxphoenix.avinity.module.extension;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target(TYPE)
public @interface Extension {

    public Class<? extends IExtension>[] points() default {};

}
