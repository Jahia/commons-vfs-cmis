package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.vfs.*;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.apache.commons.vfs.provider.LayeredFileName;
import org.apache.commons.vfs.provider.URLFileName;
import org.apache.commons.vfs.provider.UriParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The commons VFS file system implementation for CMIS
 */
public class CmisFileSystem extends AbstractCmisFileSystem implements FileSystem {

    protected CmisFileSystem(FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
    }

    @Override
    protected FileObject createFile(FileName fileName) throws Exception {
        CmisFileObject parentLayer = (CmisFileObject) getParentLayer();
        return parentLayer.getCmisBindingFileSystem().createFile(fileName);
    }

    public Session getSession() throws FileSystemException {
        CmisFileObject parentLayer = (CmisFileObject) getParentLayer();
        return parentLayer.getCmisBindingFileSystem().getSession();
    }

    @Override
    protected void addCapabilities(Collection caps) {
        caps.addAll(CmisFileProvider.capabilities);
    }

}
