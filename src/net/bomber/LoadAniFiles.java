/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.bomber.ani.*;
import net.bomber.ani.ANI_SEQ_STAT_FRAM.Step;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

/**
 *
 * @author pierre
 */
public class LoadAniFiles {

    static final Predicate<String> notComment = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return !input.startsWith("#");
        }
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        final URL listURL = Resources.getResource(LoadAniFiles.class, "res/list");
        final List<String> animationNames = new ArrayList<>(Arrays.asList(Resources.toString(listURL, Charsets.US_ASCII).trim().split("\n")));

        Collections.sort(animationNames);
        final List<CHFILE> chFiles = Lists.newArrayList();

        for (String animationName : Iterables.filter(animationNames, notComment)) {
            final URL animationURL = new URL(listURL, animationName);
            final CHFILE ch = new CHFILE(animationURL);
            chFiles.add(ch);
            ch.ani.show();
        }
        createSpriteSheet(chFiles);
    }

    private static void createSpriteSheet(final List<CHFILE> chFiles) throws RuntimeException, IOException {
        final Map<ANI_FRAM_CIMG, SpriteSheetBuilder.Element> elements = Maps.newHashMap();
        final SpriteSheetBuilder spriteSheetBuilder = new SpriteSheetBuilder();
        for (final CHFILE ch : chFiles) {
            for (ANI_FRAM fram : filter(ch.ani.objects, ANI_FRAM.class)) {
                for (ANI_FRAM_CIMG cimg : filter(fram.objects, ANI_FRAM_CIMG.class)) {
                    SpriteSheetBuilder.Element element = spriteSheetBuilder.addImage(cimg.createBuffuredImage());
                    elements.put(cimg, element);
                }
            }
        }
        final BufferedImage sheet = spriteSheetBuilder.build();
        ImageIO.write(sheet, "png", new File("e:\\sprites.png"));

        final JsonFactory jsonFactory = new JsonFactory();

        final JsonGenerator jg = jsonFactory.createJsonGenerator(new File("e:\\ani.json"), JsonEncoding.UTF8);
        jg.setPrettyPrinter(new DefaultPrettyPrinter());

        jg.writeStartObject();
        for (final CHFILE ch : chFiles) {
            final List<ANI_FRAM_CIMG> images = Lists.newArrayList();
            for (ANI_FRAM fram : filter(ch.ani.objects, ANI_FRAM.class)) {
                Iterables.addAll(images, filter(fram.objects, ANI_FRAM_CIMG.class));
            }


            for (final ANI_SEQ seq : ch.ani.sequences()) {
                jg.writeFieldName(seq.name());
                jg.writeStartObject();
                {
                    jg.writeFieldName("frames");
                    jg.writeStartArray();
                    {
                        for (final ANI_SEQ_STAT stat : seq.stats()) {
                            for (final Step step : stat.steps()) {
                                try {
                                    final ANI_FRAM_CIMG img = images.get(step.getImageIndex());

                                    final SpriteSheetBuilder.Element elem = elements.get(img);
                                    //I should iterate over steps rather than stat because there are rare exception where there a multiple steps (ex CORNER6).
                                    jg.writeStartObject();
                                    jg.writeNumberField("xo", elem.getUsedArea().x - img.getXpos());
                                    jg.writeNumberField("yo", elem.getUsedArea().y - img.getYpos());
                                    jg.writeNumberField("x", elem.getPosition().x);
                                    jg.writeNumberField("y", elem.getPosition().y);
                                    jg.writeNumberField("w", elem.getPosition().width);
                                    jg.writeNumberField("h", elem.getPosition().height);
                                    jg.writeEndObject();
                                } catch (IndexOutOfBoundsException ex) {
                                    System.err.println("Failed to read " + seq.name());
                                }
                            }
                        }
                    }
                    jg.writeEndArray();
                }
                jg.writeEndObject();
            }
        }
        jg.writeEndObject();
        jg.close();
    }
}
