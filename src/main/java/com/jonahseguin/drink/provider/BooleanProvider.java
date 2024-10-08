package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class BooleanProvider extends DrinkProvider<Boolean> {

    public static final BooleanProvider INSTANCE = new BooleanProvider();

    private static final List<String> SUGGEST = List.of("true", "false");
    private static final List<String> SUGGEST_TRUE = List.of("true");
    private static final List<String> SUGGEST_FALSE = List.of("false");

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Boolean defaultNullValue() {
        return false;
    }

    @Override
    public Boolean provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        if (s == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException ex) {
            throw new CommandExitMessage(Component.translatable("error.provider.boolean", s));
        }
    }

    @Override
    public String argumentDescription() {
        return "true/false";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        prefix = prefix.toLowerCase();
        if (prefix.isEmpty()) {
            return SUGGEST;
        }
        if ("true".startsWith(prefix)) {
            return SUGGEST_TRUE;
        } else if ("false".startsWith(prefix)) {
            return SUGGEST_FALSE;
        } else {
            return Collections.emptyList();
        }
    }
}
