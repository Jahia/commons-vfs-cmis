package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.URLFileNameParser;
import org.apache.commons.vfs.provider.UriParser;
import org.apache.commons.vfs.provider.VfsComponentContext;

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

    @Override
    /**
     * We override this method just to create a CmisFileName object, no other modifications were done to the parent
     * code that is copied here.
     */
    public FileName parseUri(VfsComponentContext context, FileName base, String filename) throws FileSystemException {
        // FTP URI are generic URI (as per RFC 2396)
        final StringBuffer name = new StringBuffer();

        // Extract the scheme and authority parts
        final Authority auth = extractToPath(filename, name);

        // Extract the queryString
        String queryString = UriParser.extractQueryString(name);

        // Decode and normalise the file name
        try {
            UriParser.canonicalizePath(name, 0, name.length(), this);
        } catch (FileSystemException fse) {
            System.out.println("Error canonicalizing path: " + name);
        }
        UriParser.fixSeparators(name);
        FileType fileType = UriParser.normalisePath(name);
        final String path = name.toString();

        return new CmisFileName(
                auth.scheme,
                auth.hostName,
                auth.port,
                getDefaultPort(),
                auth.userName,
                auth.password,
                path,
                fileType,
                queryString);
    }
}
