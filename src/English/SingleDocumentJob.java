//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : SingleDocumentJob
//------------------------------------------------------------------------------


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.canon.meap.imaging.ImagingException;
import persistence.NeufFile;
import persistence.PersistenceFactory;

class SingleDocumentJob {
    private final String location;
    private boolean firstPage = true;
    private PdfDocumentWriter writer = null;
    private OutputStream os = null;
    private NeufFile file = null;

    SingleDocumentJob(final String location) {
        this.location = location;
    }

    public void addPage(long imageHandle) throws ImagingException, IOException {
        if (firstPage) {
            createDocumentWriter();
        }
        writer.addPage(imageHandle);
    }

    private void createDocumentWriter() {
        try {
          PersistenceFactory pFactory = new PersistenceFactory(AppletActivator.bundleContext);
            file = pFactory.createFile(location + File.separator + "image" + ".pdf");// new File(location + File.separator + "image" + ".pdf");
            os = pFactory.createOutputStream(file, false); //new FileOutputStream(file);
        } catch (IOException e) {
//            logger.e("failed to create output file", e);
        }
        writer = new PdfDocumentWriter(os);
        firstPage = false;
    }

    public List<String> getDocumentList() {
        try {
            if (null != writer) {
                closeDocumentWriter();
            }
            if (null != file) {
                return Arrays.asList(file.getAbsolutePath());
            }
        } catch (IOException e) {
//            logger.e("failed to get path of the output file", e);
        }
        return new ArrayList<String>();
    }

    public void endJob() throws IOException {
        closeDocumentWriter();
    }

    private void closeDocumentWriter() throws IOException {
        // If any of the close methods throws an exception, it indicates a document creation error and we should fail the scan.
        IOException exception = null;
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ioe) {
//            logger.e("Failed to close document writer", ioe);
            exception = ioe;
        }

        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
//            logger.e("Failed to close the output stream", e);
            exception = e;
        }

        if (exception != null) {
            throw exception;
        }
    }
}