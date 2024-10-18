//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : PersistenceFactory
//------------------------------------------------------------------------------
package persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.framework.BundleContext;

public class PersistenceFactory {
    private BundleContext bundleContext;
    
    public PersistenceFactory(BundleContext bundleContext) {
      this.bundleContext = bundleContext;
    }

    /**
     * NOTE: unlike as defined in the original API, this method is able to
     * handle both absolute AND relative pathNames.
     *
     * @see com.nuance.ndi.framework.persistence.IPersistenceFactory#createFile(java.lang.String)
     */
    public final NeufFile createFile(final String pathName) throws IOException {
        return AccessController.doPrivileged(new PrivilegedAction<NeufFile>() {
            @Override
            public NeufFile run() {
//                logD("createFile: " + pathName);
                NeufFile file;
                if (Paths.get(pathName).isAbsolute()) {
                    file = new CanonNeufFile(pathName);
                } else {
                    String firstPathname = pathName;

                    // if child path is a full path file URL, than strip off the leading part
                    if (firstPathname.startsWith("file:/")) {
                        try {
                            firstPathname = new File(new URI(firstPathname)).getAbsolutePath();
                        } catch (URISyntaxException e) {
                            throw new IllegalArgumentException("Invalid file URI");
                        }
                    }

                    String rootFolderAbsolutePath = bundleContext.getDataFile("").getAbsolutePath();
                    int indexOf = firstPathname.indexOf(rootFolderAbsolutePath);
                    if (indexOf == 0) {
                        firstPathname = firstPathname.replace(rootFolderAbsolutePath, "");
                    }

                    // strip off the leading file separators
                    if (firstPathname != null && firstPathname.startsWith(File.separator)) {
                        while (firstPathname.startsWith(File.separator)) {
                            firstPathname = firstPathname.substring(1);
                        }
//                        logD("childPathname after removed leading file seperator: " + firstPathname);
                    }

                    file = new CanonNeufFile(bundleContext.getDataFile(firstPathname).getAbsolutePath());
                }
                return file;
            }
        });
    }

    public final FileInputStream createInputStream(final NeufFile file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
                @Override
                public FileInputStream run() throws FileNotFoundException {
                    return new CanonFileInputStream(file);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (FileNotFoundException) e.getException();
        }
    }

    public final FileOutputStream createOutputStream(final NeufFile file, final boolean append) throws FileNotFoundException {
        try {
            file.getParentFile().mkdirs();
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FileOutputStream>() {
                @Override
                public FileOutputStream run() throws FileNotFoundException {
                    return new CanonFileOutputStream(file, append);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (FileNotFoundException) e.getException();
        }
    }

    public final void close(final FileInputStream is) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    is.close();
                } catch (IOException e) {
//                    logE("could not close input stream", e);
                }
                return null;
            }
        });
    }

    public final void close(final FileOutputStream os) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    os.close();
                } catch (IOException e) {
//                    logE("could not close output stream", e);
                }
                return null;
            }
        });
    }
}
