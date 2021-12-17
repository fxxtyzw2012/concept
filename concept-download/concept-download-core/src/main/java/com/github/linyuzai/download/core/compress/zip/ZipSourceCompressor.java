package com.github.linyuzai.download.core.compress.zip;

import com.github.linyuzai.download.core.compress.AbstractSourceCompressor;
import com.github.linyuzai.download.core.compress.CompressFormat;
import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.source.Source;
import com.github.linyuzai.download.core.concept.Downloadable;
import com.github.linyuzai.download.core.writer.DownloadWriter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 使用ZIP压缩的压缩器 / Compressor using zip compression
 */
@AllArgsConstructor
public class ZipSourceCompressor extends AbstractSourceCompressor {

    /**
     * 支持ZIP格式 / Support ZIP format
     *
     * @param format  压缩格式 / Compression format
     * @param context 下载上下文 / Context of download
     * @return 是否支持该压缩格式 / If support this compressed format
     */
    @Override
    public boolean support(String format, DownloadContext context) {
        return CompressFormat.ZIP.equals(format);
    }

    /**
     * 执行zip压缩 / Perform zip compression
     *
     * @param source 被压缩的对象 / Object to compress
     * @param os     写入的输出流 / Output stream to write
     * @param writer 写入执行器 / Executor of writing
     * @throws IOException I/O exception
     */
    @Override
    public void doCompress(Source source, OutputStream os, DownloadWriter writer) throws IOException {
        try (ZipOutputStream zos = newZipOutputStream(source, os)) {
            source.write(zos, null, writer, new Downloadable.WriteHandler() {
                @Override
                public void handle(Downloadable.Part part) throws IOException {
                    zos.putNextEntry(new ZipEntry(part.getPath()));
                    Downloadable.WriteHandler.super.handle(part);
                    zos.closeEntry();
                }
            });
        }
    }

    /**
     * 新建一个ZipOutputStream / new a ZipOutputStream
     * {@link ZipOutputStream#ZipOutputStream(OutputStream, Charset)}
     */
    public ZipOutputStream newZipOutputStream(Source source, OutputStream os) {
        return new ZipOutputStream(os);
    }

    /**
     * @return .zip后缀 / Use suffix .zip
     */
    @Override
    public String getSuffix() {
        return CompressFormat.ZIP_SUFFIX;
    }
}