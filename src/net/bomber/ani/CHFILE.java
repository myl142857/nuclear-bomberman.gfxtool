/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.bomber.ani;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author pierre
 */
public class CHFILE {

    final static String FILEID = "CHFILE";
    public final ANI ani;

    public CHFILE(URL resource) throws IOException {

        try (final BinarySource in = new BinarySource(resource)) {
            final String type = in.readASCII(6);
            if (type.equals(FILEID) == false) {
                throw in.failure("Invalid file identifier");
            }
            ani = new ANI(in);
        }
    }

    @Override
    public String toString() {
        return "CHFILE{" + "ani=" + ani + '}';
    }
    
}
