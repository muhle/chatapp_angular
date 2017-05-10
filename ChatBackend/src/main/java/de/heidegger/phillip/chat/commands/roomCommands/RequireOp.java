package de.heidegger.phillip.chat.commands.roomCommands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(value=RUNTIME)
public @interface RequireOp {
}
