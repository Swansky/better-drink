package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class FloatProvider extends DrinkProvider<Float> {

    public static final FloatProvider INSTANCE = new FloatProvider();

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
    public Float defaultNullValue() {
        return 0f;
    }

    @Override
    public Float provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ex) {
            throw new CommandExitMessage(Component.translatable("error.provider.float", s));
        }
    }

    @Override
    public String argumentDescription() {
        return "float number";
    }
}
