package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.annotation.Positive;
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class IntegerProvider extends DrinkProvider<Integer> {

    public static final IntegerProvider INSTANCE = new IntegerProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean allowNullArgument() {
        return false;
    }

    @Nullable
    @Override
    public Integer defaultNullValue() {
        return 0;
    }

    @Override
    @Nullable
    public Integer provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        try {
            int value = Integer.parseInt(s);
            for (Annotation annotation : annotations) {
                if (annotation instanceof Positive && (value <= 0)) {
                    throw new CommandExitMessage(Component.translatable("error.provider.integer.positive", s));
                }
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new CommandExitMessage(Component.translatable("error.provider.integer", s));
        }
    }

    @Override
    public String argumentDescription() {
        return "integer";
    }

}
