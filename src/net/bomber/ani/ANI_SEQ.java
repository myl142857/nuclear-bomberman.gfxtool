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
public class ANI_SEQ {

    final static String ID = "SEQ ";
    final ChunkHead chunk;
    final List<Object> objects = Lists.newArrayList();
    
    final ANI_SEQ_HEAD theHead; 

    public ANI_SEQ(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        while (chunk.contains(in)) {
            final ChunkHead subChunk = ChunkHead.readAhead(in);
            switch (subChunk.type) {
                case ANI_SEQ_STAT.ID:
                    objects.add(new ANI_SEQ_STAT(in));
                    break;
                case ANI_SEQ_HEAD.ID:
                    objects.add(new ANI_SEQ_HEAD(in));
                    break;
                default:
                    throw in.failure("Unexpected chunk " + subChunk);

            }
            subChunk.checkOverflow(in);
        }
        
        theHead=getOnlyElement(filter(objects,ANI_SEQ_HEAD.class));
    }

    @Override
    public String toString() {
        return "ANI_SEQ{" + "chunk=" + chunk + ", objects=" + objects + '}';
    }

    public String name() {
        return theHead.name;
    }

    public Iterable<ANI_SEQ_STAT> stats() {
     return filter(objects, ANI_SEQ_STAT.class);
    }

   
}
