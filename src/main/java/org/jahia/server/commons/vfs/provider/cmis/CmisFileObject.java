package org.jahia.server.commons.vfs.provider.cmis;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.FolderType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.UriParser;

/**
 * A Commons VFS FileObject for a CMIS object
 */
public class CmisFileObject extends AbstractFileObject implements FileObject
{

	private CmisObject cmisObject;
	private AbstractCmisFileSystem cmisFileSystem;
	private CmisAtomPubFileSystem cmisBindingFileSystem;

	protected CmisFileObject(FileName name, AbstractFileSystem fs, CmisObject cmisObject, FileName rootName, CmisAtomPubFileSystem cmisBindingFileSystem) throws FileSystemException
	{
		super((AbstractFileName) name, fs);
		this.cmisObject = cmisObject;
		this.cmisFileSystem = (AbstractCmisFileSystem) fs;
		this.cmisBindingFileSystem = cmisBindingFileSystem;
	}

	public CmisObject getCmisObject()
	{
		return cmisObject;
	}

	public void setCmisObject(CmisObject cmisObject)
	{
		this.cmisObject = cmisObject;
	}

	public CmisAtomPubFileSystem getCmisBindingFileSystem()
	{
		return cmisBindingFileSystem;
	}

	@Override
	protected void doAttach() throws Exception
	{
	}

	@Override
	protected void doDetach() throws Exception
	{
		super.doDetach();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	protected FileType doGetType() throws Exception
	{
		if (cmisObject == null)
		{
			return FileType.IMAGINARY;
		}
		ObjectType cmisObjectType = cmisObject.getType();
		if (cmisObjectType instanceof FolderType)
		{
			return FileType.FOLDER;
		}
		else if (cmisObjectType instanceof DocumentType)
		{
			return FileType.FILE;
		}
		else
		{
			return FileType.IMAGINARY;
		}
	}

	@Override
	protected String[] doListChildren() throws Exception
	{
		if (cmisObject == null)
		{
			return new String[0];
		}
		List<String> childrenUris = new ArrayList<String>();
		if (cmisObject instanceof Folder)
		{
			Folder folder = (Folder) cmisObject;
			ItemIterable<CmisObject> childCmisObjects = folder.getChildren();
			for (CmisObject childCmisObject : childCmisObjects)
			{
				if (childCmisObject instanceof FileableCmisObject)
				{
					FileableCmisObject fileableCmisObject = (FileableCmisObject) childCmisObject;
					if (fileableCmisObject.getName() != null)
					{
						String childName = UriParser.encode(fileableCmisObject.getName(), CmisFileProvider.RESERVED_CHARS);
						childrenUris.add(childName);
					}
				}
				else
				{
					childrenUris.add("?id=" + childCmisObject.getId());
				}
			}
		}
		return childrenUris.toArray(new String[childrenUris.size()]);
	}

	@Override
	protected long doGetContentSize() throws Exception
	{
		if (cmisObject == null)
		{
			return 0;
		}
		if (cmisObject instanceof Document)
		{
			Document document = (Document) cmisObject;
			return document.getContentStreamLength();
		}
		return 0;
	}

	@Override
	protected InputStream doGetInputStream() throws Exception
	{
		if (cmisObject == null)
		{
			return null;
		}
		if (cmisObject instanceof Document)
		{
			Document document = (Document) cmisObject;
			return document.getContentStream().getStream();
		}
		return null;
	}

	@Override
	protected void doCreateFolder() throws Exception
	{
		CmisFileObject parentCmisFileObject = (CmisFileObject) cmisFileSystem.resolveFile(getName().getParent());
		if (parentCmisFileObject.cmisObject instanceof Folder)
		{
			Folder parentFolder = (Folder) parentCmisFileObject.cmisObject;
			Map<String, String> folderProperties = new HashMap<String, String>();
			folderProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			String folderName = UriParser.decode(getName().getBaseName());
			folderProperties.put(PropertyIds.NAME, folderName);
			cmisObject = parentFolder.createFolder(folderProperties);
		}
	}

	@Override
	protected void doDelete() throws Exception
	{
		cmisObject.delete();
		cmisObject = null;
	}

	@Override
	protected Map doGetAttributes() throws Exception
	{
		List<Property<?>> cmisProperties = cmisObject.getProperties();
		Map attributes = new HashMap();
		for (Property<?> cmisProperty : cmisProperties)
		{
			attributes.put(cmisProperty.getLocalName(), cmisProperty.getValuesAsString());
		}
		return attributes;
	}

	@Override
	protected OutputStream doGetOutputStream(boolean bAppend) throws Exception
	{
		// @todo implement real mime type handling.
		return new CmisOutputStream(this, cmisFileSystem, "application/octet-stream");
	}

	@Override
	protected long doGetLastModifiedTime() throws Exception
	{
		return cmisObject.getLastModificationDate().getTimeInMillis();
	}
}
