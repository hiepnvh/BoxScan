//------------------------------------------------------------------------------
// Copyright (c) 2002-2022 Kofax. All rights reserved.
// Description : PdfDocumentWriter
//------------------------------------------------------------------------------

import java.io.IOException;
import java.io.OutputStream;
import com.canon.meap.imaging.DocumentWriter;
import com.canon.meap.imaging.DocumentWriterFactory;
import com.canon.meap.imaging.ImagingException;

public class PdfDocumentWriter {
    private DocumentWriter dw = null;

    PdfDocumentWriter(final OutputStream os) {
            dw = DocumentWriterFactory.createDocumentWriter("PDF", os);
    }

    public void addPage(long imageHandle) throws IOException, ImagingException {
        try {
            dw.add(imageHandle);
        } catch (IOException e) {
            releaseResources();
            throw e;
        }
    }

    public void close() throws IOException {
        releaseResources();
    }

    private void releaseResources() throws IOException {
        if (dw != null) {
            dw.terminate();
        }
    }
}