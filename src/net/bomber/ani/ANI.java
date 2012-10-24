/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import static com.google.common.collect.Iterables.*;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.bomber.ImageFrame;
import net.bomber.Indenter;

/**
 *
 * @author pierre
 */
public class ANI {

    final static String ID = "ANI ";
    public final ChunkHead chunk;
    public final List<Object> objects = Lists.newArrayList();
    public final String sourceName;

    public ANI(final BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        while (chunk.contains(in)) {
            final ChunkHead subChunk = ChunkHead.readAhead(in);
            switch (subChunk.type) {
                case ANI_CBOX.ID:
                    objects.add(new ANI_CBOX(in));
                    break;
                case ANI_FRAM.ID:
                    objects.add(new ANI_FRAM(in));
                    break;
                case ANI_SEQ.ID:
                    objects.add(new ANI_SEQ(in));
                    break;
                // Ignored chunks
                case "TPAL":
                case "PAL ":
                case "HEAD":
                    objects.add(new UNKNOWN(in));
                    break;
                default:
                    throw in.failure("Unexpected chunk " + subChunk);

            }
            subChunk.checkOverflow(in);
        }
        
        sourceName = new File(in.resource.getFile()).getName();
        System.out.println(sourceName);
        
    }

    public void show() throws IOException, RuntimeException {                
        for (final ANI_FRAM fram : filter(objects, ANI_FRAM.class)) {
            final ANI_FRAM_FNAM fnam = getFirst(filter(fram.objects, ANI_FRAM_FNAM.class), null);
            final String name = fnam == null ? "#undef" : fnam.filename;
            for (ANI_FRAM_CIMG cimg : filter(fram.objects, ANI_FRAM_CIMG.class)) {
                ImageFrame.addImage(sourceName, name, cimg.createBuffuredImage());
            }
        }
        final StringBuilder stringBuilder = new StringBuilder();
        new Indenter(stringBuilder).append(toString());
        ImageFrame.setText(sourceName, stringBuilder.toString());
    }
    
    public Iterable<ANI_SEQ> sequences(){
        return filter(objects, ANI_SEQ.class);
    }

    @Override
    public String toString() {
        return "ANI{" + "chunk=" + chunk + ", objects=" + objects + '}';
    }
}
