package org.jahia.server.commons.vfs.provider.cmis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.URLFileName;
import org.apache.commons.vfs2.provider.UriParser;

/**
 * The Cmis AtomPub Binding Common VFS FileSystem implementation
 */
public class CmisAtomPubFileSystem extends AbstractCmisFileSystem implements FileSystem
{

    private static final char[] USERNAME_RESERVED = {':', '@', '/'};
    private static final char[] PASSWORD_RESERVED = {'@', '/', '?'};

    Session session = null;
    String rootFolderPath = null;
    String cmisEntryPointUri = null;
    Map<String, String> cmisParameters = new HashMap<String, String>();
    CmisFileObject rootCmisFileObject = null;

    protected CmisAtomPubFileSystem(AbstractFileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
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
    protected FileObject createFile(AbstractFileName fileName) throws Exception {
        // make sure we call the session first to initialize all session variables (including cmisEntryPointUri)
        if (session == null) {
            getSession(fileName);
        }

        return getRoot();
    }

    @Override
    public FileObject getRoot() throws FileSystemException
    {
        return rootCmisFileObject;
    }

    @Override
    protected void addCapabilities(Collection<Capability> caps) {
        caps.addAll(CmisAtomPubFileProvider.capabilities);
    }

    public Session getSession() {
        return session;
    }

    protected void appendCredentials(URLFileName outerName, StringBuilder stringBuilder, boolean addPassword) {
        if (outerName.getUserName() != null && outerName.getUserName().length() != 0) {
            UriParser.appendEncoded(stringBuilder, outerName.getUserName(), USERNAME_RESERVED);
            if (outerName.getPassword() != null && outerName.getPassword().length() != 0) {
                stringBuilder.append(':');
                if (addPassword) {
                    UriParser.appendEncoded(stringBuilder, outerName.getPassword(), PASSWORD_RESERVED);
                } else {
                    stringBuilder.append("*****");
                }
            }
            stringBuilder.append('@');
        }
    }

    public String getCmisURI(URLFileName urlFileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http");
        builder.append("://");
        appendCredentials(urlFileName, builder, true);
        builder.append(urlFileName.getHostName());
        if (urlFileName.getPort() != urlFileName.getDefaultPort()) {
            builder.append(':');
            builder.append(urlFileName.getPort());
        }
        builder.append(urlFileName.getPath());
        if (urlFileName.getQueryString() != null) {
            builder.append("?");
            builder.append(urlFileName.getQueryString());
        }
        return builder.toString();
    }

    protected Session getSession(FileName fileName) throws FileSystemException {
        if (session != null) {
            return session;
        }
        URLFileName urlFileName = (URLFileName) fileName;
        // Create a SessionFactory and set up the SessionParameter map
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        if (!cmisParameters.containsKey(SessionParameter.ATOMPUB_URL)) {
            // we assume the first call to getSession will be with the entry point URI.

            cmisParameters.put(SessionParameter.ATOMPUB_URL, getCmisURI(urlFileName));
        }
        cmisEntryPointUri = cmisParameters.get(SessionParameter.ATOMPUB_URL);
        if (cmisEntryPointUri.endsWith("/")) {
            cmisEntryPointUri = cmisEntryPointUri.substring(0, cmisEntryPointUri.length() - 1);
        }
        cmisParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        if (urlFileName.getUserName() != null) {
            cmisParameters.put(SessionParameter.USER, urlFileName.getUserName());
        }
        if (urlFileName.getPassword() != null) {
            cmisParameters.put(SessionParameter.PASSWORD, urlFileName.getPassword());
        }

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
        rootCmisFileObject = new CmisFileObject(getRootName(), this, rootFolder, getRootName(), this);
        rootFolderPath = rootFolder.getPath();
        return session;
    }

}
