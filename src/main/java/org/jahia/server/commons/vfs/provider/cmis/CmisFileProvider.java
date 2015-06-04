package org.jahia.server.commons.vfs.provider.cmis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractLayeredFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.LayeredFileName;

/**
 * A Common VFS provider implementation for CMIS
 */
public class CmisFileProvider extends AbstractLayeredFileProvider implements FileProvider
{

	public static final char[] RESERVED_CHARS = new char[] {
			// '?', '/', '\\', ' ', '&', '"', '\'', '*', '#', ';', ':', '<', '>', '|', '!'
			'!' };

	//@formatter:off
    protected final static Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays.asList(
            Capability.CREATE,
            Capability.DELETE,
            Capability.RENAME,
            Capability.GET_TYPE,
            Capability.GET_LAST_MODIFIED,
            Capability.LIST_CHILDREN,
            Capability.READ_CONTENT,
            Capability.URI,
            Capability.WRITE_CONTENT,
            Capability.APPEND_CONTENT,
            Capability.RANDOM_ACCESS_READ,
            Capability.RANDOM_ACCESS_WRITE));
    //@formatter:on

	public CmisFileProvider()
	{
		super();
	}

	@Override
	protected FileSystem doCreateFileSystem(String scheme, FileObject file, FileSystemOptions fileSystemOptions) throws FileSystemException
	{
		final FileName rootName = new LayeredFileName(scheme, file.getName(), FileName.ROOT_PATH, FileType.FOLDER);
		return new CmisFileSystem(rootName, file, fileSystemOptions);
	}

	public Collection<Capability> getCapabilities()
	{
		return capabilities;
	}
}
