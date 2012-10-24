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
class UNKNOWN {

    final ChunkHead chunk;

    public UNKNOWN(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in);
        in.readBytes(chunk.length);
    }

    @Override
    public String toString() {
        return "UNKNOWN{" + "chunk=" + chunk + '}';
    }
    
}
