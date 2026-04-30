package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardFrame extends JFrame {

    private static final Color BG       = new Color(13, 17, 23);
    private static final Color SIDEBAR  = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color ACCENT2  = new Color(63, 185, 80);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);
    private static final Color HOVER    = new Color(33, 38, 45);

    private final User user;
    private JPanel contentArea;

    public DashboardFrame(User user) {
        this.user = user;
        setTitle("KnowVerse — Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 480));
        buildUI();
        showWelcome();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Sidebar ─────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_C));

        JLabel brand = new JLabel("KnowVerse");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(ACCENT);
        brand.setBorder(new EmptyBorder(24, 20, 8, 20));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);

        JLabel welcome = new JLabel("Hello, " + shorten(User.name));
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcome.setForeground(SUBTEXT);
        welcome.setBorder(new EmptyBorder(0, 20, 20, 20));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(welcome);

        sidebar.add(divider());

        sidebar.add(navButton("👤  View Profile", () -> {
            ProfilePanel pp = new ProfilePanel(user);
            showPanel(pp);
        }));
        sidebar.add(navButton("✏️  Update Profile", () -> showPanel(new UpdateProfilePanel(user))));
        sidebar.add(navButton("🔍  Search Profile", () -> showPanel(new SearchProfilePanel())));
        sidebar.add(navButton("🏘️  Create Community", () -> showPanel(new CreateCommunityPanel())));
        sidebar.add(navButton("📋  Communities", () -> showPanel(new CommunitiesPanel())));

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(divider());

        JButton logout = navButton("⬅️  Logout", null);
        logout.setForeground(new Color(248, 81, 73));
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(16));

        logout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        // ── Content ──────────────────────────────────────────────
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG);

        root.add(sidebar, BorderLayout.WEST);
        root.add(contentArea, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void showWelcome() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);
        JLabel lbl = new JLabel("Welcome back, " + User.name + "!");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(new Color(230, 237, 243));
        JLabel sub = new JLabel("Use the sidebar to navigate.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(SUBTEXT);
        JPanel box = new JPanel();
        box.setBackground(BG);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(lbl);
        box.add(Box.createVerticalStrut(8));
        box.add(sub);
        p.add(box);
        showPanel(p);
    }

    public void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JButton navButton(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(new Color(201, 209, 217));
        b.setBackground(SIDEBAR);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(HOVER); }
            public void mouseExited(MouseEvent e)  { b.setBackground(SIDEBAR); }
        });
        if (action != null) b.addActionListener(e -> action.run());
        return b;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        sep.setBackground(BORDER_C);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private String shorten(String s) {
        if (s == null) return "";
        String[] parts = s.split(" ");
        return parts[0];
    }
}
