package com.github.linyuzai.cloud.web.core.intercept;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.linyuzai.cloud.web.core.CloudWebException;
import com.github.linyuzai.cloud.web.core.context.WebContext;
import com.github.linyuzai.cloud.web.core.intercept.annotation.OnWebResponse;
import com.github.linyuzai.cloud.web.core.result.WebResult;
import com.sun.org.apache.xpath.internal.operations.String;
import lombok.*;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

/**
 * 字符串响应拦截器
 * 若响应体为字符串则设置设置响应实体内容，且为 {@link String} 类型
 */
@Getter
@RequiredArgsConstructor
@OnWebResponse
public class ParseStringTypeResponseInterceptor implements WebInterceptor {

    private final ObjectMapper objectMapper;

    public ParseStringTypeResponseInterceptor() {
        this(new ObjectMapper());
    }

    @SneakyThrows
    @Override
    public Object intercept(WebContext context, ValueReturner returner, WebInterceptorChain chain) {
        MethodParameter parameter = context.get(MethodParameter.class);
        if (parameter != null) {
            Method method = parameter.getMethod();
            if (method != null && method.getReturnType() == String.class) {
                WebResult<?> webResult = context.get(WebResult.class);
                if (webResult == null) {
                    throw new CloudWebException("WebResult not found");
                }
                if (!(webResult instanceof String)) {
                    context.put(WebResult.class, objectMapper.writeValueAsString(webResult));
                }
            }
        }
        return chain.next(context, returner);
    }

    @Override
    public int getOrder() {
        return Orders.STRING_TYPE;
    }
}
