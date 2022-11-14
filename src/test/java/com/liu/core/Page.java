package com.liu.core;


import java.lang.reflect.Field;
import java.util.Objects;

public abstract class Page {
    protected final String displayName;

    public Page() {
        this("");
    }

    public Page(String displayName) {
        Objects.requireNonNull(displayName, "'displayName' must not be null");
        if (displayName.isBlank()) {
            this.displayName = this.prettifyCamelCaseString(this.getClass().getSimpleName());
        } else {
            this.displayName = displayName;
        }

        this.initializeFields();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    protected String prettifyCamelCaseString(String input) {
        String s = String.join(" ", input.replace("_", " - ").split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
        String var10000 = s.substring(0, 1).toUpperCase();
        return var10000 + s.substring(1);
    }

    private void initializeFields() {
        try {
            Class<?> page = this.getClass();
            Field[] var2 = page.getDeclaredFields();
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Field field = var2[var4];
                field.setAccessible(true);
                String displayName = null;
                if (field.isAnnotationPresent(DisplayName.class)) {
                    displayName = Objects.requireNonNull(field.getAnnotation(DisplayName.class).value(), "'value' must not be null");
                    if (displayName.isBlank()) {
                        throw new RuntimeException("'value' must not be an empty or blank string");
                    }
                }
            }

        } catch (Exception var7) {
            throw new RuntimeException("Error initializing page fields", var7);
        }
    }
}
