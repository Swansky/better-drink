package com.jonahseguin.drink;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public final class TranslateUtils {


    private static final Component ERROR_LABEL;
    public static final Component NO_PERMISSION_MESSAGE;
    public static final Component EXCEPTION_MESSAGE;

    static {
        ERROR_LABEL = Component.text("[")
                .append(Component.translatable("error.label").color(NamedTextColor.RED))
                .append(Component.text("] - "));
        NO_PERMISSION_MESSAGE = makeErrorMessage("error.no_permission");
        EXCEPTION_MESSAGE = makeErrorMessage("error.exception");
    }

    private TranslateUtils() {
        throw new IllegalArgumentException("Utils class");
    }

    private static final TranslationRegistry REGISTRY = TranslationRegistry.create(Key.key("better-drink:translation"));

    private static final Locale[] SUPPORTED_LOCAL = new Locale[]{Locale.US, Locale.FRANCE};
    private static final boolean register = false;

    public static void registerTranslationKeys() {
        if (register) return;
        int countKey = 0;
        for (Locale locale : SUPPORTED_LOCAL) {
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale, UTF8ResourceBundleControl.get());
            int size = bundle.keySet().size();
            if (size > countKey) countKey = size;
            if (size < countKey) {
                throw new IllegalArgumentException(
                        String.format("Translation key of %s have some missing key. Found %s but need %s", locale, size, countKey));
            }
            REGISTRY.registerAll(locale, bundle, true);

            GlobalTranslator.translator().addSource(REGISTRY);
        }
    }

    public static Component makeErrorMessage(String key, Component... components) {
        return ERROR_LABEL.append(Component.translatable(key, components));
    }

    public static Component makeErrorMessage(Component displayableMessage) {
        return ERROR_LABEL.append(displayableMessage);
    }
}
