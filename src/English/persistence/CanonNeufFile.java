//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : CanonNeufFile
//------------------------------------------------------------------------------

package persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;


/**
 * Canon File implementation. On MEAP we have to embed all file operations in
 * AccessController.doPrivileged() blocks in order to make file access work
 * through different user sessions on the device.
 *
 *
 * @author Bartha_Sebestyen
 *
 */
public final class CanonNeufFile extends NeufFile {
    private static final long serialVersionUID = -214360186802357685L;

    private File wrappedFile;

    protected CanonNeufFile(final String path) {
        super(path);
        wrappedFile = new File(path);
    }

    private CanonNeufFile(final File file) {
        super(file.getPath());
        wrappedFile = file;
    }

    @Override
    public String getAbsolutePath() {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return wrappedFile.getAbsolutePath();
            }
        });
    }

    @Override
    public CanonNeufFile getAbsoluteFile() {
        return AccessController.doPrivileged(new PrivilegedAction<CanonNeufFile>() {
            @Override
            public CanonNeufFile run() {
                return new CanonNeufFile(wrappedFile.getAbsoluteFile());
            }
        });
    }

    @Override
    public CanonNeufFile getParentFile() {
        return AccessController.doPrivileged(new PrivilegedAction<CanonNeufFile>() {

            @Override
            public CanonNeufFile run() {
                return new CanonNeufFile(wrappedFile.getParentFile());
            }
        });
    }

    @Override
    public String getCanonicalPath() throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws IOException {
                    return wrappedFile.getCanonicalPath();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public CanonNeufFile getCanonicalFile() throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<CanonNeufFile>() {
                @Override
                public CanonNeufFile run() throws IOException {
                    return new CanonNeufFile(wrappedFile.getCanonicalFile());
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public URI toURI() {
        return AccessController.doPrivileged(new PrivilegedAction<URI>() {
            @Override
            public URI run() {
                return wrappedFile.toURI();
            }
        });
    }

    @Override
    public boolean canRead() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.canRead();
            }
        });
    }

    @Override
    public boolean canWrite() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.canWrite();
            }
        });
    }

    @Override
    public boolean exists() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.exists();
            }
        });
    }

    @Override
    public boolean isDirectory() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.isDirectory();
            }
        });
    }

    @Override
    public boolean isFile() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.isFile();
            }
        });
    }

    @Override
    public boolean isHidden() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.isHidden();
            }
        });
    }

    @Override
    public long lastModified() {
        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return wrappedFile.lastModified();
            }
        });
    }

    @Override
    public long length() {
        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return wrappedFile.length();
            }
        });
    }

    @Override
    public boolean createNewFile() throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws IOException {
                    return wrappedFile.createNewFile();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public boolean delete() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.delete();
            }
        });
    }

    @Override
    public void deleteOnExit() {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                wrappedFile.deleteOnExit();
                return null;
            }
        });
    }

    @Override
    public String[] list() {
        return AccessController.doPrivileged(new PrivilegedAction<String[]>() {
            @Override
            public String[] run() {
                return wrappedFile.list();
            }
        });
    }

    @Override
    public String[] list(final FilenameFilter filter) {
        return AccessController.doPrivileged(new PrivilegedAction<String[]>() {
            @Override
            public String[] run() {
                return wrappedFile.list(filter);
            }
        });
    }

    @Override
    public CanonNeufFile[] listFiles() {
        return AccessController.doPrivileged(new PrivilegedAction<CanonNeufFile[]>() {
            @Override
            public CanonNeufFile[] run() {
                return toCanonNeufFileArray(wrappedFile.listFiles());
            }
        });
    }

    @Override
    public CanonNeufFile[] listFiles(final FilenameFilter filter) {
        return AccessController.doPrivileged(new PrivilegedAction<CanonNeufFile[]>() {
            @Override
            public CanonNeufFile[] run() {
                return toCanonNeufFileArray(wrappedFile.listFiles(filter));
            }
        });
    }

    @Override
    public CanonNeufFile[] listFiles(final FileFilter filter) {
        return AccessController.doPrivileged(new PrivilegedAction<CanonNeufFile[]>() {
            @Override
            public CanonNeufFile[] run() {
                return toCanonNeufFileArray(wrappedFile.listFiles(filter));
            }
        });
    }

    private CanonNeufFile[] toCanonNeufFileArray(final File[] fileArray) {
        if (fileArray != null) {
            CanonNeufFile[] retArray = new CanonNeufFile[fileArray.length];

            for (int i = 0; i < fileArray.length; i++) {
                retArray[i] = new CanonNeufFile(fileArray[i]);
            }

            return retArray;
        } else {
            return null;
        }
    }

    @Override
    public boolean mkdir() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.mkdir();
            }
        });
    }

    @Override
    public boolean mkdirs() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.mkdirs();
            }
        });
    }

    @Override
    public boolean renameTo(final File dest) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.renameTo(dest);
            }
        });
    }

    @Override
    public boolean setLastModified(final long time) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setLastModified(time);
            }
        });
    }

    @Override
    public boolean setReadOnly() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setReadOnly();
            }
        });
    }

    @Override
    public boolean setWritable(final boolean writable, final boolean ownerOnly) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setWritable(writable, ownerOnly);
            }
        });
    }

    @Override
    public boolean setWritable(final boolean writable) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setWritable(writable, true);
            }
        });
    }

    @Override
    public boolean setReadable(final boolean readable, final boolean ownerOnly) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setReadable(readable, ownerOnly);
            }
        });
    }

    @Override
    public boolean setReadable(final boolean readable) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setReadable(readable, true);
            }
        });
    }

    @Override
    public boolean setExecutable(final boolean executable, final boolean ownerOnly) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setExecutable(executable, ownerOnly);
            }
        });
    }

    @Override
    public boolean setExecutable(final boolean executable) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.setExecutable(executable, true);
            }
        });
    }

    @Override
    public boolean canExecute() {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return wrappedFile.canExecute();
            }
        });
    }

    @Override
    public long getTotalSpace() {
        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return wrappedFile.getTotalSpace();
            }
        });
    }

    @Override
    public long getFreeSpace() {
        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return wrappedFile.getFreeSpace();
            }
        });
    }

    @Override
    public long getUsableSpace() {
        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return wrappedFile.getUsableSpace();
            }
        });
    }

    public static File createTempFile(final String prefix, final String suffix, final File directory) throws IOException {
        throw new UnsupportedOperationException("createTempFile() function is disabled");
    }

    public static File createTempFile(final String prefix, final String suffix) throws IOException {
        throw new UnsupportedOperationException("createTempFile() function is disabled");
    }
}