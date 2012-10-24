/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author pierre
 */
public class ANI_SEQ_STAT_FRAM {

    final static String ID = "FRAM";
    final ChunkHead chunk;
    final short stepCount;
    final List<Step> steps = Lists.newArrayList();

    public ANI_SEQ_STAT_FRAM(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        stepCount = in.readShort();
        for (int stepIdx = 0; stepIdx < stepCount; stepIdx++) {
            steps.add(new Step(in));
        }
    }

    public static class Step {

        final short imageIndex;
        final short xMoveOffset;
        final short yMoveOffset;
        final short flip;
        final short unknown;

        public Step(BinarySource in) throws IOException {
            imageIndex = in.readShort();
            xMoveOffset = in.readShort();
            yMoveOffset = in.readShort();
            flip = in.readShort();
            unknown = in.readShort();
        }

        public short getFlip() {
            return flip;
        }

        public short getImageIndex() {
            return imageIndex;
        }

        public short getxMoveOffset() {
            return xMoveOffset;
        }

        public short getyMoveOffset() {
            return yMoveOffset;
        }

        
        @Override
        public String toString() {
            return "Step{" + "imageIndex=" + imageIndex + ", xMoveOffset=" + xMoveOffset + ", yMoveOffset=" + yMoveOffset + ", flip=" + flip + ", unknown=" + unknown + '}';
        }
    }

    @Override
    public String toString() {
        return "ANI_SEQ_STAT_FRAM{" + "chunk=" + chunk + ", stepCount=" + stepCount + ", steps=" + steps + '}';
    }
}
