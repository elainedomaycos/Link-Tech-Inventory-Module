import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lblStatus;
    private JButton btnShowPassword;
    private JButton btnLogin;
    private JCheckBox cbRememberMe;

    private int failedAttempts = 0;
    private long lockoutUntilMillis = 0L;
    private Timer lockoutTimer;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 60_000L;
    private final AuthRepository authRepository = new AuthRepository();

    public LoginFrame() {
        setTitle("LinkTech ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 520);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppColors.PAGE_BG);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(12);
        card.setBackground(AppColors.CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setPreferredSize(new Dimension(760, 430));

        JPanel leftPanel = buildBrandPanel();
        JPanel rightPanel = buildFormPanel();

        card.add(leftPanel, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);

        root.add(card);
        setContentPane(root);

        loadRememberedUser();
    }

    private JPanel buildBrandPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(290, 430));
        leftPanel.setBackground(AppColors.TABLE_HEADER_BG);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(30, 26, 30, 26));

        JLabel appName = new JLabel("LinkTech");
        appName.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        appName.setForeground(AppColors.TEXT_PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("<html><div style='width:230px;'>Manage your entire business from one place.</div></html>");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle2 = new JLabel("<html><div style='width:230px;'>Procurement, inventory, and supplier management - unified.</div></html>");
        subtitle2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle2.setForeground(AppColors.TEXT_SECONDARY);
        subtitle2.setAlignmentX(Component.LEFT_ALIGNMENT);

        UIComponents.RoundedPanel badge = new UIComponents.RoundedPanel(10);
        badge.setBackground(new Color(230, 241, 251));
        badge.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel badgeLabel = new JLabel("Secure Access");
        badgeLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        badgeLabel.setForeground(AppColors.PILL_MONITOR_FG);
        badge.add(badgeLabel);

        JPanel bullet1 = makeChecklistItem("Real-time inventory tracking");
        JPanel bullet2 = makeChecklistItem("Supplier management");
        JPanel bullet3 = makeChecklistItem("Purchase order workflows");
        JPanel bullet4 = makeChecklistItem("Low-stock alerts");

        leftPanel.add(appName);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(subtitle);
        leftPanel.add(Box.createVerticalStrut(6));
        leftPanel.add(subtitle2);
        leftPanel.add(Box.createVerticalStrut(16));
        leftPanel.add(badge);
        leftPanel.add(Box.createVerticalStrut(18));
        leftPanel.add(bullet1);
        leftPanel.add(Box.createVerticalStrut(9));
        leftPanel.add(bullet2);
        leftPanel.add(Box.createVerticalStrut(9));
        leftPanel.add(bullet3);
        leftPanel.add(Box.createVerticalStrut(9));
        leftPanel.add(bullet4);
        leftPanel.add(Box.createVerticalGlue());

        JLabel footerNote = new JLabel("© LinkTech Company");
        footerNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerNote.setForeground(AppColors.TEXT_MUTED);
        footerNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(footerNote);

        return leftPanel;
    }

    private JPanel makeChecklistItem(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComponent checkIcon = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(16, 16);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(34, 197, 94));
                g2.fillOval(0, 0, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(4, 8, 7, 11);
                g2.drawLine(7, 11, 12, 5);
                g2.dispose();
            }
        };

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(new Color(75, 85, 99));

        row.add(checkIcon);
        row.add(lbl);
        return row;
    }

    private JPanel buildFormPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(true);
        rightPanel.setBackground(AppColors.CARD_BG);
        rightPanel.setLayout(new GridBagLayout());

        UIComponents.RoundedPanel formWrap = new UIComponents.RoundedPanel(10);
        formWrap.setBackground(AppColors.CARD_BG);
        formWrap.setBorder(new EmptyBorder(30, 30, 24, 30));
        formWrap.setLayout(new BoxLayout(formWrap, BoxLayout.Y_AXIS));
        formWrap.setPreferredSize(new Dimension(410, 360));

        JLabel formTitle = new JLabel("Sign in");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 30));
        formTitle.setForeground(AppColors.TEXT_PRIMARY);

        JLabel formSub = new JLabel("Use your account credentials");
        formSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        formSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        formSub.setForeground(AppColors.TEXT_SECONDARY);

        JLabel lblUser = new JLabel("Username");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(AppColors.TEXT_SECONDARY);

        tfUsername = UIComponents.createTextField("e.g. admin");
        tfUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel lblPass = new JLabel("Password");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(AppColors.TEXT_SECONDARY);

        JPanel passWrap = new JPanel(new BorderLayout(8, 0));
        passWrap.setOpaque(false);
        passWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        passWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        pfPassword = new JPasswordField();
        pfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pfPassword.setPreferredSize(new Dimension(0, 42));
        pfPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            new EmptyBorder(8, 12, 8, 12)
        ));
        pfPassword.setEchoChar('•');
        pfPassword.setBackground(AppColors.INPUT_BG);

        btnShowPassword = UIComponents.createSecondaryButton("Show");
        btnShowPassword.setPreferredSize(new Dimension(74, 42));
        btnShowPassword.addActionListener(e -> togglePasswordVisibility());

        passWrap.add(pfPassword, BorderLayout.CENTER);
        passWrap.add(btnShowPassword, BorderLayout.EAST);

        cbRememberMe = new JCheckBox("Remember me");
        cbRememberMe.setOpaque(false);
        cbRememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRememberMe.setForeground(AppColors.TEXT_SECONDARY);
        cbRememberMe.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = UIComponents.createPrimaryButton("Sign In");
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.addActionListener(e -> tryLogin());
        tfUsername.addActionListener(e -> tryLogin());
        pfPassword.addActionListener(e -> tryLogin());

        lblStatus = new JLabel(" ");
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(new Color(220, 38, 38));

        JLabel hint = new JLabel("Demo: admin/admin123, manager/manager123, staff/staff123");
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(AppColors.TEXT_SECONDARY);

        formWrap.add(formTitle);
        formWrap.add(Box.createVerticalStrut(4));
        formWrap.add(formSub);
        formWrap.add(Box.createVerticalStrut(18));
        formWrap.add(lblUser);
        formWrap.add(Box.createVerticalStrut(6));
        formWrap.add(tfUsername);
        formWrap.add(Box.createVerticalStrut(12));
        formWrap.add(lblPass);
        formWrap.add(Box.createVerticalStrut(6));
        formWrap.add(passWrap);
        formWrap.add(Box.createVerticalStrut(10));
        formWrap.add(cbRememberMe);
        formWrap.add(Box.createVerticalStrut(12));
        formWrap.add(btnLogin);
        formWrap.add(Box.createVerticalStrut(10));
        formWrap.add(lblStatus);
        formWrap.add(Box.createVerticalStrut(8));
        formWrap.add(hint);

        rightPanel.add(formWrap);
        return rightPanel;
    }

    private void togglePasswordVisibility() {
        if (pfPassword.getEchoChar() == '\u0000') {
            pfPassword.setEchoChar('•');
            btnShowPassword.setText("Show");
        } else {
            pfPassword.setEchoChar((char) 0);
            btnShowPassword.setText("Hide");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private void loadRememberedUser() {
        try {
            AuthRepository.RememberedLogin rememberedLogin = authRepository.loadRememberedLogin();
            cbRememberMe.setSelected(rememberedLogin.isRememberMe());
            if (rememberedLogin.isRememberMe() && !rememberedLogin.getUsername().trim().isEmpty()) {
                tfUsername.setText(rememberedLogin.getUsername());
                pfPassword.requestFocusInWindow();
            }
        } catch (Exception exception) {
            cbRememberMe.setSelected(false);
            lblStatus.setForeground(new Color(220, 38, 38));
            lblStatus.setText("Could not load remembered login from database.");
        }
    }

    private void persistRememberedUser(String username) {
        try {
            authRepository.saveRememberedLogin(cbRememberMe.isSelected(), username);
        } catch (Exception exception) {
            lblStatus.setForeground(new Color(220, 38, 38));
            lblStatus.setText("Login worked, but remember-me could not be saved.");
        }
    }

    private void startLockoutCountdown() {
        if (lockoutTimer != null) {
            lockoutTimer.stop();
        }
        lockoutTimer = new Timer(1000, e -> updateLockoutState());
        lockoutTimer.start();
        updateLockoutState();
    }

    private void updateLockoutState() {
        long now = System.currentTimeMillis();
        if (now >= lockoutUntilMillis) {
            if (lockoutTimer != null) {
                lockoutTimer.stop();
            }
            btnLogin.setEnabled(true);
            lblStatus.setForeground(AppColors.TEXT_SECONDARY);
            lblStatus.setText(" ");
            return;
        }

        long remainingMs = lockoutUntilMillis - now;
        long seconds = (remainingMs + 999) / 1000;
        btnLogin.setEnabled(false);
        lblStatus.setForeground(new Color(220, 38, 38));
        lblStatus.setText("Too many failed attempts. Try again in " + seconds + "s.");
    }

    private void tryLogin() {
        String username = normalizeUsername(tfUsername.getText());
        String password = new String(pfPassword.getPassword());

        if (System.currentTimeMillis() < lockoutUntilMillis) {
            updateLockoutState();
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(new Color(220, 38, 38));
            lblStatus.setText("Please enter username and password.");
            return;
        }

        String role;
        try {
            role = authRepository.authenticate(username, password);
        } catch (Exception exception) {
            lblStatus.setForeground(new Color(220, 38, 38));
            lblStatus.setText("Database login failed: " + exception.getMessage());
            return;
        }

        if (role != null) {
            failedAttempts = 0;
            lockoutUntilMillis = 0L;
            btnLogin.setEnabled(true);
            persistRememberedUser(username);
            lblStatus.setForeground(new Color(22, 163, 74));
            lblStatus.setText("Login successful. Welcome, " + role + ".");
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            dispose();
            return;
        }

        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            failedAttempts = 0;
            lockoutUntilMillis = System.currentTimeMillis() + LOCKOUT_DURATION_MS;
            startLockoutCountdown();
            return;
        }

        lblStatus.setForeground(new Color(220, 38, 38));
        int remainingAttempts = MAX_ATTEMPTS - failedAttempts;
        lblStatus.setText("Invalid username or password. Attempts left: " + remainingAttempts + ".");
    }
}
