package net.buildtheearth.terrabungee.common.discord;

import com.google.common.collect.ImmutableMap;
import net.buildtheearth.terrabungee.common.exceptions.InvalidParameterException;
import net.buildtheearth.terrabungee.common.exceptions.NotFoundException;
import net.buildtheearth.terrabungee.common.exceptions.NotImplementedException;
import net.buildtheearth.terrabungee.common.exceptions.ServerException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Xbox Bedrock
 */
public class BotApiErrors {
    public static final Map<String, Class< ? extends RuntimeException>> exceptionMap = ImmutableMap.of(
            "INVALID_PARAMETER", InvalidParameterException.class,
            "NOT_FOUND", NotFoundException.class,
            "SERVER_ERROR", ServerException.class,
            "NOT_IMPLEMENTED", NotImplementedException.class
    );

    public static Class< ? extends RuntimeException> getException(String name) {
        Class< ? extends RuntimeException> toThrow = exceptionMap.get(name);

        if (toThrow == null) return RuntimeException.class;

        return toThrow;
    }

    public static void throwException(String name, String msg)  {
        Class< ? extends RuntimeException> toThrow = getException(name);

        try {
            throw toThrow.getConstructor(String.class).newInstance(msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Exception in throwing exception");
        }
    }
}

