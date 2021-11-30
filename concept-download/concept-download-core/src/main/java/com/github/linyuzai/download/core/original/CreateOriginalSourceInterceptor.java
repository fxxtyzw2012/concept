package com.github.linyuzai.download.core.original;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.interceptor.DownloadInterceptor;
import com.github.linyuzai.download.core.interceptor.DownloadInterceptorChain;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * 下载源处理拦截器
 */
@AllArgsConstructor
public class CreateOriginalSourceInterceptor implements DownloadInterceptor {

    /**
     * 将所有需要下载的数据对象转换为下载源
     *
     * @param context 下载上下文
     * @param chain   下载链
     */
    @Override
    public void intercept(DownloadContext context, DownloadInterceptorChain chain) throws IOException {
        Object source = context.getOptions().getSource();
        OriginalSourceFactoryAdapter adapter = context.get(OriginalSourceFactoryAdapter.class);
        OriginalSourceFactory factory = adapter.getOriginalSourceFactory(source, context);
        context.set(OriginalSource.class, factory.create(source, context));
        chain.next(context);
    }

    @Override
    public int getOrder() {
        return ORDER_SOURCE_PREPARE;
    }
}
