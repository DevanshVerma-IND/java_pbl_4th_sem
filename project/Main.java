package project;

import javax.swing.SwingUtilities;

/**
 * Entry point. Launches the Swing GUI.
 * The original console-based logic is preserved in the backend classes;
 * this simply boots the GUI on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Use cross-platform L&F so colours render consistently on all OS
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
