package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The commons VFS file system implementation for CMIS
 */
public class CmisFileSystem extends AbstractFileSystem implements FileSystem {

    Session session = null;
    String rootFolderPath = null;
    String cmisEntryPointUri = null;

    protected CmisFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) {
        super(rootName, null, fileSystemOptions);
        // Create a SessionFactory and set up the SessionParameter map
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // connection settings - we are connecting to a public cmis repo,
        // using the AtomPUB binding
        System.out.println("rootName=" + rootName);
        String bindingType = CmisFileSystemConfigBuilder.getInstance().getSessionParameter(fileSystemOptions, SessionParameter.BINDING_TYPE);
        if (BindingType.ATOMPUB.value().equals(bindingType)) {

        } else if (BindingType.WEBSERVICES.value().equals(bindingType)) {

        } else if (BindingType.BROWSER.value().equals(bindingType)) {

        } else if (BindingType.LOCAL.value().equals(bindingType)) {

        } else if (BindingType.CUSTOM.value().equals(bindingType)) {

        } else {

        }
        parameter.put(SessionParameter.ATOMPUB_URL, "http://repo.opencmis.org/inmemory/atom/");
        cmisEntryPointUri = "http://repo.opencmis.org/inmemory/atom/";
        if (cmisEntryPointUri.endsWith("/")) {
            cmisEntryPointUri = cmisEntryPointUri.substring(0, cmisEntryPointUri.length()-1);
        }
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // find all the repositories at this URL - there should only be one.
        List<Repository> repositories = sessionFactory.getRepositories(parameter);
        for (Repository r : repositories) {
            System.out.println("Found repository: " + r.getName());
        }

        // create session with the first (and only) repository
        Repository repository = repositories.get(0);
        parameter.put(SessionParameter.REPOSITORY_ID, repository.getId());
        session = sessionFactory.createSession(parameter);

        Folder rootFolder = session.getRootFolder();
        rootFolderPath = rootFolder.getPath();

    }

    @Override
    protected FileObject createFile(FileName fileName) throws Exception {
        CmisFileName cmisFileName = (CmisFileName) fileName;
        String cmisUri = cmisFileName.getCmisURI();
        String cmisPath = null;
        boolean byID = true;
        if (cmisUri.startsWith(cmisEntryPointUri)) {
            cmisPath = cmisUri.substring(cmisEntryPointUri.length());
            byID = false;
            if (cmisPath.length() == 0) {
                cmisPath = "/";
                byID = false;
            }
        }
        CmisObject cmisObject = null;
        if (byID) {
            cmisObject = session.getObjectByPath(cmisUri);
        } else {
            cmisObject = session.getObjectByPath(cmisPath);
        }
        if (cmisObject != null) {
            return new CmisFileObject(fileName, this, cmisObject);
        } else {
            return null;
        }
    }

    @Override
    protected void addCapabilities(Collection caps) {
        caps.addAll(CmisFileProvider.capabilities);
    }
}
