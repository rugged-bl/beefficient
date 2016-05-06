package com.beefficient.util;

import android.support.annotation.Nullable;

import java.util.Arrays;

public final class ObjectUtils {
    /**
     * Null-safe equivalent of {@code a.equals(b)}.
     */
    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    /**
     * Convenience wrapper for {@link Arrays#hashCode}, adding varargs.
     * This can be used to compute a hash code for an object's fields as follows:
     * {@code Objects.hash(a, b, c)}.
     */
    public static int hashCode(@Nullable Object... objects) {
        return Arrays.hashCode(objects);
    }
}