package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.URLFileName;

/**
 * Custom file name implementation for CMIS Common VFS provider implementation
 */
public class CmisFileName extends URLFileName {

    private String cmisURI = null;
    private String rootUri = null;

    public CmisFileName(String scheme, String hostName, int port, int defaultPort, String userName, String password, String path, FileType type, String queryString, String rootUri) {
        super(scheme, hostName, port, defaultPort, userName, password, path, type, queryString);
        this.rootUri = rootUri;
    }

    public String getCmisURI() {
        StringBuffer buffer = new StringBuffer();
        if (cmisURI == null) {
            buffer.append("http");
            buffer.append("://");
            appendCredentials(buffer, true);
            buffer.append(getHostName());
            if (getPort() != getDefaultPort()) {
                buffer.append(':');
                buffer.append(getPort());
            }
            buffer.append(getPath());
            if (getQueryString() != null) {
                buffer.append("?");
                buffer.append(getQueryString());
            }
            cmisURI = buffer.toString();
        }
        return cmisURI;
    }

    @Override
    public String getRootURI() {
        return rootUri;
    }

    public FileName createName(final String absPath, FileType type) {
        return new CmisFileName(getScheme(),
                getHostName(),
                getPort(),
                getDefaultPort(),
                getUserName(),
                getPassword(),
                absPath,
                type,
                getQueryString(), rootUri);
    }
}
