package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.commons.vfs.provider.LayeredFileName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom CMIS output stream implementation to implement the actual document creation when the output stream is
 * closed.
 */
public class CmisOutputStream extends OutputStream {

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private CmisFileObject cmisFileObject;
    private AbstractCmisFileSystem cmisFileSystem;
    private String mimeType;
    private boolean alreadyClosed = false;

    public CmisOutputStream(CmisFileObject cmisFileObject, AbstractCmisFileSystem cmisFileSystem, String mimeType) {
        this.cmisFileObject = cmisFileObject;
        this.cmisFileSystem = cmisFileSystem;
        this.mimeType = mimeType;
    }

    @Override
    public void write(int i) throws IOException {
        byteArrayOutputStream.write(i);
    }

    @Override
    public void close() throws IOException {
        if (alreadyClosed) {
            super.close();
            return;
        }
        if (cmisFileObject.getCmisObject() == null) {
            LayeredFileName layeredFileName = (LayeredFileName) cmisFileObject.getName();
            CmisFileObject parentCmisFileObject = (CmisFileObject) cmisFileSystem.resolveFile(layeredFileName.getParent());
            if (parentCmisFileObject.getCmisObject() instanceof Folder) {
                Folder parentFolder = (Folder) parentCmisFileObject.getCmisObject();

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                ContentStream contentStream = cmisFileSystem.getSession().getObjectFactory().createContentStream(layeredFileName.getBaseName(), byteArrayOutputStream.size(), mimeType, byteArrayInputStream);

                Map<String, String> documentProperties = new HashMap<String, String>();
                documentProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                documentProperties.put(PropertyIds.NAME, layeredFileName.getBaseName());

                DocumentType documentType = (DocumentType) cmisFileSystem.getSession().getTypeDefinition("cmis:document");
                Document document = null;
                if (documentType.isVersionable()) {
                    document = parentFolder.createDocument(documentProperties, contentStream, VersioningState.MAJOR);
                } else {
                    document = parentFolder.createDocument(documentProperties, contentStream, VersioningState.NONE);
                }
                cmisFileObject.setCmisObject(document);
            }
        } else {
            LayeredFileName layeredFileName = (LayeredFileName) cmisFileObject.getName();
            Document document = (Document) cmisFileObject.getCmisObject();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            ContentStream contentStream = cmisFileSystem.getSession().getObjectFactory().createContentStream(layeredFileName.getBaseName(), byteArrayOutputStream.size(), mimeType, byteArrayInputStream);
            DocumentType documentType = (DocumentType) cmisFileSystem.getSession().getTypeDefinition("cmis:document");
            if (documentType.isVersionable()) {
                document = (Document) cmisFileSystem.getSession().getObject(document.checkOut());
                ObjectId objectId = document.checkIn(true, null, contentStream, "VFS update");
                cmisFileObject.setCmisObject(cmisFileSystem.getSession().getObject(objectId));
            } else {
                document.setContentStream(contentStream, true);
            }
        }
        alreadyClosed = true;
        super.close();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
