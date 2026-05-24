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

    public DashboardAgriculteur() {

        setTitle("🌿 AgriTIC - Interface Graphique");
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
        Color primary = new Color(41, 128, 185);
        Color green = new Color(46, 204, 113);
        Color red = new Color(231, 76, 60);

        setLayout(new BorderLayout());
        getContentPane().setBackground(bg);

        // ===== HEADER =====
        title = new JLabel("🌾 AGRITIC CENTER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primary);
        title.setBorder(new EmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();
        model.addColumn("🆔 ID");
        model.addColumn("🌽 Culture");
        model.addColumn("💧 Humidité");
        model.addColumn("🌡 Température");
        model.addColumn("⚙ État");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Center alignment
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(scroll, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(bg);

        refreshBtn = new JButton("🔄 Refresh Data");
        irrigateBtn = new JButton("💧 Irrigate P01");

        styleButton(refreshBtn, primary);
        styleButton(irrigateBtn, green);

        status = new JLabel("🟡 System ready...");
        status.setFont(new Font("Segoe UI", Font.BOLD, 14));

        bottom.add(refreshBtn);
        bottom.add(irrigateBtn);
        bottom.add(status);

        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        refreshBtn.addActionListener(e -> loadData());
        irrigateBtn.addActionListener(e -> irrigate());
    }

    // ================= STYLE =================
    private void styleButton(JButton btn, Color color) {
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

                ParsedParcelle pp = parseParcelle(p);

                String stateIcon;
                if (pp.humidite < 30) {
                    stateIcon = "🔥 SECHE";
                } else if (pp.humidite < 70) {
                    stateIcon = "⚠ NORMAL";
                } else {
                    stateIcon = "💧 HUMIDE";
                }

                model.addRow(new Object[]{
                        pp.id,
                        pp.culture,
                        pp.humidite,
                        pp.temperature,
                        stateIcon
                });
            }

            status.setText("🟢 " + service.getEtatPompe());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + e.getMessage());
        }
    }

    // ================= IRRIGATION =================
    private void irrigate() {

        try {
            service.irriguerManuellement("P01");

            JOptionPane.showMessageDialog(this,
                    "💧 Irrigation envoyée pour P01");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur irrigation");
        }
    }

    // ================= PARSER PRO =================
    private ParsedParcelle parseParcelle(String p) {

        ParsedParcelle pp = new ParsedParcelle();

        pp.id = extract(p, "id='", "'");
        pp.culture = extract(p, "culture='", "'");
        pp.humidite = parseDouble(extract(p, "humidite=", ","));
        pp.temperature = parseDouble(extract(p, "temperature=", ","));

        return pp;
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

    private double parseDouble(String val) {
        try {
            return Double.parseDouble(val.replaceAll("[^0-9.]", ""));
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