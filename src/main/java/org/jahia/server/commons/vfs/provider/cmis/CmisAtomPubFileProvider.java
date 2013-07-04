package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.*;
import org.apache.commons.vfs.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs.provider.FileProvider;
import org.apache.commons.vfs.provider.url.UrlFileNameParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: loom
 * Date: 04.07.13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class CmisAtomPubFileProvider extends AbstractOriginatingFileProvider implements FileProvider {

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

    public CmisAtomPubFileProvider() {
        super();
        setFileNameParser(new UrlFileNameParser());
    }

    @Override
    protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) throws FileSystemException {
        return new CmisAtomPubFileSystem(rootName, null, fileSystemOptions);
    }

    public Collection getCapabilities() {
        return capabilities;
    }
}
