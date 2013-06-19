package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.provider.AbstractFileSystem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A Commons VFS FileObject for a CMIS object
 */
public class CmisFileObject extends AbstractFileObject
        implements FileObject {

    private CmisObject cmisObject;

    protected CmisFileObject(FileName name, AbstractFileSystem fs, CmisObject cmisObject) {
        super(name, fs);
        this.cmisObject = cmisObject;
    }

    @Override
    protected FileType doGetType() throws Exception {
        ObjectType cmisObjectType = cmisObject.getType();
        if (cmisObjectType instanceof FolderType) {
            return FileType.FOLDER;
        } else if (cmisObjectType instanceof DocumentType) {
            return FileType.FILE;
        } else {
            return FileType.IMAGINARY;
        }
    }

    @Override
    protected String[] doListChildren() throws Exception {
        List<String> childrenUris = new ArrayList<String>();
        if (cmisObject instanceof Folder) {
            Folder folder = (Folder) cmisObject;
            ItemIterable<CmisObject> childCmisObjects = folder.getChildren();
            for (CmisObject childCmisObject : childCmisObjects) {
                if (childCmisObject instanceof FileableCmisObject) {
                    FileableCmisObject fileableCmisObject = (FileableCmisObject) childCmisObject;
                    if (fileableCmisObject.getName() != null) {
                        childrenUris.add(fileableCmisObject.getName());
                    }
                } else {
                    childrenUris.add("?id=" + childCmisObject.getId());
                }
            }
        }
        return childrenUris.toArray(new String[childrenUris.size()]);
    }

    @Override
    protected long doGetContentSize() throws Exception {
        if (cmisObject instanceof Document) {
            Document document = (Document) cmisObject;
            return document.getContentStreamLength();
        }
        return 0;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        if (cmisObject instanceof Document) {
            Document document = (Document) cmisObject;
            return document.getContentStream().getStream();
        }
        return null;
    }
}
