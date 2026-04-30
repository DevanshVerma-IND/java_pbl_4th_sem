package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

/**
 * Lets the user search any registered person by name and view their profile.
 * Mirrors User.viewPersonProfile(), viewExtraProfile(), viewCertificates().
 */
public class SearchProfilePanel extends JPanel {

    private static final Color BG       = new Color(13, 17, 23);
    private static final Color CARD     = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color ACCENT2  = new Color(63, 185, 80);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);
    private static final Color FIELD_BG = new Color(13, 17, 23);

    private static final String URL     = "jdbc:mysql://localhost:3306/mydb_java";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    private JPanel resultArea;

    public SearchProfilePanel() {
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));
        buildUI();
    }

    private void buildUI() {
        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Search Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(title);
        outer.add(Box.createVerticalStrut(20));

        // Search bar row
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBackground(FIELD_BG);
        nameField.setForeground(TEXT);
        nameField.setCaretColor(ACCENT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(8, 12, 8, 12)));

        JButton searchBtn = LoginFrame.accentButton("Search", ACCENT);

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(BG);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        searchRow.add(nameField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);
        outer.add(searchRow);
        outer.add(Box.createVerticalStrut(24));

        // Result area
        resultArea = new JPanel();
        resultArea.setBackground(BG);
        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));
        resultArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(resultArea);

        JScrollPane sp = new JScrollPane(outer);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(sp, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String name = nameField.getText().trim().toUpperCase();
            if (name.isEmpty()) { LoginFrame.showError("Enter a name to search."); return; }
            searchProfile(name);
        });

        nameField.addActionListener(e -> searchBtn.doClick());
    }

    private void searchProfile(String name) {
        resultArea.removeAll();

        String query = "select * from users where name = ?";
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String rollno = rs.getString("rollno");
                String id     = rs.getString("id");
                String nm     = rs.getString("name");
                String email  = rs.getString("email");
                String status = rs.getString("status");

                JPanel basic = card("Basic Info");
                basic.add(row("Name",   nm));
                if ("Student".equalsIgnoreCase(status)) {
                    basic.add(row("Roll No", rollno));
                    basic.add(row("ID",      id));
                }
                basic.add(row("Email",  email));
                basic.add(row("Status", status));
                resultArea.add(basic);
                resultArea.add(Box.createVerticalStrut(14));

                JPanel bioCard = card("Links & Bio");
                loadBio(nm, bioCard);
                resultArea.add(bioCard);
                resultArea.add(Box.createVerticalStrut(14));

                JPanel certCard = card("Certificates");
                loadCertificates(nm, certCard);
                resultArea.add(certCard);
            } else {
                JLabel notFound = new JLabel("Profile not found.");
                notFound.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                notFound.setForeground(SUBTEXT);
                notFound.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultArea.add(notFound);
            }
        } catch (Exception e) {
            LoginFrame.showError(e.getMessage());
        }

        resultArea.revalidate();
        resultArea.repaint();
    }

    private void loadBio(String person, JPanel card) {
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement("select * from bio where person = ?")) {
            ps.setString(1, person);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String li   = rs.getString("linkedIn");
                String gh   = rs.getString("github");
                String lc   = rs.getString("leetcode");
                String desc = rs.getString("description");
                if (!isNull(li))   card.add(row("LinkedIn",    li));
                if (!isNull(gh))   card.add(row("GitHub",      gh));
                if (!isNull(lc))   card.add(row("LeetCode",    lc));
                if (!isNull(desc)) card.add(row("Description", desc));
            } else {
                card.add(emptyNote("No bio."));
            }
        } catch (Exception e) { card.add(emptyNote("Could not load.")); }
    }

    private void loadCertificates(String person, JPanel card) {
        boolean any = false;
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement("select * from certificates where person = ?")) {
            ps.setString(1, person);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                any = true;
                String t  = rs.getString("title");
                String cr = rs.getString("credentials");
                JPanel item = certItem(t, cr);
                card.add(item);
                card.add(Box.createVerticalStrut(8));
            }
        } catch (Exception e) { card.add(emptyNote("Could not load.")); }
        if (!any) card.add(emptyNote("No certificates."));
    }

    private JPanel certItem(String title, String creds) {
        JPanel item = new JPanel();
        item.setBackground(new Color(30, 37, 45));
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(10, 14, 10, 14)));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tl.setForeground(ACCENT2);
        JLabel cl = new JLabel(creds);
        cl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cl.setForeground(SUBTEXT);
        item.add(tl);
        item.add(Box.createVerticalStrut(4));
        item.add(cl);
        return item;
    }

    // ── Shared helpers ───────────────────────────────────────────────────────

    private JPanel card(String title) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(14, 18, 14, 18)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel tl = new JLabel(title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tl.setForeground(ACCENT);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(tl);
        p.add(Box.createVerticalStrut(10));
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
