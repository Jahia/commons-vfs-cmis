package org.jahia.server.commons.vfs.provider.cmis;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

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
    private CmisFileSystem cmisFileSystem;
    private String mimeType;
    private boolean alreadyClosed = false;

    public CmisOutputStream(CmisFileObject cmisFileObject, CmisFileSystem cmisFileSystem, String mimeType) {
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
            CmisFileName cmisFileName = (CmisFileName) cmisFileObject.getName();
            CmisFileObject parentCmisFileObject = (CmisFileObject) cmisFileSystem.resolveFile(cmisFileName.getParent());
            if (parentCmisFileObject.getCmisObject() instanceof Folder) {
                Folder parentFolder = (Folder) parentCmisFileObject.getCmisObject();

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                ContentStream contentStream = cmisFileSystem.getSession(cmisFileName).getObjectFactory().createContentStream(cmisFileName.getBaseName(), byteArrayOutputStream.size(), mimeType, byteArrayInputStream);

                Map<String, String> documentProperties = new HashMap<String, String>();
                documentProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                documentProperties.put(PropertyIds.NAME, cmisFileName.getBaseName());

                Document document = parentFolder.createDocument(documentProperties, contentStream, VersioningState.NONE);
                cmisFileObject.setCmisObject(document);
            }
        } else {
            CmisFileName cmisFileName = (CmisFileName) cmisFileObject.getName();
            Document document = (Document) cmisFileObject.getCmisObject();
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            ContentStream contentStream = cmisFileSystem.getSession(cmisFileName).getObjectFactory().createContentStream(cmisFileName.getBaseName(), byteArrayOutputStream.size(), mimeType, byteArrayInputStream);
            document.setContentStream(contentStream, true);
        }
        alreadyClosed = true;
        super.close();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
