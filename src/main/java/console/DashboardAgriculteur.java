package console;

import rmi.AgriTICRemote;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class DashboardAgriculteur extends JFrame {

    private AgriTICRemote service;

    private JTable table;
    private DefaultTableModel model;

    private JLabel title;
    private JLabel status;

    private JButton refreshBtn;
    private JButton irrigateBtn;

    // 🌿 NOUVEAU : choix parcelle
    private JComboBox<String> comboParcelle;

    public DashboardAgriculteur() {

        setTitle("🌿 AgriTIC -  Dashboard APP");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initRMI();
        initUI();
    }

    // ================= RMI =================
    private void initRMI() {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (AgriTICRemote) registry.lookup("AgriTICService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur connexion RMI : " + e.getMessage());
        }
    }

    // ================= UI =================
    private void initUI() {

        Color bg = new Color(245, 247, 250);
        Color blue = new Color(41, 128, 185);
        Color green = new Color(46, 204, 113);

        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        // ===== HEADER =====
        title = new JLabel("AGRITIC CENTRE DE CONTROLE ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(blue);
        title.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();

        model.addColumn("🆔 ID");
        model.addColumn("🌽 Culture");
        model.addColumn("💧 Humidité (%)");
        model.addColumn("🌡 Température (°C)");
        model.addColumn("⚙ État");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        // ===== PANEL BAS =====
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(bg);

        refreshBtn = new JButton(" Actualiser");

        comboParcelle = new JComboBox<>(new String[]{
                "P01", "P02", "P03", "P04", "P05"
        });

        irrigateBtn = new JButton(" Irriguer");

        style(refreshBtn, blue);
        style(irrigateBtn, green);

        status = new JLabel("🟡 Système prêt...");
        status.setFont(new Font("Segoe UI", Font.BOLD, 14));

        bottom.add(refreshBtn);
        bottom.add(comboParcelle);
        bottom.add(irrigateBtn);
        bottom.add(status);

        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        refreshBtn.addActionListener(e -> loadData());
        irrigateBtn.addActionListener(e -> irrigate());
    }

    // ================= STYLE =================
    private void style(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    // ================= LOAD DATA =================
    private void loadData() {

        try {
            model.setRowCount(0);

            List<String> data = service.getListeParcelles();

            for (String p : data) {

                ParsedParcelle pp = parse(p);

                String etat;

                if (pp.humidite < 30) {
                    etat = " SECHE";
                } else if (pp.humidite < 70) {
                    etat = " NORMAL";
                } else {
                    etat = " HUMIDE";
                }

                model.addRow(new Object[]{
                        pp.id,
                        pp.culture,
                        pp.humidite,
                        pp.temperature,
                        etat
                });
            }

            status.setText("🟢 " + service.getEtatPompe());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + e.getMessage());
        }
    }

    // ================= IRRIGATION (MODIFIÉE) =================
    private void irrigate() {

        try {

            String parcelle = (String) comboParcelle.getSelectedItem();

            service.irriguerManuellement(parcelle);

            JOptionPane.showMessageDialog(this,
                    "💧 Irrigation envoyée pour " + parcelle);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur irrigation");
        }
    }

    // ================= PARSER =================
    private ParsedParcelle parse(String text) {

        ParsedParcelle p = new ParsedParcelle();

        try {
            p.id = extract(text, "id='", "'");
            p.culture = extract(text, "culture='", "'");
            p.humidite = parseDouble(extract(text, "humidite=", ","));
            p.temperature = parseDouble(extract(text, "temperature=", ","));
        } catch (Exception e) {
            p.id = "??";
            p.culture = "??";
            p.humidite = 0;
            p.temperature = 0;
        }

        return p;
    }

    private String extract(String text, String start, String end) {
        try {
            int s = text.indexOf(start) + start.length();
            int e = text.indexOf(end, s);
            if (e == -1) e = text.length();
            return text.substring(s, e).replace("'", "").trim();
        } catch (Exception e) {
            return "?";
        }
    }

    private double parseDouble(String v) {
        try {
            return Double.parseDouble(v.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= STRUCT =================
    static class ParsedParcelle {
        String id;
        String culture;
        double humidite;
        double temperature;
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new DashboardAgriculteur().setVisible(true));
    }
}