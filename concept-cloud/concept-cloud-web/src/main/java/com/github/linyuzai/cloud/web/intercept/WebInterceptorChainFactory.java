package com.github.linyuzai.cloud.web.intercept;

import java.util.List;

public interface WebInterceptorChainFactory {

    WebInterceptorChain create(List<WebInterceptor> interceptors);
}