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
 * Created with IntelliJ IDEA.
 * User: loom
 * Date: 04.07.13
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class CmisAtomPubFileSystem extends AbstractCmisFileSystem implements FileSystem {

    private static final char[] USERNAME_RESERVED = {':', '@', '/'};
    private static final char[] PASSWORD_RESERVED = {'@', '/', '?'};

    Session session = null;
    String rootFolderPath = null;
    String cmisEntryPointUri = null;
    Map<String, String> cmisParameters = new HashMap<String, String>();
    CmisFileObject rootCmisFileObject = null;

    protected CmisAtomPubFileSystem(FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
        System.out.println("rootName=" + rootName);
        System.out.println("parentLayer=" + parentLayer);
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
        if (fileName instanceof LayeredFileName) {
            LayeredFileName layeredFileName = (LayeredFileName) fileName;
            String cmisUri = getCmisURI(layeredFileName);
            String cmisPath = null;
            // make sure we call the session first to initialize all session variables (including cmisEntryPointUri)
            Session cmisSession = getSession(layeredFileName);
            boolean byID = true;
            if (cmisUri.equals(cmisEntryPointUri)) {
                return rootCmisFileObject;
            } else if (cmisUri.startsWith(cmisEntryPointUri)) {
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
                    /*
                    String[] pathParts = cmisPath.split("/");
                    StringBuilder encodedPath = new StringBuilder();
                    encodedPath.append("/");
                    for (String pathPart : pathParts) {
                        encodedPath.append(URLEncoder.encode(pathPart, "UTF-8"));
                        encodedPath.append("/");
                    }
                    */
                    StringBuilder encodedPath = new StringBuilder(cmisPath);
                    cmisObject = cmisSession.getObjectByPath(encodedPath.toString());
                }
            } catch (CmisObjectNotFoundException confe) {
                cmisObject = null;
            }
            return new CmisFileObject(fileName, this, cmisObject, getRootName(), this);
        } else {
            String cmisUri = getCmisURI((URLFileName) fileName);
            String cmisPath = null;
            // make sure we call the session first to initialize all session variables (including cmisEntryPointUri)
            Session cmisSession = getSession(fileName);

            return new CmisFileObject(fileName, this, cmisSession.getRootFolder(), getRootName(), this);
        }
    }

    @Override
    protected void addCapabilities(Collection caps) {
        caps.addAll(CmisAtomPubFileProvider.capabilities);
    }

    public Session getSession() {
        return session;
    }

    protected void appendCredentials(URLFileName outerName, StringBuffer buffer, boolean addPassword)
    {
        if (outerName.getUserName() != null && outerName.getUserName().length() != 0)
        {
            UriParser.appendEncoded(buffer, outerName.getUserName(), USERNAME_RESERVED);
            if (outerName.getPassword() != null && outerName.getPassword().length() != 0)
            {
                buffer.append(':');
                if (addPassword)
                {
                    UriParser.appendEncoded(buffer, outerName.getPassword(), PASSWORD_RESERVED);
                }
                else
                {
                    buffer.append("*****");
                }
            }
            buffer.append('@');
        }
    }

    public String getCmisURI(LayeredFileName layeredFileName) {
        StringBuffer buffer = new StringBuffer();
            URLFileName outerName = (URLFileName) layeredFileName.getOuterName();
            buffer.append("http");
            buffer.append("://");
            appendCredentials(outerName, buffer, true);
            buffer.append(outerName.getHostName());
            if (outerName.getPort() != outerName.getDefaultPort()) {
                buffer.append(':');
                buffer.append(outerName.getPort());
            }
            buffer.append(outerName.getPath());
            buffer.append(layeredFileName.getPath());
            if (outerName.getQueryString() != null) {
                buffer.append("?");
                buffer.append(outerName.getQueryString());
            }
        return buffer.toString();
    }

    public String getCmisURI(URLFileName urlFileName) {
        StringBuffer buffer = new StringBuffer();
            buffer.append("http");
            buffer.append("://");
            appendCredentials(urlFileName, buffer, true);
            buffer.append(urlFileName.getHostName());
            if (urlFileName.getPort() != urlFileName.getDefaultPort()) {
                buffer.append(':');
                buffer.append(urlFileName.getPort());
            }
            buffer.append(urlFileName.getPath());
            if (urlFileName.getQueryString() != null) {
                buffer.append("?");
                buffer.append(urlFileName.getQueryString());
            }
        return buffer.toString();
    }

    public Session getSession(FileName fileName) throws FileSystemException {
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
