package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    private static final Color BG       = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);
    private static final Color FIELD_BG = new Color(13, 17, 23);
    private static final Font  LABEL_F  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FIELD_F  = new Font("Segoe UI", Font.PLAIN, 14);

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);
        setSize(400, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel();
        root.setBackground(BG);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(20));

        JTextField nameF   = field(); nameF.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField passF = pfield(); passF.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField newF  = pfield(); newF.setAlignmentX(Component.LEFT_ALIGNMENT);

        root.add(block("Your Name", nameF));
        root.add(Box.createVerticalStrut(12));
        root.add(block("Current Password", passF));
        root.add(Box.createVerticalStrut(12));
        root.add(block("New Password", newF));
        root.add(Box.createVerticalStrut(22));

        JButton btn = LoginFrame.accentButton("Update Password", ACCENT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        root.add(btn);

        setContentPane(root);

        btn.addActionListener(e -> {
            String name  = nameF.getText().trim().toUpperCase();
            String pass  = new String(passF.getPassword()).trim();
            String newPw = new String(newF.getPassword()).trim();
            if (name.isEmpty() || pass.isEmpty() || newPw.isEmpty()) {
                LoginFrame.showError("All fields are required.");
                return;
            }
            Login.changePassword(name, pass, newPw);
            LoginFrame.showInfo("Password change request sent.");
            dispose();
        });
    }

    private JPanel block(String lbl, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(lbl);
        l.setFont(LABEL_F);
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(FIELD_F); f.setBackground(FIELD_BG);
        f.setForeground(TEXT); f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPasswordField pfield() {
        JPasswordField f = new JPasswordField();
        f.setFont(FIELD_F); f.setBackground(FIELD_BG);
        f.setForeground(TEXT); f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }
}
