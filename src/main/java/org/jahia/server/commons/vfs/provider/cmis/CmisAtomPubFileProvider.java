package org.jahia.server.commons.vfs.provider.cmis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.url.UrlFileNameParser;

public class CmisAtomPubFileProvider extends AbstractOriginatingFileProvider implements FileProvider
{
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
			Capability.RANDOM_ACCESS_WRITE
	));
	//@formatter:on

	public CmisAtomPubFileProvider()
	{
		super();
		setFileNameParser(new UrlFileNameParser());
	}

	@Override
	protected FileSystem doCreateFileSystem(FileName rootName, FileSystemOptions fileSystemOptions) throws FileSystemException
	{
		return new CmisAtomPubFileSystem((AbstractFileName) rootName, null, fileSystemOptions);
	}

	public  Collection<Capability> getCapabilities()
	{
		return capabilities;
	}
}
