package com.github.linyuzai.download.core.source.file;

import com.github.linyuzai.download.core.concept.AbstractPart;
import com.github.linyuzai.download.core.concept.Part;
import com.github.linyuzai.download.core.contenttype.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 文件目录结构支持 / File directory structure support
 */
@Getter
@AllArgsConstructor
public class FilePart extends AbstractPart {

    protected File file;

    protected String name;

    protected String path;

    @SneakyThrows
    @Override
    public InputStream openInputStream() {
        return file.isFile() ? new FileInputStream(file) : new EmptyInputStream();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return file.isFile() ? path : path + File.separator;
    }

    @Override
    public String getContentType() {
        return file.isFile() ? ContentType.file(file) : null;
    }

    @Override
    public Charset getCharset() {
        return null;
    }

    @Override
    public Long getLength() {
        return file.isFile() ? file.length() : null;
    }

    @Override
    public Collection<Part> getChildren() {
        if (file.isFile()) {
            return Collections.emptyList();
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        } else {
            Collection<Part> parts = new ArrayList<>();
            for (File f : files) {
                String childName = f.getName();
                String childPath = path + File.separator + childName;
                Part childPart = new FilePart(f, childName, childPath);
                parts.add(childPart);
            }
            return parts;
        }
    }

    @Override
    public String toString() {
        return "FilePart{" +
                "file=" + file +
                '}';
    }
}
