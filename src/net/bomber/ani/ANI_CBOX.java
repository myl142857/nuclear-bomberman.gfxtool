package net.bomber.ani;

import java.io.IOException;

/**
 *
 * @author pierre
 */
public class ANI_CBOX {

    final static String ID = "CBOX";
    final ChunkHead chunk;
    final short width;
    final short height;

    public ANI_CBOX(final BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        width = in.readShort();
        height = in.readShort();
    }

    @Override
    public String toString() {
        return "CBOX{" + "head=" + chunk + ", width=" + width + ", height=" + height + '}';
    }
}
