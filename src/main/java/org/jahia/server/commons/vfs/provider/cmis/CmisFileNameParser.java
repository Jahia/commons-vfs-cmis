package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.provider.URLFileNameParser;

/**
 * Custom file name parser for CMIS Common VFS implementation
 */
public class CmisFileNameParser extends URLFileNameParser {

    private static CmisFileNameParser instance = new CmisFileNameParser(80);

    public CmisFileNameParser(int defaultPort) {
        super(defaultPort);
    }

    public static CmisFileNameParser getInstance() {
        return instance;
    }

}
