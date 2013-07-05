package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.*;
import org.apache.commons.vfs.provider.AbstractLayeredFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.LayeredFileName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A Common VFS provider implementation for CMIS
 */
public class CmisFileProvider extends AbstractLayeredFileProvider
        implements FileProvider {

    public static final char[] RESERVED_CHARS = new char[]
            {
                    // '?', '/', '\\', ' ', '&', '"', '\'', '*', '#', ';', ':', '<', '>', '|', '!'
                    '!'
            };

    protected final static Collection capabilities = Collections.unmodifiableCollection(Arrays.asList(new Capability[]
            {
                    Capability.CREATE,
                    Capability.DELETE,
                    Capability.RENAME,
                    Capability.GET_TYPE,
                    Capability.GET_LAST_MODIFIED,
                    Capability.LIST_CHILDREN,
                    Capability.READ_CONTENT,
                    Capability.URI,
                    Capability.WRITE_CONTENT,
                    Capability.APPEND_CONTENT,
                    Capability.RANDOM_ACCESS_READ,
                    Capability.RANDOM_ACCESS_WRITE
            }));

    public CmisFileProvider() {
        super();
    }

    @Override
    protected FileSystem doCreateFileSystem(String scheme,
                                            FileObject file, FileSystemOptions fileSystemOptions) throws FileSystemException {
        final FileName rootName =
                new LayeredFileName(scheme, file.getName(), FileName.ROOT_PATH, FileType.FOLDER);
        return new CmisFileSystem(rootName, file, fileSystemOptions);
    }

    public Collection getCapabilities() {
        return capabilities;
    }
}
