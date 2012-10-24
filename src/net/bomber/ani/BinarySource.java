/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.io.*;
import java.net.URL;

/**
 *
 * @author pierre
 */
public class BinarySource extends FilterInputStream implements DataInput {

    final URL resource;

    public BinarySource(URL resource) throws IOException {
        super(new CountingInputStream(resource.openStream()));
        this.resource = resource;
    }

    RuntimeException failure(final String message) {
        return new RuntimeException(message + " at " + resource.toString() + "[" + getPosition() + "]");
    }

    public int getPosition() {
        return (int) ((CountingInputStream) in).getCount();
    }

    void verifyBytes(byte[] expected) throws IOException {
        for (int i = 0; i < expected.length; i++) {
            if (readByte() != expected[i]) {
                throw failure("byte verification failed ");
            }
        }
    }

    /**
     * This method will throw an {@link UnsupportedOperationException}.
     */
    @Override
    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        ByteStreams.readFully(this, b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        ByteStreams.readFully(this, b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return (int) in.skip(n);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b1 = in.read();
        if (0 > b1) {
            throw new EOFException();
        }

        return b1;
    }

    /**
     * Reads an unsigned {@code short} as specified by
     * {@link DataInputStream#readUnsignedShort()}, except using little-endian
     * byte order.
     *
     * @return the next two bytes of the input stream, interpreted as an
     * unsigned 16-bit integer in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int readUnsignedShort() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();

        return Ints.fromBytes((byte) 0, (byte) 0, b2, b1);
    }

    /**
     * Reads an integer as specified by {@link DataInputStream#readInt()},
     * except using little-endian byte order.
     *
     * @return the next four bytes of the input stream, interpreted as an
     *         {@code int} in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int readInt() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();
        byte b3 = readAndCheckByte();
        byte b4 = readAndCheckByte();

        return Ints.fromBytes(b4, b3, b2, b1);
    }

    /**
     * Reads a {@code long} as specified by {@link DataInputStream#readLong()},
     * except using little-endian byte order.
     *
     * @return the next eight bytes of the input stream, interpreted as a
     *         {@code long} in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long readLong() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();
        byte b3 = readAndCheckByte();
        byte b4 = readAndCheckByte();
        byte b5 = readAndCheckByte();
        byte b6 = readAndCheckByte();
        byte b7 = readAndCheckByte();
        byte b8 = readAndCheckByte();

        return Longs.fromBytes(b8, b7, b6, b5, b4, b3, b2, b1);
    }

    /**
     * Reads a {@code float} as specified by {@link DataInputStream#readFloat()},
     * except using little-endian byte order.
     *
     * @return the next four bytes of the input stream, interpreted as a
     *         {@code float} in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a {@code double} as specified by
     * {@link DataInputStream#readDouble()}, except using little-endian byte
     * order.
     *
     * @return the next eight bytes of the input stream, interpreted as a
     *         {@code double} in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readUTF() throws IOException {
        return new DataInputStream(in).readUTF();
    }

    /**
     * Reads a {@code short} as specified by {@link DataInputStream#readShort()},
     * except using little-endian byte order.
     *
     * @return the next two bytes of the input stream, interpreted as a
     *         {@code short} in little-endian byte order.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    /**
     * Reads a char as specified by {@link DataInputStream#readChar()}, except
     * using little-endian byte order.
     *
     * @return the next two bytes of the input stream, interpreted as a
     *         {@code char} in little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public char readChar() throws IOException {
        return (char) readUnsignedShort();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) readUnsignedByte();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readUnsignedByte() != 0;
    }

    /**
     * Reads a byte from the input stream checking that the end of file (EOF)
     * has not been encountered.
     *
     * @return byte read from input
     * @throws IOException if an error is encountered while reading
     * @throws EOFException if the end of file (EOF) is encountered.
     */
    private byte readAndCheckByte() throws IOException, EOFException {
        int b1 = in.read();

        if (-1 == b1) {
            throw new EOFException();
        }

        return (byte) b1;
    }

    byte[] readBytes(int len) throws IOException {
        byte[] result = new byte[len];
        if (read(result) != len) {
            throw failure("Unexpected end of stream");
        }
        return result;
    }

    String readASCII(int i) throws IOException {
        return new String(readBytes(i), Charsets.US_ASCII);
    }

    short[] readShorts(int count) throws IOException {
        short[] result = new short[count];
        for (int i = 0; i < count; i++) {
            result[i] = readShort();
        }
        return result;
    }
}
