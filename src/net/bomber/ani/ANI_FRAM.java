/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import static com.google.common.collect.Iterables.*;
/**
 *
 * @author pierre
 */
public class ANI_FRAM {

    final static String ID = "FRAM";
    public final ChunkHead chunk;
    public final List<Object> objects = Lists.newArrayList();

    final ANI_FRAM_CIMG theImg;
    public ANI_FRAM(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        while (chunk.contains(in)) {
            final ChunkHead subChunk = ChunkHead.readAhead(in);
            switch (subChunk.type) {
                case "FNAM":
                    objects.add(new ANI_FRAM_FNAM(in));
                    break;
                case "CIMG":
                    objects.add(new ANI_FRAM_CIMG(in));
                    break;
                case "ATBL":
                case "HEAD":
                    objects.add(new UNKNOWN(in));
                    break;
                default:
                    throw in.failure("Unexpected chunk");
            }
            subChunk.checkOverflow(in);
        }
        theImg=getOnlyElement(filter(objects, ANI_FRAM_CIMG.class));
    }

    @Override
    public String toString() {
        return "ANI_FRAM{" + "chunk=" + chunk + ", objects=" + objects + '}';
    }
    
    
}
