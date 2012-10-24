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
class ANI_SEQ_STAT_HEAD {

    final static String ID = "HEAD";
    final ChunkHead chunk;
    final short displayTimeMs;
    final short xMoveOffset; //Allways 0
    final short yMoveOffset;//Allways 0
    final short variable[];
    final byte unknwon[];

    public ANI_SEQ_STAT_HEAD(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        displayTimeMs = in.readShort();
        xMoveOffset = in.readShort();
        yMoveOffset = in.readShort();
        if (xMoveOffset != 0) {
            throw new RuntimeException();
        }
        if (yMoveOffset != 0) {
            throw new RuntimeException();
        }
        variable = in.readShorts(16);
        unknwon = in.readBytes(8);
    }

    @Override
    public String toString() {
        return "ANI_SEQ_STAT_HEAD{" + "chunk=" + chunk + ", displayTimeMs=" + displayTimeMs + ", xMoveOffset=" + xMoveOffset + ", yMoveOffset=" + yMoveOffset + ", variable=" + variable + ", unknwon=" + unknwon + '}';
    }
}
