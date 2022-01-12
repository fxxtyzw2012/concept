package com.github.linyuzai.download.core.writer;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.exception.DownloadException;
import com.github.linyuzai.download.core.order.OrderProvider;
import com.github.linyuzai.download.core.range.Range;
import com.github.linyuzai.download.core.concept.Downloadable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

/**
 * 默认的写入器的适配器 / Adapter of writer by default
 */
@Getter
@AllArgsConstructor
public class DefaultDownloadWriterAdapter implements DownloadWriterAdapter {

    private final List<DownloadWriter> writers;

    public void sort() {
        this.writers.sort(Comparator.comparingInt(OrderProvider::getOrder));
    }

    @Override
    public DownloadWriter getWriter(Downloadable downloadable, Range range, DownloadContext context) {
        for (DownloadWriter writer : writers) {
            if (writer.support(downloadable, range, context)) {
                return writer;
            }
        }
        throw new DownloadException("No DownloadWriter support: " + downloadable);
    }
}
