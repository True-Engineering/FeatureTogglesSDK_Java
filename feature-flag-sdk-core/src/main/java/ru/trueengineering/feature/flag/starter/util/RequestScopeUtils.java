package ru.trueengineering.feature.flag.starter.util;

import java.util.function.Supplier;

public class RequestScopeUtils {
    public static <R> R runInRequestScopeOr(
            Supplier<R> requestScopeAction,
            Supplier<R> defaultAction) {
        try {
            return requestScopeAction.get();
        } catch (Exception beanCreationException) {
            return defaultAction.get();
        }
    }
}
