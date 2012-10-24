package net.bomber;

import com.google.common.collect.Lists;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author pierre
 */
public class SpriteSheetBuilder {

    final List<Element> elements = Lists.newArrayList();

    public SpriteSheetBuilder() {
    }

    public Element addImage( BufferedImage image) {
        final Element element = new Element(image);
        elements.add(element);
        return element;
    }

    BufferedImage build() {
        Collections.sort(elements, HeightDescending.INSTANCE);
        for (int size = 1024;; size <<= 1) {
            final BufferedImage result = build(size);
            if (result != null) {
                return result;
            }
        }
    }

    private BufferedImage build(int size) {
        int x = 0, y = 0;
        int ny = 0;
        for (Element element : elements) {
            if (element.width() > size) {
                return null;// the sprite is larger than the sprite sheet
            }
            if (x + element.width() > size) { //horizontal overflow -> carriage return
                y = ny;
                x = 0;
            }
            if (y + element.height() > size) { //vertical overflow -> fail
                return null;
            }
            element.setPosition(x, y);
            x += element.width();
            ny = Math.max(ny, y + element.height());
        }
        final BufferedImage result = new BufferedImage(size, ny, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        for (final Element image : elements) {
            
            graphics.drawImage(image.image, image.position.x, image.position.y, null);
        }
        return result;
    }

    static Rectangle usedArea(BufferedImage img) {
        final int width = img.getWidth();
        final int height = img.getHeight();
        int xmin = width, xmax = 0;
        int ymin = height, ymax = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((img.getRGB(x, y) & 0xff000000) != 0) {
                    xmin = x < xmin ? x : xmin;
                    ymin = y < ymin ? y : ymin;
                    xmax = x > xmax ? x : xmax;
                    ymax = y > ymax ? y : ymax;
                }
            }
        }
        return new Rectangle(xmin, ymin, xmax - xmin + 1, ymax - ymin + 1);
    }

    public static class Element {

        public Element( BufferedImage image) {            
            this.usedArea = usedArea(image);
            this.image = image.getSubimage(usedArea.x, usedArea.y, usedArea.width, usedArea.height);
            this.position = new Rectangle(usedArea);
        }
                
        private final BufferedImage image;
        private final Rectangle usedArea,position;

        public void setPosition(int x, int y) {
            position.x = x;
            position.y = y;
        }

        int height() {
            return image.getHeight();
        }

        private int width() {
            return image.getWidth();
        }

        public Rectangle getPosition() {
            return position;
        }

        public Rectangle getUsedArea() {
            return usedArea;
        }
        

        
    }

    private static enum HeightDescending implements Comparator<Element> {

        INSTANCE;

        @Override
        public int compare(Element o1, Element o2) {
            return o2.height() - o1.height();
        }
    }
}
