/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 *
 * @author pierre
 */
public class ANI_FRAM_CIMG {

    final static String ID = "CIMG";
    final ChunkHead chunk;
    final short type;
    final short unknown1;
    final int extendedHeaderLength;
    final int unknown2;
    final short width;
    final short height;
    final short xpos;
    final short ypos;
    final short alphaColor;
    final short unknown3;
    //When palette is there
    final int unknown4;
    final int unknown5;
    final byte[] palette;
    //Then
    final int unknown6;
    final int comptressedSize;
    final int size;
    final ByteBuffer bitmap;

    public ANI_FRAM_CIMG(BinarySource in) throws IOException {
        chunk = ChunkHead.read(in).checkType(ID);
        type = in.readShort();
        unknown1 = in.readShort();
        extendedHeaderLength = in.readInt();
        unknown2 = in.readInt();
        width = in.readShort();
        height = in.readShort();
        xpos = in.readShort();
        ypos = in.readShort();
        alphaColor = in.readShort();
        unknown3 = in.readShort();
        //Is there a palette ?
        if (extendedHeaderLength > 32) {
            unknown4 = in.readInt();
            unknown5 = in.readInt();
            palette = in.readBytes(extendedHeaderLength - 32);
        } else {
            unknown4 = 0;
            unknown5 = 0;
            palette = null;
        }
        unknown6 = in.readInt();
        comptressedSize = in.readInt();
        size = in.readInt();
        bitmap = unpackRLE(in);
        //Skip additional data
        in.readBytes(chunk.available(in));
    }

    public BufferedImage createBuffuredImage() throws RuntimeException {
        switch (type) {
            case 0x04: {
                //16 bit per pixel
                bitmap.order(ByteOrder.LITTLE_ENDIAN);
                final ShortBuffer shortBuffer = bitmap.asShortBuffer();
                short[] shortBitMap = new short[shortBuffer.remaining()];
                bitmap.asShortBuffer().get(shortBitMap);
                for (int p = 0; p < shortBitMap.length; p++) {
                    final short v = shortBitMap[p];
                    shortBitMap[p] = (v == alphaColor) ? (short) 0x0000 : (short) (v | 0x8000);

                }
                // little endian 16 bit color
                // X1R5G5B5  xrrr rrgg gggb bbbb
                // Which coresponds to the followng masks
                // 0x7C000   0111 1100 0000 0000        
                // 0x03E0    0000 0011 1110 0000
                // 0x001F    0000 0000 0001 1111
                final int RED_MASK = 0x7C00,
                        GREEN_MASK = 0x03E0,
                        BLUE_MASK = 0x001F,
                        ALPHA_MASK = 0x8000;
                final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
                final DirectColorModel colorModel = new DirectColorModel(colorSpace, 16, RED_MASK, GREEN_MASK, BLUE_MASK, ALPHA_MASK, false, DataBuffer.TYPE_USHORT);
                final SampleModel sampleModel = colorModel.createCompatibleSampleModel(width, height);
                final DataBufferUShort dataBuffer = new DataBufferUShort(shortBitMap, shortBitMap.length);
                final WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
                return new BufferedImage(colorModel, raster, false, null);
            }


            case 0x0B: {
                final IndexColorModel colorModel = new IndexColorModel(8, 256, palette, 0, true, alphaColor);//RGBA palette
                final SampleModel sampleModel = colorModel.createCompatibleSampleModel(width, height);
                final DataBufferByte dataBuffer = new DataBufferByte(bitmap.array(), bitmap.capacity());
                final WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
                return new BufferedImage(colorModel, raster, false, null);
            }
            default:
                throw new RuntimeException("Unsuppoerted image type " + type);
        }
    }

    int bitPerPixel() {
        switch (type) {
            case 0x04:
                return 16;
            case 0x05:
                return 24;
            case 0x0A:
                return 4;
            case 0x0B:
                return 8;
            default:
                throw new RuntimeException("Unexpected image type");
        }
    }

    int bytesPerUnit() {
        switch (type) {
            case 0x04:
                return 2;
            case 0x05:
                return 3;
            case 0x0A:
                return 1;
            case 0x0B:
                return 1;
            default:
                throw new RuntimeException("Unexpected image type");
        }
    }

    private ByteBuffer unpackRLE(BinarySource in) throws IOException {
        final ByteBuffer buf = ByteBuffer.allocate(size);
        // the data unit that is repeated in an RLE block
        byte[] data = new byte[bytesPerUnit()];
        // and the number of times it is to be repeated
        // (or the number of raw bytes)

        while (chunk.contains(in) && buf.hasRemaining()) {

            final int controlByte = in.readByte();

            final boolean isCompressed = (controlByte & 0x80) != 0;
            final int count = (controlByte & 0x7F) + 1;

            // if bit 7 is set, this denotes the start of an RLE block
            if (isCompressed) {
                // unset bit 7 to get the repeat count minus one
                // the one unit will be added right below in this loop
                in.read(data);
                for (int i = 0; i < count; i++) {
                    buf.put(data);
                }
            } else {
                for (int i = 0; i < count; i++) {
                    in.read(data);
                    buf.put(data);
                }
            }
        }
        buf.rewind();
        return buf;
    }

    @Override
    public String toString() {
        return "ANI_FRAM_CIMG{" + "chunk=" + chunk + ", type=" + type + ", unknown1=" + unknown1 + ", extendedHeaderLength=" + extendedHeaderLength + ", unknown2=" + unknown2 + ", width=" + width + ", height=" + height + ", xpos=" + xpos + ", ypos=" + ypos + ", alphaColor=" + alphaColor + ", unknown3=" + unknown3 + ", unknown4=" + unknown4 + ", unknown5=" + unknown5 + ", palette=" + palette + ", unknown6=" + unknown6 + ", comptressedSize=" + comptressedSize + ", size=" + size + ", bitmap=" + bitmap + '}';
    }

    public short getXpos() {
        return xpos;
    }

    public short getYpos() {
        return ypos;
    }
    
}
