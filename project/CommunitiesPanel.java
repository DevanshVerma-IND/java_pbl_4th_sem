package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Full community browser.
 * Mirrors Communication.viewCommunities() → gotoCommunity() →
 *   viewAllQuestion() / askQuestion() → goToQuestion() →
 *     viewAllAnswers() / answerQuestion() → upvote()
 */
public class CommunitiesPanel extends JPanel {

    // ── Theme ────────────────────────────────────────────────────────────────
    static final Color BG       = new Color(13, 17, 23);
    static final Color CARD     = new Color(22, 27, 34);
    static final Color ACCENT   = new Color(88, 166, 255);
    static final Color ACCENT2  = new Color(63, 185, 80);
    static final Color WARN     = new Color(210, 153, 34);
    static final Color TEXT     = new Color(230, 237, 243);
    static final Color SUBTEXT  = new Color(139, 148, 158);
    static final Color BORDER_C = new Color(48, 54, 61);
    static final Color FIELD_BG = new Color(13, 17, 23);

    static final String URL     = "jdbc:mysql://localhost:3306/mydb_java";
    static final String DB_USER = "root";
    static final String DB_PASS = "root";

    // navigation breadcrumb state
    private String currentCommunityId   = null;
    private String currentCommunityName = null;
    private String currentQuestionId    = null;

    // main card/content switcher
    private final JPanel mainArea;
    private final JLabel breadcrumb;

    public CommunitiesPanel() {
        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 32, 28, 32));

        // ── Top bar ──────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel title = new JLabel("Communities");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        top.add(title, BorderLayout.NORTH);

        breadcrumb = new JLabel("All Communities");
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumb.setForeground(SUBTEXT);
        top.add(breadcrumb, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(Box.createVerticalStrut(14), BorderLayout.EAST); // spacer trick

        mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG);
        add(mainArea, BorderLayout.CENTER);

        showCommunitiesList();
    }

    // ════════════════════════════════════════════════════════════════════════
    // 1. Communities list
    // ════════════════════════════════════════════════════════════════════════

    void showCommunitiesList() {
        currentCommunityId   = null;
        currentCommunityName = null;
        currentQuestionId    = null;
        breadcrumb.setText("All Communities");

        JPanel list = new JPanel();
        list.setBackground(BG);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(14, 0, 0, 0));

        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement("select * from community");
             ResultSet rs = ps.executeQuery()) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                String id      = rs.getString("id");
                String name    = rs.getString("name");
                String creator = rs.getString("creator");

                JPanel row = communityRow(id, name, creator);
                list.add(row);
                list.add(Box.createVerticalStrut(10));
            }
            if (!any) list.add(emptyNote("No communities yet. Create one!"));

        } catch (Exception e) { list.add(emptyNote("Could not load communities.")); }

        show(list);
    }

    private JPanel communityRow(String id, String name, String creator) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(14, 18, 14, 18)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(TEXT);
        JLabel crLbl  = new JLabel("Created by " + creator);
        crLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        crLbl.setForeground(SUBTEXT);

        JPanel left = new JPanel();
        left.setBackground(CARD);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(nameLbl);
        left.add(Box.createVerticalStrut(4));
        left.add(crLbl);
        p.add(left, BorderLayout.CENTER);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrow.setForeground(ACCENT);
        p.add(arrow, BorderLayout.EAST);

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color orig = CARD;
            public void mouseEntered(java.awt.event.MouseEvent e) {
                p.setBackground(new Color(33, 38, 45));
                left.setBackground(new Color(33, 38, 45));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                p.setBackground(orig); left.setBackground(orig);
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showCommunity(id, name);
            }
        });
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. Inside a community — questions list + ask
    // ════════════════════════════════════════════════════════════════════════

    void showCommunity(String communityId, String communityName) {
        currentCommunityId   = communityId;
        currentCommunityName = communityName;
        currentQuestionId    = null;
        breadcrumb.setText("All Communities  ›  " + communityName);

        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        // ── Back + Ask row ───────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(BG);
        topBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton backBtn = LoginFrame.accentButton("← Back", BORDER_C);
        backBtn.setForeground(SUBTEXT);
        JButton askBtn  = LoginFrame.accentButton("+ Ask Question", ACCENT2);
        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(askBtn,  BorderLayout.EAST);
        outer.add(topBar);
        outer.add(Box.createVerticalStrut(18));

        // ── Questions ────────────────────────────────────────────
        JPanel qList = new JPanel();
        qList.setBackground(BG);
        qList.setLayout(new BoxLayout(qList, BoxLayout.Y_AXIS));
        qList.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadQuestions(communityId, qList);
        outer.add(qList);

        show(outer);

        backBtn.addActionListener(e -> showCommunitiesList());
        askBtn.addActionListener(e  -> showAskQuestion(communityId));
    }

    private void loadQuestions(String communityId, JPanel list) {
        list.removeAll();
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(
                     "select * from questions where community_id = ?")) {
            ps.setString(1, communityId);
            ResultSet rs = ps.executeQuery();
            boolean any = false;
            while (rs.next()) {
                any = true;
                String id   = rs.getString("id");
                String title = rs.getString("title");
                String qtext = rs.getString("question_text");
                String person = rs.getString("person");
                list.add(questionRow(id, title, qtext, person));
                list.add(Box.createVerticalStrut(10));
            }
            if (!any) list.add(emptyNote("No questions yet. Be the first to ask!"));
        } catch (Exception e) { list.add(emptyNote("Could not load questions.")); }
        list.revalidate();
        list.repaint();
    }

    private JPanel questionRow(String id, String title, String qtext, String person) {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(14, 18, 14, 18)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel tl = new JLabel(id + ". " + title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tl.setForeground(ACCENT);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);

        String preview = qtext.length() > 80 ? qtext.substring(0, 80) + "…" : qtext;
        JLabel ql = new JLabel(preview);
        ql.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ql.setForeground(TEXT);
        ql.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pl = new JLabel("by " + person);
        pl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pl.setForeground(SUBTEXT);
        pl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(tl);
        p.add(Box.createVerticalStrut(4));
        p.add(ql);
        p.add(Box.createVerticalStrut(4));
        p.add(pl);

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { p.setBackground(new Color(33,38,45)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { p.setBackground(CARD); }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showQuestion(id, title, qtext, person);
            }
        });
        return p;
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. Ask Question
    // ════════════════════════════════════════════════════════════════════════

    void showAskQuestion(String communityId) {
        breadcrumb.setText("All Communities  ›  " + currentCommunityName + "  ›  Ask Question");

        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        JButton backBtn = LoginFrame.accentButton("← Back", BORDER_C);
        backBtn.setForeground(SUBTEXT);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(backBtn);
        outer.add(Box.createVerticalStrut(16));

        JLabel lbl = new JLabel("Ask a Question");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(lbl);
        outer.add(Box.createVerticalStrut(16));

        JTextField titleField = styledField();
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(fieldBlock("Title", titleField));
        outer.add(Box.createVerticalStrut(12));

        JTextArea bodyArea = new JTextArea(5, 40);
        bodyArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bodyArea.setBackground(FIELD_BG);
        bodyArea.setForeground(TEXT);
        bodyArea.setCaretColor(ACCENT);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane bsp = new JScrollPane(bodyArea);
        bsp.setBorder(new LineBorder(BORDER_C, 1, true));
        bsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        bsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel ql = new JLabel("Question");
        ql.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ql.setForeground(SUBTEXT);
        ql.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(ql);
        outer.add(Box.createVerticalStrut(4));
        outer.add(bsp);
        outer.add(Box.createVerticalStrut(16));

        JButton submitBtn = LoginFrame.accentButton("Submit Question", ACCENT);
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(submitBtn);

        show(outer);

        backBtn.addActionListener(e -> showCommunity(communityId, currentCommunityName));
        submitBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String b = bodyArea.getText().trim();
            if (t.isEmpty() || b.isEmpty()) { LoginFrame.showError("Both title and question are required."); return; }
            askQuestion(communityId, t, b);
            LoginFrame.showInfo("Question submitted successfully!");
            showCommunity(communityId, currentCommunityName);
        });
    }

    private void askQuestion(String communityId, String title, String body) {
        String q = "insert into questions (community_id, title, question_text, person) values (?,?,?,?)";
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, communityId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setString(4, User.name);
            ps.executeUpdate();
        } catch (Exception e) { LoginFrame.showError(e.getMessage()); }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 4. Question detail — answers + answer + upvote
    // ════════════════════════════════════════════════════════════════════════

    void showQuestion(String qId, String title, String qtext, String person) {
        currentQuestionId = qId;
        breadcrumb.setText("All Communities  ›  " + currentCommunityName + "  ›  " + title);

        JPanel outer = new JPanel();
        outer.setBackground(BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        JButton backBtn = LoginFrame.accentButton("← Back", BORDER_C);
        backBtn.setForeground(SUBTEXT);
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(backBtn);
        outer.add(Box.createVerticalStrut(14));

        // Question display
        JPanel qCard = new JPanel();
        qCard.setBackground(CARD);
        qCard.setLayout(new BoxLayout(qCard, BoxLayout.Y_AXIS));
        qCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(16, 18, 16, 18)));
        qCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        qCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel tl = new JLabel(qId + ". " + title);
        tl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tl.setForeground(TEXT);
        tl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel pl = new JLabel("by " + person);
        pl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pl.setForeground(SUBTEXT);
        pl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea qt = new JTextArea(qtext);
        qt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qt.setForeground(TEXT);
        qt.setBackground(CARD);
        qt.setEditable(false);
        qt.setWrapStyleWord(true);
        qt.setLineWrap(true);
        qt.setAlignmentX(Component.LEFT_ALIGNMENT);
        qCard.add(tl);
        qCard.add(Box.createVerticalStrut(4));
        qCard.add(pl);
        qCard.add(Box.createVerticalStrut(10));
        qCard.add(qt);
        outer.add(qCard);
        outer.add(Box.createVerticalStrut(18));

        // Answer input
        JLabel aLabel = new JLabel("Your Answer");
        aLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aLabel.setForeground(ACCENT);
        aLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(aLabel);
        outer.add(Box.createVerticalStrut(8));

        JTextArea ansArea = new JTextArea(4, 40);
        ansArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ansArea.setBackground(FIELD_BG);
        ansArea.setForeground(TEXT);
        ansArea.setCaretColor(ACCENT);
        ansArea.setLineWrap(true);
        ansArea.setWrapStyleWord(true);
        ansArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane asp = new JScrollPane(ansArea);
        asp.setBorder(new LineBorder(BORDER_C, 1, true));
        asp.setAlignmentX(Component.LEFT_ALIGNMENT);
        asp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        outer.add(asp);
        outer.add(Box.createVerticalStrut(10));

        JButton ansBtn = LoginFrame.accentButton("Post Answer", ACCENT2);
        ansBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(ansBtn);
        outer.add(Box.createVerticalStrut(24));

        // Answers list
        JLabel allLabel = new JLabel("All Answers (sorted by upvotes)");
        allLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        allLabel.setForeground(ACCENT);
        allLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(allLabel);
        outer.add(Box.createVerticalStrut(10));

        JPanel answerList = new JPanel();
        answerList.setBackground(BG);
        answerList.setLayout(new BoxLayout(answerList, BoxLayout.Y_AXIS));
        answerList.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadAnswers(qId, answerList);
        outer.add(answerList);

        show(outer);

        backBtn.addActionListener(e -> showCommunity(currentCommunityId, currentCommunityName));

        ansBtn.addActionListener(e -> {
            String ans = ansArea.getText().trim();
            if (ans.isEmpty()) { LoginFrame.showError("Answer cannot be empty."); return; }
            postAnswer(qId, ans);
            ansArea.setText("");
            loadAnswers(qId, answerList);
            answerList.revalidate();
            answerList.repaint();
            LoginFrame.showInfo("Answer posted!");
        });
    }

    private void loadAnswers(String questionId, JPanel list) {
        list.removeAll();
        String query = "select * from answers where question_id = ? order by count desc";
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, questionId);
            ResultSet rs = ps.executeQuery();
            boolean any = false;
            while (rs.next()) {
                any = true;
                String id   = rs.getString("id");
                String atxt = rs.getString("answer_text");
                String pers = rs.getString("person");
                String cnt  = rs.getString("count");
                list.add(answerRow(id, atxt, pers, cnt, questionId, list));
                list.add(Box.createVerticalStrut(10));
            }
            if (!any) list.add(emptyNote("No answers yet."));
        } catch (Exception e) { list.add(emptyNote("Could not load answers.")); }
        list.revalidate();
        list.repaint();
    }

    private JPanel answerRow(String id, String text, String person, String count,
                              String questionId, JPanel parentList) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(14, 18, 14, 18)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel left = new JPanel();
        left.setBackground(CARD);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel pLabel = new JLabel("Answered by " + person);
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pLabel.setForeground(ACCENT2);
        pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea ta = new JTextArea(text);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ta.setForeground(TEXT);
        ta.setBackground(CARD);
        ta.setEditable(false);
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(pLabel);
        left.add(Box.createVerticalStrut(6));
        left.add(ta);
        p.add(left, BorderLayout.CENTER);

        // Upvote panel
        JPanel right = new JPanel();
        right.setBackground(CARD);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel countLbl = new JLabel("▲ " + count);
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLbl.setForeground(WARN);
        countLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton upBtn = LoginFrame.accentButton("Upvote", WARN);
        upBtn.setForeground(Color.WHITE);
        upBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        upBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(countLbl);
        right.add(Box.createVerticalStrut(6));
        right.add(upBtn);
        p.add(right, BorderLayout.EAST);

        upBtn.addActionListener(e -> {
            Communication.upvote(questionId, id);
            // Reload answers to reflect new count
            loadAnswers(questionId, parentList);
        });

        return p;
    }

    private void postAnswer(String questionId, String text) {
        String q = "insert into answers (question_id, answer_text, person, count) values (?,?,?,?)";
        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, questionId);
            ps.setString(2, text);
            ps.setString(3, User.name);
            ps.setString(4, "0");
            ps.executeUpdate();
        } catch (Exception e) { LoginFrame.showError(e.getMessage()); }
    }

    // ── Navigation helper ────────────────────────────────────────────────────

    private void show(JPanel panel) {
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        mainArea.removeAll();
        mainArea.add(sp, BorderLayout.CENTER);
        mainArea.revalidate();
        mainArea.repaint();
    }

    // ── Shared widget helpers ────────────────────────────────────────────────

    static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(7, 10, 7, 10)));
        return f;
    }

    static JPanel fieldBlock(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    static JLabel emptyNote(String msg) {
        JLabel l = new JLabel(msg);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
