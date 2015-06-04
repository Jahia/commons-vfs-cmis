package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

/**
 *
 */
public abstract class AbstractCmisFileSystem extends AbstractFileSystem implements FileSystem
{

	protected AbstractCmisFileSystem(FileName rootName, FileObject parentLayer, FileSystemOptions fileSystemOptions)
	{
		super(rootName, parentLayer, fileSystemOptions);
	}

	public abstract Session getSession() throws FileSystemException;
}
