/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UnexpectedErrorReport.java
 *
 * Created on 1 janv. 2011, 17:57:33
 */
package space.app.common;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.jnlp.BasicService;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Pierre
 */
public class DbgError extends javax.swing.JDialog {

    public static UncaughtExceptionHandler handler() {
        return new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                report(e);
            }
        };
    }

    public static void assertNotNull(String string, Object bs) {
        if(bs==null)
            DbgError.report(new AssertionError(string));
    }
    private final List<Image> windowIcons;

    /**
     * This helper function invoke and wait the runnable except when the current thread is 
     * the dispatch thread. When if the current thrad is the dispatch thread, the runnable
     * is runned in the current thread.
     * This funciton returns after the runnable terminates
     * @param run a not null runnable
     */
    private static void invokeIfNeeded(final Runnable run) throws IllegalArgumentException {
        if (run == null) {
            throw new IllegalArgumentException();
        }
        if (java.awt.EventQueue.isDispatchThread()) {
            run.run();
        } else {
            try {
                java.awt.EventQueue.invokeAndWait(run);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void unexpected() {
        report(new RuntimeException("Unexpected code path"));
    }

  

    /**
     * This method can be called from any thread
     * Show a dialog box to the user and wait for it to close
     * @param message maybe null throwable message to display
     */
    public static void report(final Throwable message) {
        Logger.getLogger(DbgError.class.getName()).log(Level.SEVERE, "Error reported",
                message);
        invokeIfNeeded(new Runnable() {
            public void run() {
                new DbgError(message).setVisible(true);
            }
        });
    }

    /**
     * Build a list of images from the given list of relative URL
     * @param resourceNames resources relative name
     * @return a unmodifiable list of image
     */
    private  List<Image> loadList(final String... resourceNames) {
        if (resourceNames == null) {
            throw new IllegalArgumentException();
        }
        final List<Image> res = new ArrayList<Image>();
        for (final String resourceName : resourceNames) {
            try {
                res.add(ImageIO.read(getClass().getResourceAsStream(resourceName)));
            } catch (IOException ex) {
                // Failed to load the image. nothing to do, there will be one missing element in the list
                Logger.getLogger(DbgError.class.getName()).log(Level.SEVERE,
                        resourceName, ex);
            }
        }
        return Collections.unmodifiableList(res);
    }

    /**
     * General setup of the dialog box called by every constructor
     */
    private DbgError(final Throwable event) {
        super((Frame) null, true);

   

        // Read the dialog icons
        windowIcons = loadList(
                "dialog-error-16.png",
                "dialog-error-22.png",
                "dialog-error-32.png");
        initComponents();
        /**
         * When escape is pressed, the windows is closed by the standard mechanism
         */
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ESCAPE, 0), "Leave");
        getRootPane().getActionMap().put("Leave", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                dispatchEvent(new WindowEvent(DbgError.this,
                        WindowEvent.WINDOW_CLOSING));
            }
        });

        /**
         * Display the exception content
         */
        // Set the button pressed on ctrl+enter
        getRootPane().setDefaultButton(jResumeButton);
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        if (event != null) {
            event.printStackTrace(pw);
        }
        pw.close();//It also flush the stream
        // Note that StringWriter does not need to be closed
        jErrorTextArea.setText(sw.toString());
        jErrorTextArea.setCaretPosition(0);

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jKillButton = new javax.swing.JButton();
        jResumeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jErrorTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Oups ... an unexpected error occured");
        setIconImages(windowIcons);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jSeparator1, java.awt.BorderLayout.NORTH);

        jKillButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/space/debug/process-stop.png"))); // NOI18N
        jKillButton.setText("Kill Process");
        jKillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jKillButtonActionPerformed(evt);
            }
        });
        jPanel2.add(jKillButton);

        jResumeButton.setText("Resume");
        jResumeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResumeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(jResumeButton);

        jPanel1.add(jPanel2, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 300));

        jErrorTextArea.setColumns(20);
        jErrorTextArea.setEditable(false);
        jErrorTextArea.setLineWrap(true);
        jErrorTextArea.setRows(5);
        jScrollPane1.setViewportView(jErrorTextArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jKillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jKillButtonActionPerformed
        /**
         * This is the end of program execution
         */
        System.exit(1);
    }//GEN-LAST:event_jKillButtonActionPerformed

    private void jResumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResumeButtonActionPerformed
        //Perform the same action as when the user closes the windows
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_jResumeButtonActionPerformed

    /**
     * For test purpose
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                DbgError dialog = new DbgError(
                        new RuntimeException("outch"));
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea jErrorTextArea;
    private javax.swing.JButton jKillButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jResumeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
