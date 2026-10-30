import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class AESProject extends JFrame {

    private JTextField txtKeyEncrypt;
    private JTextArea txtPlaintext;
    private JTextArea txtCiphertextResult;

    private JTextField txtKeyDecrypt;
    private JTextArea txtCiphertextInput;
    private JTextArea txtPlaintextResult;

    private final Color CARD_BG = new Color(24, 32, 47, 235);
    private final Color TEXT_LIGHT = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);
    
    private final Color ACCENT_GREEN = new Color(16, 185, 129);
    private final Color ACCENT_RED = new Color(225, 29, 72);
    private final Color ACCENT_BLUE = new Color(79, 70, 229);

    public AESProject() {
        setTitle("AES-128 Cryptography Studio - Kelompok 1");
        setSize(940, 670);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        CyberBackgroundPanel mainPanel = new CyberBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        Font fontTitle = new Font("Segoe UI", Font.BOLD, 22);
        Font fontSubtitle = new Font("Segoe UI", Font.PLAIN, 13);

        // --- HEADER ---
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("STUDIO KRIPTOGRAFI AES-128");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(TEXT_LIGHT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Modul Pemrosesan Terpisah: Enkripsi Data & Dekripsi Data");
        lblSub.setFont(fontSubtitle);
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(lblTitle);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 4)));
        panelHeader.add(lblSub);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- BODY 2 KOLOM ---
        JPanel panelCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCenter.setOpaque(false);

        panelCenter.add(createEncryptionCard());
        panelCenter.add(createDecryptionCard());

        mainPanel.add(panelHeader, BorderLayout.NORTH);
        mainPanel.add(panelCenter, BorderLayout.CENTER);

        add(mainPanel);
    }

    // --- PANEL ENKRIPSI ---
    private JPanel createEncryptionCard() {
        JPanel panel = createStyledCard("PANEL ENKRIPSI", ACCENT_GREEN);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Consolas", Font.PLAIN, 12);

        txtKeyEncrypt = new JTextField();
        styleTextField(txtKeyEncrypt, fontInput);

        txtPlaintext = new JTextArea(4, 20);
        styleTextArea(txtPlaintext, fontInput);

        txtCiphertextResult = new JTextArea(4, 20);
        styleTextArea(txtCiphertextResult, fontInput);
        txtCiphertextResult.setEditable(false);

        RoundedButton btnDoEncrypt = new RoundedButton("PROSES ENKRIPSI", ACCENT_GREEN);
        RoundedButton btnCopyCipher = new RoundedButton("Salin Ciphertext", ACCENT_BLUE);

        btnDoEncrypt.addActionListener(e -> {
            try {
                String key = txtKeyEncrypt.getText().trim();
                String text = txtPlaintext.getText().trim();
                if (key.isEmpty() || text.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Kunci dan Plaintext wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String result = encrypt(text, key);
                txtCiphertextResult.setText(result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal Mengenkripsi Data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCopyCipher.addActionListener(e -> {
            if (!txtCiphertextResult.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtCiphertextResult.getText()), null);
                JOptionPane.showMessageDialog(this, "Ciphertext disalin!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Assembly konsisten Kiri
        panel.add(createLabel("Kunci Rahasia (Secret Key):", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(txtKeyEncrypt);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(createLabel("Plaintext (Teks Asli):", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(createCustomScrollPane(txtPlaintext));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(btnDoEncrypt);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        panel.add(createLabel("Hasil Ciphertext:", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(createCustomScrollPane(txtCiphertextResult));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBottom.setOpaque(false);
        panelBottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelBottom.add(btnCopyCipher);
        panel.add(panelBottom);

        return panel;
    }

    // --- PANEL DEKRIPSI ---
    private JPanel createDecryptionCard() {
        JPanel panel = createStyledCard("PANEL DEKRIPSI", ACCENT_RED);

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Consolas", Font.PLAIN, 12);

        txtKeyDecrypt = new JTextField();
        styleTextField(txtKeyDecrypt, fontInput);

        txtCiphertextInput = new JTextArea(4, 20);
        styleTextArea(txtCiphertextInput, fontInput);

        txtPlaintextResult = new JTextArea(4, 20);
        styleTextArea(txtPlaintextResult, fontInput);
        txtPlaintextResult.setEditable(false);

        RoundedButton btnDoDecrypt = new RoundedButton("PROSES DEKRIPSI", ACCENT_RED);
        RoundedButton btnCopyPlain = new RoundedButton("Salin Teks Asli", ACCENT_BLUE);

        btnDoDecrypt.addActionListener(e -> {
            try {
                String key = txtKeyDecrypt.getText().trim();
                String text = txtCiphertextInput.getText().trim();
                if (key.isEmpty() || text.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Kunci dan Ciphertext wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String result = decrypt(text, key);
                txtPlaintextResult.setText(result);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal Dekripsi! Kunci salah atau format tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCopyPlain.addActionListener(e -> {
            if (!txtPlaintextResult.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtPlaintextResult.getText()), null);
                JOptionPane.showMessageDialog(this, "Teks Asli disalin!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Assembly konsisten Kiri
        panel.add(createLabel("Kunci Rahasia (Secret Key):", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(txtKeyDecrypt);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(createLabel("Ciphertext (Teks Terenkripsi):", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(createCustomScrollPane(txtCiphertextInput));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(btnDoDecrypt);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        panel.add(createLabel("Hasil Plaintext Dekripsi:", fontLabel));
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(createCustomScrollPane(txtPlaintextResult));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBottom.setOpaque(false);
        panelBottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panelBottom.add(btnCopyPlain);
        panel.add(panelBottom);

        return panel;
    }

    private JPanel createStyledCard(String title, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblCardTitle = new JLabel(title);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCardTitle.setForeground(accentColor);
        lblCardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblCardTitle);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        return card;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleTextField(JTextField tf, Font font) {
        tf.setFont(font);
        tf.setBackground(new Color(15, 23, 42));
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(Color.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleTextArea(JTextArea ta, Font font) {
        ta.setFont(font);
        ta.setBackground(new Color(15, 23, 42));
        ta.setForeground(TEXT_LIGHT);
        ta.setCaretColor(Color.WHITE);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
    }

    private JScrollPane createCustomScrollPane(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        sp.setBackground(new Color(15, 23, 42));
        return sp;
    }

    // --- ALGORITMA AES-128 ---
    private SecretKeySpec generateAESKey(String myKey) throws Exception {
        byte[] keyBytes = myKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        keyBytes = sha.digest(keyBytes);
        byte[] aesKey = new byte[16];
        System.arraycopy(keyBytes, 0, aesKey, 0, 16);
        return new SecretKeySpec(aesKey, "AES");
    }

    private String encrypt(String plaintext, String secretKey) throws Exception {
        SecretKeySpec keySpec = generateAESKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decrypt(String ciphertext, String secretKey) throws Exception {
        SecretKeySpec keySpec = generateAESKey(secretKey);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AESProject().setVisible(true));
    }

    // --- TOMBOL MODERN FULL WIDTH ---
    private static class RoundedButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color pressedColor;
        private boolean isHovered = false;

        public RoundedButton(String text, Color baseColor) {
            super(text);
            this.normalColor = baseColor;
            this.hoverColor = baseColor.brighter();
            this.pressedColor = baseColor.darker();

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(pressedColor);
            } else if (isHovered) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(normalColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- ANIMASI BACKGROUND CYBER NETWORK ---
    private static class CyberBackgroundPanel extends JPanel {
        private final ArrayList<Node> nodes = new ArrayList<>();
        private final Random random = new Random();

        public CyberBackgroundPanel() {
            setBackground(new Color(11, 15, 25));

            for (int i = 0; i < 35; i++) {
                nodes.add(new Node(random.nextInt(940), random.nextInt(670)));
            }

            Timer timer = new Timer(30, e -> {
                for (Node n : nodes) {
                    n.update();
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    Node n1 = nodes.get(i);
                    Node n2 = nodes.get(j);
                    double dist = Math.hypot(n1.x - n2.x, n1.y - n2.y);
                    if (dist < 120) {
                        int alpha = (int) (80 * (1 - dist / 120));
                        g2d.setColor(new Color(99, 102, 241, alpha));
                        g2d.drawLine((int) n1.x, (int) n1.y, (int) n2.x, (int) n2.y);
                    }
                }
            }

            for (Node n : nodes) {
                g2d.setColor(new Color(16, 185, 129, 150));
                g2d.fillOval((int) n.x - 2, (int) n.y - 2, 5, 5);
            }
        }

        private class Node {
            double x, y, dx, dy;

            Node(double x, double y) {
                this.x = x;
                this.y = y;
                this.dx = (random.nextDouble() - 0.5) * 1.2;
                this.dy = (random.nextDouble() - 0.5) * 1.2;
            }

            void update() {
                x += dx;
                y += dy;
                if (x < 0 || x > 940) dx *= -1;
                if (y < 0 || y > 670) dy *= -1;
            }
        }
    }
}