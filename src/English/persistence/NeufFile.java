//------------------------------------------------------------------------------
// Copyright (c) 2021 Kofax. All rights reserved.
// Description : NeufFile
//------------------------------------------------------------------------------

package persistence;

import java.io.File;
import java.net.MalformedURLException;

public abstract class NeufFile extends File {
    private static final long serialVersionUID = 2595475749216574481L;

    public static final String NEUFFILE_URI_HEADER = "neuffile://";
    private static final String FILE_HEADER = "file:";

    protected NeufFile(String child) {
        super(child);
    }

    protected NeufFile(File base, String child) {
        super(base, child);
    }

    public File getFile() {
        return this;
    }

    /**
     * Make sure we are not addressing files outside of persistent factory
     * accessible root folder. By abstracting this method each implementor has
     * the chance to throw and exception and force the client: do not jump over
     * a fence!
     */
    @Override
    public abstract NeufFile getParentFile();

    public String getNeufFileURLAsString() throws MalformedURLException {
        String retVal = toURI().toURL().toString();
        int endOfSchemeIndex = FILE_HEADER.length();

        retVal = NEUFFILE_URI_HEADER + retVal.substring(endOfSchemeIndex);

        return retVal;
    }
}