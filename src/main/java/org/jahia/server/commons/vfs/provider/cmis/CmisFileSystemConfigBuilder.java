package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemOptions;

/**
 * A file system config builder for CMIS
 */
public class CmisFileSystemConfigBuilder extends FileSystemConfigBuilder {

    final static CmisFileSystemConfigBuilder instance = new CmisFileSystemConfigBuilder();

    public static CmisFileSystemConfigBuilder getInstance() {
        return instance;
    }

    public void setSessionParameter(FileSystemOptions opts, String parameterName, String parameterValue) {
        setParam(opts, parameterName, parameterValue);
    }

    public String getSessionParameter(FileSystemOptions opts, String parameterName) {
        return (String) getParam(opts, parameterName);
    }

    @Override
    protected Class getConfigClass() {
        return CmisFileSystem.class;
    }

}
