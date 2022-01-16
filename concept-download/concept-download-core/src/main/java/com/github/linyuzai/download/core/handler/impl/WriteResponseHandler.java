package com.github.linyuzai.download.core.handler.impl;

import com.github.linyuzai.download.core.compress.Compression;
import com.github.linyuzai.download.core.concept.Part;
import com.github.linyuzai.download.core.web.ContentType;
import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.context.DownloadContextInitializer;
import com.github.linyuzai.download.core.handler.DownloadHandler;
import com.github.linyuzai.download.core.handler.DownloadHandlerChain;
import com.github.linyuzai.download.core.range.Range;
import com.github.linyuzai.download.core.web.DownloadRequestProvider;
import com.github.linyuzai.download.core.web.DownloadResponse;
import com.github.linyuzai.download.core.web.DownloadResponseProvider;
import com.github.linyuzai.download.core.writer.DownloadWriter;
import com.github.linyuzai.download.core.writer.DownloadWriterAdapter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import reactor.core.publisher.Mono;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 写响应处理器 / A handler to write response
 */
@CommonsLog
@AllArgsConstructor
public class WriteResponseHandler implements DownloadHandler, DownloadContextInitializer {

    private DownloadWriterAdapter downloadWriterAdapter;

    private DownloadRequestProvider downloadRequestProvider;

    private DownloadResponseProvider downloadResponseProvider;

    /**
     * 将压缩后的对象写入输出流 / Writes the compressed object to the output stream
     * 设置inline或文件名 / Set inline or file name
     * 设置ContentType / Set ContentType
     * 设置ContentLength / Set ContentLength
     * 设置响应头 / Set response headers
     *
     * @param context 下载上下文 / Context of download
     */
    @Override
    public Mono<Void> handle(DownloadContext context, DownloadHandlerChain chain) {
        Range range = context.get(Range.class);
        Compression compression = context.get(Compression.class);
        DownloadWriter writer = downloadWriterAdapter.getWriter(compression, range, context);
        return downloadResponseProvider.getResponse(context)
                .map(response -> applyHeaders(response, compression, context))
                .flatMap(response -> response.write(new Consumer<OutputStream>() {

                    @SneakyThrows
                    @Override
                    public void accept(OutputStream os) {
                        Collection<Part> parts = compression.getParts();
                        for (Part part : parts) {
                            writer.write(part.getInputStream(), os, range,
                                    part.getCharset(), part.getLength());
                        }
                        os.flush();
                    }
                })).flatMap(it -> chain.next(context));
    }

    private DownloadResponse applyHeaders(DownloadResponse response, Compression compression, DownloadContext context) {
        boolean inline = context.getOptions().isInline();
        if (inline) {
            response.setInline();
        } else {
            String filename = context.getOptions().getFilename();
            if (filename == null || filename.isEmpty()) {
                response.setFilename(compression.getName());
            } else {
                response.setFilename(filename);
            }
        }
        String contentType = context.getOptions().getContentType();
        if (contentType == null || contentType.isEmpty()) {
            String compressionContentType = compression.getContentType();
            if (compressionContentType == null || compressionContentType.isEmpty()) {
                response.setContentType(ContentType.Application.OCTET_STREAM);
            } else {
                response.setContentType(compressionContentType);
            }
        } else {
            response.setContentType(contentType);
        }
        Long length = compression.getLength();
        response.setContentLength(length);
        Map<String, String> headers = context.getOptions().getHeaders();
        if (headers != null) {
            response.setHeaders(headers);
        }
        return response;
    }

    /**
     * 初始化时，将请求和响应放到上下文中 / Put the request and response into context when initializing
     *
     * @param context 下载上下文 / Context of download
     */
    @Override
    public void initialize(DownloadContext context) {
        context.set(DownloadWriterAdapter.class, downloadWriterAdapter);
        context.set(DownloadRequestProvider.class, downloadRequestProvider);
        context.set(DownloadResponseProvider.class, downloadResponseProvider);
    }

    @Override
    public int getOrder() {
        return ORDER_WRITE_RESPONSE;
    }
}