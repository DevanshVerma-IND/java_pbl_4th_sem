package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UpdateProfilePanel extends JPanel {

    private static final Color BG       = new Color(13, 17, 23);
    private static final Color CARD     = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color ACCENT2  = new Color(63, 185, 80);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);
    private static final Color FIELD_BG = new Color(13, 17, 23);
    private static final Font  LABEL_F  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FIELD_F  = new Font("Segoe UI", Font.PLAIN, 13);

    private final User user;

    public UpdateProfilePanel(User user) {
        this.user = user;
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Update Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(24));

        // ── Bio Section ─────────────────────────────────────────
        content.add(sectionLabel("Update Bio Links"));
        content.add(Box.createVerticalStrut(12));

        JTextField liField = field(); liField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField ghField = field(); ghField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField lcField = field(); lcField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bioCard = new JPanel();
        bioCard.setBackground(CARD);
        bioCard.setLayout(new BoxLayout(bioCard, BoxLayout.Y_AXIS));
        bioCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(16, 18, 16, 18)));
        bioCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ❗ FIX: increase height so button is visible
        bioCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        bioCard.add(fieldBlock("LinkedIn (enter 'null' to clear)", liField));
        bioCard.add(Box.createVerticalStrut(10));
        bioCard.add(fieldBlock("GitHub (enter 'null' to clear)", ghField));
        bioCard.add(Box.createVerticalStrut(10));
        bioCard.add(fieldBlock("LeetCode (enter 'null' to clear)", lcField));
        bioCard.add(Box.createVerticalStrut(20));

        JButton bioBtn = LoginFrame.accentButton("Save Bio", ACCENT);
        bioBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioCard.add(bioBtn);

        content.add(bioCard);
        content.add(Box.createVerticalStrut(20));

        // ── Description Section ──────────────────────────────────
        content.add(sectionLabel("Update Description"));
        content.add(Box.createVerticalStrut(12));

        JTextArea descArea = new JTextArea(4, 40);
        descArea.setFont(FIELD_F);
        descArea.setBackground(FIELD_BG);
        descArea.setForeground(TEXT);
        descArea.setCaretColor(ACCENT);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane descSp = new JScrollPane(descArea);
        descSp.setBorder(new LineBorder(BORDER_C, 1, true));
        descSp.setAlignmentX(Component.LEFT_ALIGNMENT);
        descSp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton descBtn = LoginFrame.accentButton("Save Description", ACCENT);
        descBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel descCard = new JPanel();
        descCard.setBackground(CARD);
        descCard.setLayout(new BoxLayout(descCard, BoxLayout.Y_AXIS));
        descCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(16, 18, 16, 18)));
        descCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        descCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        descCard.add(descSp);
        descCard.add(Box.createVerticalStrut(12));
        descCard.add(descBtn);

        content.add(descCard);
        content.add(Box.createVerticalStrut(20));

        // ── Certificates Section ─────────────────────────────────
        content.add(sectionLabel("Add Certificate"));
        content.add(Box.createVerticalStrut(12));

        JTextField certTitle = field(); certTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField certCreds = field(); certCreds.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel certCard = new JPanel();
        certCard.setBackground(CARD);
        certCard.setLayout(new BoxLayout(certCard, BoxLayout.Y_AXIS));
        certCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(16, 18, 16, 18)));
        certCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        certCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        certCard.add(fieldBlock("Certificate Title", certTitle));
        certCard.add(Box.createVerticalStrut(10));
        certCard.add(fieldBlock("Credentials / Credential ID", certCreds));
        certCard.add(Box.createVerticalStrut(14));

        JButton certBtn = LoginFrame.accentButton("Add Certificate", ACCENT2);
        certBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        certCard.add(certBtn);

        content.add(certCard);

        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(sp, BorderLayout.CENTER);

        // ── Actions (UNCHANGED) ──────────────────────────────────
        bioBtn.addActionListener(e -> {
            String li = liField.getText().trim();
            String gh = ghField.getText().trim();
            String lc = lcField.getText().trim();
            if (li.isEmpty() || gh.isEmpty() || lc.isEmpty()) {
                LoginFrame.showError("Enter a value or 'null' for each link.");
                return;
            }
            updateBio(li, gh, lc);
        });

        descBtn.addActionListener(e -> {
            String desc = descArea.getText().trim();
            if (desc.isEmpty()) {
                LoginFrame.showError("Description cannot be empty.");
                return;
            }
            updateDescription(desc);
        });

        certBtn.addActionListener(e -> {
            String t  = certTitle.getText().trim();
            String cr = certCreds.getText().trim();
            if (t.isEmpty() || cr.isEmpty()) {
                LoginFrame.showError("Both fields are required.");
                return;
            }
            addCertificate(t, cr);
        });
    }

    // ── Backend methods (UNCHANGED) ─────────────────────────────

    private void updateBio(String li, String gh, String lc) {
        String query = "update bio set linkedIn = ?, github = ?, leetcode = ? where person = ?";
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb_java", "root", "root");
             java.sql.PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, li);
            ps.setString(2, gh);
            ps.setString(3, lc);
            ps.setString(4, User.name);
            int rows = ps.executeUpdate();
            if (rows > 0) LoginFrame.showInfo("Bio updated successfully!");
            else LoginFrame.showError("Something went wrong. Make sure your bio record exists.");
        } catch (Exception e) {
            LoginFrame.showError(e.getMessage());
        }
    }

    private void updateDescription(String desc) {
        String query = "update bio set description = ? where person = ?";
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb_java", "root", "root");
             java.sql.PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, desc);
            ps.setString(2, User.name);
            int rows = ps.executeUpdate();
            if (rows > 0) LoginFrame.showInfo("Description updated successfully!");
            else LoginFrame.showError("Something went wrong.");
        } catch (Exception e) {
            LoginFrame.showError(e.getMessage());
        }
    }

    private void addCertificate(String title, String creds) {
        String query = "insert into certificates (person, title, credentials) values (?, ?, ?)";
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb_java", "root", "root");
             java.sql.PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, User.name);
            ps.setString(2, title);
            ps.setString(3, creds);
            int rows = ps.executeUpdate();
            if (rows > 0) LoginFrame.showInfo("Certificate added successfully!");
            else LoginFrame.showError("Something went wrong.");
        } catch (Exception e) {
            LoginFrame.showError(e.getMessage());
        }
    }

    // ── Helpers ─────────────────────────────────────────────────

    private JPanel fieldBlock(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
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
        f.setFont(FIELD_F);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(ACCENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}