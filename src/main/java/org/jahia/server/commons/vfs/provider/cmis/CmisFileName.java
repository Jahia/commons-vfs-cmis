package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;

/**
 * Custom file name implementation for CMIS Common VFS provider implementation
 */
public class CmisFileName extends GenericFileName {
    protected CmisFileName(String scheme, String hostName, int port, int defaultPort, String userName, String password, String path, FileType type) {
        super(scheme, hostName, port, defaultPort, userName, password, path, type);
    }
}
