package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;

public class UUIDProvider extends DrinkProvider<UUID> {

    public static final UUIDProvider INSTANCE = new UUIDProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public UUID provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = arg.get();
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ex) {
            throw new CommandExitMessage("Required valid uuid, Given: '" + s + "'");
        }
    }

    @Override
    public String argumentDescription() {
        return "uuid";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return List.of("<uuid>");
    }
}
