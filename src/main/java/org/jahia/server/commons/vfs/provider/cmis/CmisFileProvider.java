package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.*;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.FileProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A Common VFS provider implementation for CMIS
 */
public class CmisFileProvider extends AbstractOriginatingFileProvider
        implements FileProvider {

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
        setFileNameParser(CmisFileNameParser.getInstance());
    }

    @Override
    protected FileSystem doCreateFileSystem(FileName fileName, FileSystemOptions fileSystemOptions) throws FileSystemException {
        return new CmisFileSystem(fileName, fileSystemOptions);
    }

    public Collection getCapabilities() {
        return capabilities;
    }
}
