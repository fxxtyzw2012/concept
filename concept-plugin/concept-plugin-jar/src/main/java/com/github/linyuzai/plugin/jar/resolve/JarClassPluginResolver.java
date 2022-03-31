package com.github.linyuzai.plugin.jar.resolve;

import com.github.linyuzai.plugin.core.context.PluginContext;
import com.github.linyuzai.plugin.core.resolve.AbstractPluginResolver;
import com.github.linyuzai.plugin.core.resolve.DependOnResolvers;
import com.github.linyuzai.plugin.jar.concept.JarPlugin;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

@DependOnResolvers(JarClassNamePluginResolver.class)
public class JarClassPluginResolver extends AbstractPluginResolver {

    @Override
    public void resolve(PluginContext context) {
        JarPlugin plugin = context.getPlugin();
        ClassLoader classLoader = plugin.getPluginClassLoader();
        Map<String, String> classNameMap = context.get(JarPlugin.CLASS_NAME);
        Map<String, Class<?>> classMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : classNameMap.entrySet()) {
            classMap.put(entry.getKey(), loadClass(classLoader, entry.getValue()));
        }
        context.set(JarPlugin.CLASS, classMap);
    }

    @SneakyThrows
    private Class<?> loadClass(ClassLoader classLoader, String className) {
        return classLoader.loadClass(className);
    }

    @Override
    public boolean support(PluginContext context) {
        return context.contains(JarPlugin.CLASS_NAME);
    }
}
