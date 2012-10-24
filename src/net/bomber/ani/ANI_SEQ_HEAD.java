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
class ANI_SEQ_HEAD {

    final static String ID = "HEAD";
    final ChunkHead chunk;
    final String name;
    short xOffset;
    short yOffset;
    short variables[];
    short statCount;
    byte unknown[];

    public ANI_SEQ_HEAD(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        name = in.readASCII(42).split("\u0000")[0];
        xOffset = in.readShort();
        yOffset = in.readShort();
        variables = in.readShorts(16);
        statCount = in.readShort();
        unknown = in.readBytes(16);
    }

    @Override
    public String toString() {
        return "ANI_SEQ_HEAD{" + "chunk=" + chunk + ", name=" + name + ", xOffset=" + xOffset + ", yOffset=" + yOffset + ", variables=" + variables + ", statCount=" + statCount + ", unknown=" + unknown + '}';
    }
    
    
}
