package project;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Panel for creating a new community.
 * Wraps Communication.checkCommunity() and Communication.createCommunity() logic with GUI dialogs.
 */
public class CreateCommunityPanel extends JPanel {

    private static final Color BG       = new Color(13, 17, 23);
    private static final Color CARD     = new Color(22, 27, 34);
    private static final Color ACCENT   = new Color(88, 166, 255);
    private static final Color TEXT     = new Color(230, 237, 243);
    private static final Color SUBTEXT  = new Color(139, 148, 158);
    private static final Color BORDER_C = new Color(48, 54, 61);
    private static final Color FIELD_BG = new Color(13, 17, 23);

    public CreateCommunityPanel() {
        setBackground(BG);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(28, 32, 28, 32)));
        card.setPreferredSize(new Dimension(440, 240));

        JLabel title = new JLabel("Create Community");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        JLabel sub = new JLabel("Start a topic-based group for discussion.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(22));

        JLabel label = new JLabel("Community Name");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(SUBTEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(6));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBackground(FIELD_BG);
        nameField.setForeground(TEXT);
        nameField.setCaretColor(ACCENT);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true), new EmptyBorder(8, 12, 8, 12)));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(nameField);
        card.add(Box.createVerticalStrut(20));

        JButton createBtn = LoginFrame.accentButton("Create Community", ACCENT);
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(createBtn);

        add(card);

        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                LoginFrame.showError("Enter a proper community name.");
                return;
            }

            // Check for duplicates (mirrors Communication.checkCommunity)
            ArrayList<String> duplicates = Communication.checkCommunity(name);
            if (!duplicates.isEmpty()) {
                StringBuilder sb = new StringBuilder("Communities with similar name exist:\n");
                for (String d : duplicates) sb.append("• ").append(d).append("\n");
                sb.append("\nDo you still want to create this community?");
                int choice = JOptionPane.showConfirmDialog(
                    this, sb.toString(), "Similar Communities Found",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice != JOptionPane.YES_OPTION) return;
            }

            // Create community (mirrors Communication.createCommunity)
            createCommunity(name);
        });
    }

    private void createCommunity(String name) {
        String query = "insert into community(name, creator) values (?, ?)";
        try (java.sql.Connection c = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb_java", "root", "root");
             java.sql.PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, User.name);
            int rows = ps.executeUpdate();
            if (rows > 0) LoginFrame.showInfo("Community \"" + name + "\" created successfully!");
            else LoginFrame.showError("Something went wrong.");
        } catch (Exception ex) {
            LoginFrame.showError(ex.getMessage());
        }
    }
}
