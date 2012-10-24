package net.bomber;

import java.io.IOException;

/**
 *
 * @author pierre
 */
public class Indenter implements Appendable {

    final Appendable out;
    int indent = 0;

    public Indenter(Appendable out) {
        this.out = out;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        append(csq, 0, csq.length());
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        for (int i = start; i < end; i++) {
            append(csq.charAt(i));
        }
        return this;
    }

    @Override
    public Appendable append(char c) throws IOException {

        if (c == '{' || c == '[') {
            indent++;
            out.append(c);
            endl();
        } else if (c == '}' || c == ']') {
            endl();
            out.append(c);
            indent--;
        } else {
            out.append(c);
        }
        return this;
    }

    private void endl() throws IOException {
        out.append('\n');
        for (int s = 0; s < indent; s++) {
            out.append(' ');
        }
    }
}
