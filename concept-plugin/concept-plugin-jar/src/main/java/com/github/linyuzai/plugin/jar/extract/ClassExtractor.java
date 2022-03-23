package com.github.linyuzai.plugin.jar.extract;

import com.github.linyuzai.plugin.core.convert.*;
import com.github.linyuzai.plugin.core.extract.TypeMetadataPluginExtractor;
import com.github.linyuzai.plugin.core.match.PluginMatcher;
import com.github.linyuzai.plugin.core.util.ReflectionUtils;
import com.github.linyuzai.plugin.core.util.TypeMetadata;
import com.github.linyuzai.plugin.jar.match.ClassMatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public abstract class ClassExtractor<T> extends TypeMetadataPluginExtractor<T> {

    @Override
    public PluginMatcher getMatcher(TypeMetadata metadata, Annotation[] annotations) {
        Class<?> target = metadata.getTargetClass();
        PluginConvertor convertor = getConvertorAdapter().adapt(metadata);
        return new ClassMatcher(target, annotations, convertor);
    }

    @Override
    public Class<?> getTargetClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class) {
                if (Class.class.isAssignableFrom((Class<?>) rawType)) {
                    Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
                    return ReflectionUtils.toClass(arguments[0]);
                }
            }
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length > 0) {
                Type upperBound = upperBounds[0];
                if (upperBound instanceof Class) {
                    if (Class.class.isAssignableFrom((Class<?>) upperBound)) {
                        return Object.class;
                    }
                } else if (upperBound instanceof ParameterizedType) {
                    return getTargetClass(upperBound);
                }
            }
        }
        return null;
    }
}
