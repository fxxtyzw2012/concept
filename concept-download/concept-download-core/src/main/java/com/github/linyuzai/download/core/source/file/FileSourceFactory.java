package com.github.linyuzai.download.core.source.file;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.source.Source;
import com.github.linyuzai.download.core.source.SourceFactory;
import lombok.extern.apachecommons.CommonsLog;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 支持文件对象的工厂 / Factory support file object
 */
@CommonsLog
public class FileSourceFactory implements SourceFactory {

    /**
     * 支持文件对象 / File object supported
     *
     * @param source  需要下载的数据对象 / Object to download
     * @param context 下载上下文 / Context of download
     * @return 是否支持 / Is it supported
     */
    @Override
    public boolean support(Object source, DownloadContext context) {
        return source instanceof File;
    }

    /**
     * Use {@link FileSource}
     *
     * @param source  需要下载的数据对象 / Object to download
     * @param context 下载上下文 / Context of download
     * @return 下载源 / Source {@link FileSource}
     */
    @Override
    public Mono<Source> create(Object source, DownloadContext context) {
        Charset charset = context.getOptions().getCharset();
        FileSource build = new FileSource.Builder<>()
                .file((File) source)
                .charset(charset)
                .build();
        context.log("[Create source] " + build);
        return Mono.just(build);
    }
}
