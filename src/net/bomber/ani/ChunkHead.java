/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import java.io.IOException;

/**
 *
 * @author pierre
 */
class ChunkHead {

    final int start;
    final String type;
    final int length;
    final short id;

    public static ChunkHead readAhead(final BinarySource in) throws IOException {
        in.mark(10);
        final ChunkHead result = new ChunkHead(in);
        in.reset();
        return result;
    }

    public static ChunkHead read(final BinarySource in) throws IOException {
        return new ChunkHead(in);
    }

    private ChunkHead(final BinarySource in) throws IOException {
        start = in.getPosition();
        type = in.readASCII(4);
        length = in.readInt();
        id = in.readShort();
    }

    int available(BinarySource in) {
        return (start + 10 + length) - in.getPosition();
    }

    boolean contains(BinarySource in) {
        return available(in) > 0;
    }

    void checkOverflow(BinarySource in) {
        final int available = available(in);
        if (available > 0) {
            throw new RuntimeException("" + available + " bytes remain to be read in the chunk " + this);
        }
        if (available < 0) {
            throw new RuntimeException("" + available + " bytes over readed in the chunk " + this);
        }
    }

    @Override
    public String toString() {
        return "Chunk{" + "start=" + start + ", type=" + type + ", len=" + length + ", id=" + id + '}';
    }

    ChunkHead checkType(String expectedType) {
        if (type.equals(expectedType) == false) {
            throw new RuntimeException("Unexpected type of chunk. Expected:" + expectedType + " Found:" + type);
        }
        return this;
    }
}
