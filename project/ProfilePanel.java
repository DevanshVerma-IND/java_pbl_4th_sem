package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Displays the logged-in user's full profile: basic info + bio + certificates.
 * Calls the same DB queries as User.viewProfile(), viewExtraProfile(), viewCertificates().
 */
public class ProfilePanel extends JPanel {

    private static final Color BG       = new Color(13, 17, 23);
    private static final Color CARD     = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color ACCENT2  = new Color(63, 185, 80);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);

    private static final String URL      = "jdbc:mysql://localhost:3306/mydb_java";
    private static final String DB_USER  = "root";
    private static final String DB_PASS  = "root";

    public ProfilePanel(User user) {
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));

        JPanel scroll = new JPanel();
        scroll.setBackground(BG);
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));

        // ── Title ───────────────────────────────────────────────
        JLabel title = header("My Profile");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(title);
        scroll.add(Box.createVerticalStrut(20));

        // ── Basic Info Card ─────────────────────────────────────
        JPanel basicCard = card("Basic Info");
        String status = user.getStatus();
        if ("Student".equalsIgnoreCase(status)) {
            basicCard.add(row("ID",      user.getId()));
            basicCard.add(row("Roll No", user.getRollno()));
        }
        basicCard.add(row("Name",   User.name));
        basicCard.add(row("Email",  user.getEmail()));
        basicCard.add(row("Status", status));
        scroll.add(basicCard);
        scroll.add(Box.createVerticalStrut(16));

        // ── Bio Card ────────────────────────────────────────────
        JPanel bioCard = card("Links & Bio");
        loadBio(User.name, bioCard);
        scroll.add(bioCard);
        scroll.add(Box.createVerticalStrut(16));

        // ── Certificates Card ────────────────────────────────────
        JPanel certCard = card("Certificates");
        loadCertificates(User.name, certCard);
        scroll.add(certCard);

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp, BorderLayout.CENTER);
    }

    private void loadBio(String person, JPanel card) {
        String query = "select * from bio where person = ?";
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, person);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String li  = rs.getString("linkedIn");
                String gh  = rs.getString("github");
                String lc  = rs.getString("leetcode");
                String desc = rs.getString("description");
                if (!isNull(li))   card.add(row("LinkedIn",    li));
                if (!isNull(gh))   card.add(row("GitHub",      gh));
                if (!isNull(lc))   card.add(row("LeetCode",    lc));
                if (!isNull(desc)) card.add(row("Description", desc));
            } else {
                card.add(emptyNote("No bio added yet."));
            }
        } catch (Exception e) {
            card.add(emptyNote("Could not load bio."));
        }
    }

    private void loadCertificates(String person, JPanel card) {
        String query = "select * from certificates where person = ?";
        boolean any = false;
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, person);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                any = true;
                String t = rs.getString("title");
                String cr = rs.getString("credentials");
                JPanel item = new JPanel();
                item.setBackground(new Color(30, 37, 45));
                item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
                item.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_C, 1, true), new EmptyBorder(10, 14, 10, 14)));
                item.setAlignmentX(Component.LEFT_ALIGNMENT);
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                JLabel tl = new JLabel(t);
                tl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                tl.setForeground(ACCENT2);
                JLabel cl = new JLabel(cr);
                cl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                cl.setForeground(SUBTEXT);
                item.add(tl);
                item.add(Box.createVerticalStrut(4));
                item.add(cl);
                card.add(item);
                card.add(Box.createVerticalStrut(8));
            }
        } catch (Exception e) {
            card.add(emptyNote("Could not load certificates."));
        }
        if (!any) card.add(emptyNote("No certificates added yet."));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JLabel header(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setForeground(TEXT);
        return l;
    }

    private JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(16, 20, 16, 20)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(ACCENT);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(tl);
        p.add(Box.createVerticalStrut(12));
        return p;
    }

    private JPanel row(String key, String val) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        p.setBackground(CARD);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel k = new JLabel(key + ":  ");
        k.setFont(new Font("Segoe UI", Font.BOLD, 13));
        k.setForeground(SUBTEXT);
        JLabel v = new JLabel(val == null ? "—" : val);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        v.setForeground(TEXT);
        p.add(k);
        p.add(v);
        return p;
    }

    private JLabel emptyNote(String msg) {
        JLabel l = new JLabel(msg);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private boolean isNull(String s) {
        return s == null || s.equalsIgnoreCase("null") || s.isBlank();
    }
}
