//------------------------------------------------------------------------------
// Copyright (c) 2002-2021 Kofax. All rights reserved.
// Description : CanonFileInputStream
//------------------------------------------------------------------------------

package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Canon FileInputStream implementation. On MEAP we have to embed all file
 * operations in AccessController.doPrivileged() blocks in order to make file
 * access work through different user sessions on the device.
 *
 * @author Bartha_Sebestyen
 *
 */
public class CanonFileInputStream extends FileInputStream {

    public CanonFileInputStream(final NeufFile file) throws FileNotFoundException {
        super(file);

    }

    @Override
    public int read() throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws IOException {
                    return CanonFileInputStream.super.read();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public int read(final byte[] b) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws IOException {
                    return CanonFileInputStream.super.read(b);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws IOException {
                    return CanonFileInputStream.super.read(b, off, len);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public long skip(final long n) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Long>() {
                @Override
                public Long run() throws IOException {
                    return CanonFileInputStream.super.skip(n);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }

    }

    @Override
    public int available() throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws IOException {
                    return CanonFileInputStream.super.available();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws IOException {
                    CanonFileInputStream.super.close();
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
                return CanonFileInputStream.super.getChannel();
            }
        });
    }

    @Override
    protected void finalize() throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    CanonFileInputStream.super.finalize();
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }
}