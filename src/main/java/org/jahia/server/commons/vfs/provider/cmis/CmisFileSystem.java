package org.jahia.server.commons.vfs.provider.cmis;

import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.LayeredFileName;
import org.apache.commons.vfs2.provider.UriParser;

/**
 * The commons VFS file system implementation for CMIS
 */
public class CmisFileSystem extends AbstractCmisFileSystem implements FileSystem
{

	protected CmisFileSystem(FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions)
	{
		super(rootName, parentLayer, fileSystemOptions);
	}

	@Override
	protected FileObject createFile(AbstractFileName fileName) throws Exception
	{
		CmisFileObject parentLayer = (CmisFileObject) getParentLayer();
		LayeredFileName layeredFileName = (LayeredFileName) fileName;
		// make sure we call the session first to initialize all session variables (including cmisEntryPointUri)
		Session cmisSession = parentLayer.getCmisBindingFileSystem().getSession(layeredFileName);
		CmisObject cmisObject = null;
		try
		{
			String decodedPath = UriParser.decode(fileName.getPath());
			cmisObject = cmisSession.getObjectByPath(decodedPath);
		}
		catch (CmisObjectNotFoundException confe)
		{
			cmisObject = null;
		}
		return new CmisFileObject(fileName, this, cmisObject, getRootName(), parentLayer.getCmisBindingFileSystem());
	}

	public Session getSession() throws FileSystemException
	{
		CmisFileObject parentLayer = (CmisFileObject) getParentLayer();
		return parentLayer.getCmisBindingFileSystem().getSession();
	}

	@Override
	protected void addCapabilities(Collection caps)
	{
		caps.addAll(CmisFileProvider.capabilities);
	}

}
