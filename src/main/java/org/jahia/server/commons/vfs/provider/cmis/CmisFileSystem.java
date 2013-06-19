package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
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
    Map<String,String> cmisParameters = new HashMap<String,String>();

    protected CmisFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) {
        super(rootName, null, fileSystemOptions);

        System.out.println("rootName=" + rootName);
        String bindingType = CmisFileSystemConfigBuilder.getInstance().getSessionParameter(fileSystemOptions, SessionParameter.BINDING_TYPE);
        if (BindingType.ATOMPUB.value().equals(bindingType)) {
            cmisParameters.put(SessionParameter.ATOMPUB_URL, CmisFileSystemConfigBuilder.getInstance().getSessionParameter(fileSystemOptions, SessionParameter.ATOMPUB_URL));
        } else if (BindingType.WEBSERVICES.value().equals(bindingType)) {

        } else if (BindingType.BROWSER.value().equals(bindingType)) {

        } else if (BindingType.LOCAL.value().equals(bindingType)) {

        } else if (BindingType.CUSTOM.value().equals(bindingType)) {

        } else {

        }

    }

    @Override
    protected FileObject createFile(FileName fileName) throws Exception {
        CmisFileName cmisFileName = (CmisFileName) fileName;
        String cmisUri = cmisFileName.getCmisURI();
        String cmisPath = null;
        // make sure we call the session first to initialize all session variables (including cmisEntryPointUri)
        Session cmisSession = getSession(cmisFileName);
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
        try {
            if (byID) {
                cmisObject = cmisSession.getObject(cmisUri);
            } else {
                cmisObject = cmisSession.getObjectByPath(cmisPath);
            }
        } catch (CmisObjectNotFoundException confe) {
            cmisObject = null;
        }
        return new CmisFileObject(fileName, this, cmisObject);
    }

    @Override
    protected void addCapabilities(Collection caps) {
        caps.addAll(CmisFileProvider.capabilities);
    }

    public Session getSession(CmisFileName cmisFileName) {
        if (session != null) {
            return session;
        }
        // Create a SessionFactory and set up the SessionParameter map
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        if (!cmisParameters.containsKey(SessionParameter.ATOMPUB_URL)) {
            // we assume the first call to getSession will be with the entry point URI.
            cmisParameters.put(SessionParameter.ATOMPUB_URL, cmisFileName.getCmisURI());
        }
        cmisEntryPointUri = cmisParameters.get(SessionParameter.ATOMPUB_URL);
        if (cmisEntryPointUri.endsWith("/")) {
            cmisEntryPointUri = cmisEntryPointUri.substring(0, cmisEntryPointUri.length()-1);
        }
        cmisParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // find all the repositories at this URL - there should only be one.
        List<Repository> repositories = sessionFactory.getRepositories(cmisParameters);
        for (Repository r : repositories) {
            System.out.println("Found repository: " + r.getName());
        }

        // create session with the first (and only) repository
        Repository repository = repositories.get(0);
        cmisParameters.put(SessionParameter.REPOSITORY_ID, repository.getId());
        session = sessionFactory.createSession(cmisParameters);

        Folder rootFolder = session.getRootFolder();
        rootFolderPath = rootFolder.getPath();
        return session;
    }
}
