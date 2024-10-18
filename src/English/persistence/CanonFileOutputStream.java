//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : CanonFileOutputStream
//------------------------------------------------------------------------------

package persistence;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Canon FileOutputStream implementation. On MEAP we have to embed all file
 * operations in AccessController.doPrivileged() blocks in order to make file
 * access work through different user sessions on the device.
 *
 * @author Bartha_Sebestyen
 *
 */
public class CanonFileOutputStream extends FileOutputStream {

    /**
     * @param file
     * @param append
     */
    public CanonFileOutputStream(final NeufFile file, final boolean append) throws FileNotFoundException {
        super(file, append);
    }

    @Override
    public void write(final int b) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileOutputStream.super.write(b);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public void write(final byte[] b) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileOutputStream.super.write(b);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileOutputStream.super.write(b, off, len);
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileOutputStream.super.close();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public FileChannel getChannel() {
        return AccessController.doPrivileged(new PrivilegedAction<FileChannel>() {
            @Override
            public FileChannel run() {
                return CanonFileOutputStream.super.getChannel();
            }
        });
    }

    @Override
    protected void finalize() throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileOutputStream.super.finalize();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

}