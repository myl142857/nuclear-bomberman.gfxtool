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
public class ANI_FRAM_FNAM {

    final static String ID = "FNAM";
    public final ChunkHead chunk;
    public final String filename;

    public ANI_FRAM_FNAM(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        filename = in.readASCII(chunk.length - 1);
        final byte zero = in.readByte();
        if (zero != 0) {
            throw new RuntimeException("Missing null terminal character");
        }
    }

    @Override
    public String toString() {
        return "ANI_FRAM_FNAM{" + "chunk=" + chunk + ", filename=" + filename + '}';
    }
}
