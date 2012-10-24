/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import static com.google.common.collect.Iterables.*;
import net.bomber.ani.ANI_SEQ_STAT_FRAM.Step;


/**
 *
 * @author pierre
 */
public class ANI_SEQ_STAT {

    final static String ID = "STAT";
    final ChunkHead chunk;
    final List<Object> objects = Lists.newArrayList();
    final ANI_SEQ_STAT_HEAD theHead;
    final ANI_SEQ_STAT_FRAM theFram;

    public ANI_SEQ_STAT(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);

        while (chunk.contains(in)) {
            final ChunkHead subChunk = ChunkHead.readAhead(in);
            switch (subChunk.type) {
                case ANI_SEQ_STAT_FRAM.ID:
                    objects.add(new ANI_SEQ_STAT_FRAM(in));
                    break;
                case ANI_SEQ_STAT_HEAD.ID:
                    objects.add(new ANI_SEQ_STAT_HEAD(in));
                    break;
                default:
                    throw in.failure("Unexpected chunk " + subChunk);

            }
            subChunk.checkOverflow(in);
        }

        theHead = getOnlyElement(filter(objects, ANI_SEQ_STAT_HEAD.class));
        theFram = getOnlyElement(filter(objects, ANI_SEQ_STAT_FRAM.class));
    }

    @Override
    public String toString() {
        return "ANI_SEQ_STAT{" + "chunk=" + chunk + ", objects=" + objects + '}';
    }

    public int displayTimeMs() {
        return theHead.displayTimeMs;
    }

    
    public Iterable<Step> steps() {
        return theFram.steps;
    }
}
