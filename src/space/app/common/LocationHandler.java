package space.app.common;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 * This implementation store windows position, size and state ( maximised or
 * not) to user preferences, thanks to java.util.prefs, in a node that depends
 * on the given JFrame class.
 *
 * To use this class, call subscribe once and pass the root JFrame as argument.
 *
 * When {@link subscribe} is called, the window state is restored from the
 * preferences. Windows located out of the screen are moved so that at least 50
 * pixels are visible for each dimensions.
 *
 * A windows wider or taller than the screen is truncated.
 *
 *
 * The following preferences keys are used: <ul> <li><b>x</b>: the topleft
 * position of the windows on x axis. Previous position for maximized
 * windows.</li> <li><b>y</b>: the topleft position of the windows on y axis.
 * Previous position for maximized windows.</li> <li><b>width</b>: the
 * horizontal size of the windows. Previous size for maximized windows.</li>
 * <li><b>height</b>: the vertical size of the windows. Previous size for
 * maximized window.</li> <li><b>state</b>: the extended state {
 *
 * @see JFrame.getExtendedState}.</li> </ul>
 *
 * The preferences are flushed on frame "closing" event.
 * @author Pierre LABATUT
 */
public class LocationHandler {

    // not null reference to a preference note where windows locaton is read / write
    final private Preferences node;
    // not null reference to a frame on which the location is applied
    final private JFrame frame;
    private int x, y, width, height, state;

    public LocationHandler(final JFrame frame) {
        if (frame == null) {
            throw new NullPointerException();
        }
        this.frame = frame;
        node = Preferences.userNodeForPackage(frame.getClass());

        frame.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                capture();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                capture();
            }
        });


        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                save();
                try {
                    node.flush();
                } catch (BackingStoreException ex) {
                    throw new RuntimeException(ex);// Well I can't do anything else than crash
                }
            }
        });
    }
    /**
     * amount in pixel of the windows that must be visible on the screen. This
     * parameter is used in an algogrithm that bring back invisible windows to
     * the visible space.
     */
    private static final int MARGIN = 50;

    /**
     * Read data from a preference node and restores windows location
     */
    public void load() {
        if (node == null) {
            throw new NullPointerException();
        }
        if (frame == null) {
            throw new NullPointerException();
        }
        /**
         * Restore the windows position and dimension. This has to be performed
         * before setExtendedState() because it remove the maximised status of a
         * window.
         */
        final Rectangle r = frame.getBounds();
        r.x = x = node.getInt("x", r.x);
        r.y = y = node.getInt("y", r.y);
        r.width = width = node.getInt("width", r.width);
        r.height = height = node.getInt("height", r.height);
        /**
         * Check that the windows is not wider than the screen
         */
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (width > screenSize.width) {
            width = r.width = screenSize.width;
        }
        if (height > screenSize.height) {
            height = r.height = screenSize.height;
        }
        /**
         * Check that the windows in not out of the screen @TODO test this
         * implementation with a multiscreen setup
         */
        if (r.getMaxX() < MARGIN) {
            x = r.x = MARGIN - r.width;
        }
        if (r.getMaxY() < MARGIN) {
            y = r.y = MARGIN - r.height;
        }
        if (r.x > screenSize.width - MARGIN) {
            x = r.x = screenSize.width - MARGIN;
        }
        if (r.y > screenSize.height - MARGIN) {
            y = r.y = screenSize.height - MARGIN;
        }


        frame.setBounds(r);
        // Maximize if needed
        frame.setExtendedState(node.getInt("state", 0) & ~JFrame.ICONIFIED);
    }

    private void capture() {
        if (node == null) {
            throw new NullPointerException();
        }
        if (frame == null) {
            throw new NullPointerException();
        }

        state = frame.getExtendedState();
        final Rectangle r = frame.getBounds();


        /**
         * Magic Number -4 ? This is the window position of the state change
         * event, when a windows is maximized. Why ? Who knows ... anyway this
         * is no relyable
         */
        // Don't record the size and position for maximized windows
        if ((state & JFrame.MAXIMIZED_HORIZ) == 0 && r.x != -4) {
            x = r.x;
            width = r.width;
        }
        // Don't record the size and position for maximized windows
        if ((state & JFrame.MAXIMIZED_VERT) == 0 && r.y != -4) {
            y = r.y;
            height = r.height;
        }
    }

    /**
     * Store frame's localization in a preference node. The preference storage
     * is not flushed.
     */
    public void save() {
        if (node == null) {
            throw new NullPointerException();
        }
        if (frame == null) {
            throw new NullPointerException();
        }

        node.putInt("state", state);
        node.putInt("x", x);
        node.putInt("y", y);
        node.putInt("width", width);
        node.putInt("height", height);

    }
}
