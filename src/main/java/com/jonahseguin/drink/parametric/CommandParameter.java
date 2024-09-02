package com.jonahseguin.drink.parametric;

import com.google.common.collect.ImmutableList;
import com.jonahseguin.drink.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;


public class CommandParameter {

    private final Class<?> type;
    private final Parameter parameter;
    private final List<Annotation> allAnnotations;
    private final List<Annotation> classifierAnnotations;
    private final List<Annotation> modifierAnnotations;
    private final boolean flag;
    private final boolean requireLastArg;

    public CommandParameter(Class<?> type, Parameter parameter, Annotation[] allAnnotations) {
        this.type = type;
        this.parameter = parameter;
        this.allAnnotations = ImmutableList.copyOf(allAnnotations);
        this.classifierAnnotations = loadClassifiers();
        this.modifierAnnotations = loadModifiers();
        this.flag = loadFlag();
        this.requireLastArg = calculateRequireLastArg();
    }

    private boolean calculateRequireLastArg() {
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(LastArg.class)) {
                return true;
            }
        }
        return false;
    }

    public boolean isText() {
        return parameter.isAnnotationPresent(Text.class);
    }

    public boolean isOptional() {
        return parameter.isAnnotationPresent(OptArg.class);
    }

    public String getDefaultOptionalValue() {
        return parameter.getAnnotation(OptArg.class).value();
    }

    private boolean loadFlag() {
        return parameter.isAnnotationPresent(Flag.class);
    }

    public boolean isFlag() {
        return flag;
    }

    public Flag getFlag() {
        return parameter.getAnnotation(Flag.class);
    }

    private List<Annotation> loadClassifiers() {
        List<Annotation> classifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Classifier.class)) {
                classifiers.add(annotation);
            }
        }
        return ImmutableList.copyOf(classifiers);
    }

    private List<Annotation> loadModifiers() {
        List<Annotation> modifiers = new ArrayList<>();
        for (Annotation annotation : allAnnotations) {
            if (annotation.annotationType().isAnnotationPresent(Modifier.class)) {
                modifiers.add(annotation);
            }
        }
        return ImmutableList.copyOf(modifiers);
    }

    public Class<?> getType() {
        return type;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public List<Annotation> getAllAnnotations() {
        return allAnnotations;
    }

    public List<Annotation> getClassifierAnnotations() {
        return classifierAnnotations;
    }

    public List<Annotation> getModifierAnnotations() {
        return modifierAnnotations;
    }

    public boolean isRequireLastArg() {
        return requireLastArg;
    }
}
