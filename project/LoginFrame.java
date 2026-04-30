package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class LoginFrame extends JFrame {

    private static final Color BG        = new Color(13, 17, 23);
    private static final Color PANEL_BG  = new Color(22, 27, 34);
    private static final Color ACCENT    = new Color(88, 166, 255);
    private static final Color SUBTEXT   = new Color(139, 148, 158);
    private static final Color BORDER_C  = new Color(48, 54, 61);
    private static final Color FIELD_BG  = new Color(13, 17, 23);
    private static final Color TEXT      = new Color(230, 237, 243);

    private static final Font TITLE_F = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font LABEL_F = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FIELD_F = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BTN_F   = new Font("Segoe UI", Font.BOLD, 13);

    public LoginFrame() {
        setTitle("KnowVerse — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Header ─────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BG);

        JLabel logo = new JLabel("KnowVerse");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(ACCENT);

        JLabel sub = new JLabel("Academic Q&A Portal");
        sub.setFont(LABEL_F);
        sub.setForeground(SUBTEXT);

        JPanel hbox = new JPanel();
        hbox.setBackground(BG);
        hbox.setLayout(new BoxLayout(hbox, BoxLayout.Y_AXIS));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        hbox.add(logo);
        hbox.add(Box.createVerticalStrut(4));
        hbox.add(sub);

        header.add(hbox);
        root.add(header, BorderLayout.NORTH);

        // ── Card ───────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(30, 36, 30, 36)
        ));

        JLabel title = new JLabel("Sign in");
        title.setFont(TITLE_F);
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(24));

        JTextField nameField = styledField();
        JPasswordField passField = styledPasswordField();

        // 🔐 Show/Hide password checkbox
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(PANEL_BG);
        showPassword.setForeground(SUBTEXT);
        showPassword.setFont(LABEL_F);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });

        card.add(fieldBlock("Name", nameField));
        card.add(Box.createVerticalStrut(16));
        card.add(fieldBlock("Password", passField));
        card.add(Box.createVerticalStrut(6));
        card.add(showPassword);
        card.add(Box.createVerticalStrut(18));

        JButton loginBtn = accentButton("Sign In", ACCENT);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));

        JButton changeBtn = accentButton("Change Password", BORDER_C);
        changeBtn.setForeground(SUBTEXT);
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        card.add(changeBtn);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        center.add(card);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // ── Login Logic ───────────────────
        Runnable loginAction = () -> {
            String name = nameField.getText().trim().toUpperCase();
            String password = new String(passField.getPassword()).trim();

            if (name.isEmpty() || password.isEmpty()) {
                showError("Please enter both name and password.");
                return;
            }

            ArrayList<String> details = Login.getDetails(name, password);

            if (!details.isEmpty()) {
                User user = new User(
                        details.get(0),
                        details.get(1),
                        details.get(2),
                        details.get(3),
                        details.get(4)
                );
                dispose();
                new DashboardFrame(user);
            } else {
                showError("Account doesn't exist or credentials are wrong.");
            }
        };

        // Button click
        loginBtn.addActionListener(e -> loginAction.run());

        // ✅ ENTER key works
        nameField.addActionListener(e -> loginAction.run());
        passField.addActionListener(e -> loginAction.run());

        // ✅ Default button
        getRootPane().setDefaultButton(loginBtn);

        // Change password
        changeBtn.addActionListener(e -> new ChangePasswordDialog(this));
    }

    // ── Helpers ─────────────────────────

    private JPanel fieldBlock(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PANEL_BG);

        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_F);
        lbl.setForeground(SUBTEXT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(field);

        return p;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FIELD_F);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FIELD_F);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setEchoChar('•');
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_C, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    static JButton accentButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(BTN_F);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));

        b.addMouseListener(new MouseAdapter() {
            Color orig = bg;
            public void mouseEntered(MouseEvent e) { b.setBackground(orig.brighter()); }
            public void mouseExited(MouseEvent e)  { b.setBackground(orig); }
        });

        return b;
    }

    static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ✅ RESTORED METHOD (Fixes your errors)
    static void showInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}