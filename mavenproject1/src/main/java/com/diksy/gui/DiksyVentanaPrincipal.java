package com.diksy.gui;

import com.diksy.BuscarPalabras;
import com.diksy.Idioma;
import com.diksy.Palabra;
import com.diksy.RepoPalabras;
import com.diksy.TsvStorage;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DiksyVentanaPrincipal extends JFrame {

    private final TsvStorage storage;
    private final RepoPalabras repo;
    private Idioma idiomaActual = Idioma.ESPANOL;

    // Componentes principales
    private JTable tablaPalabras;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JTextField txtPalabra;
    private JTextField txtFuente;
    private JTextField txtFecha;
    private JTextArea txtDefinicion;
    private JLabel lblIdiomaTitulo;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnNuevo;

    private Palabra palabraSeleccionada = null;

    public DiksyVentanaPrincipal() {
        this.storage = new TsvStorage("datos");
        this.repo = new RepoPalabras();

        configurarVentana();
        inicializarUI();
        cargarDatosPorIdioma(Idioma.ESPANOL);
    }

    private void configurarVentana() {
        setTitle("Diksy — Gestor de Palabras y Definiciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
    }

    private void inicializarUI() {
        JPanel panelRaiz = new JPanel(new BorderLayout());

        // 1. Barra Lateral (Sidebar de Idiomas)
        panelRaiz.add(crearSidebar(), BorderLayout.WEST);

        // 2. Área Central (Buscador, Tabla y Detalle)
        panelRaiz.add(crearPanelCentral(), BorderLayout.CENTER);

        setContentPane(panelRaiz);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));
        sidebar.setBackground(UIManager.getColor("Panel.background").darker());

        JLabel lblLogo = new JLabel("📖 Diksy");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Diccionario Personal");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSub);
        sidebar.add(Box.createVerticalStrut(30));

        JLabel lblSeccion = new JLabel("IDIOMAS");
        lblSeccion.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblSeccion.setForeground(Color.GRAY);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblSeccion);
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnEspanol = new JButton("🇪🇸 Español");
        JButton btnIngles = new JButton("🇬🇧 Inglés");

        estilizarBotonSidebar(btnEspanol);
        estilizarBotonSidebar(btnIngles);

        btnEspanol.addActionListener(e -> {
            idiomaActual = Idioma.ESPANOL;
            lblIdiomaTitulo.setText("🇪🇸 Español");
            cargarDatosPorIdioma(idiomaActual);
        });

        btnIngles.addActionListener(e -> {
            idiomaActual = Idioma.INGLES;
            lblIdiomaTitulo.setText("🇬🇧 Inglés");
            cargarDatosPorIdioma(idiomaActual);
        });

        sidebar.add(btnEspanol);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnIngles);

        sidebar.add(Box.createVerticalGlue());

        JLabel lblInfo = new JLabel("v1.0 • Offline");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfo.setForeground(Color.DARK_GRAY);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblInfo);

        return sidebar;
    }

    private void estilizarBotonSidebar(JButton btn) {
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 42));
        btn.setPreferredSize(new Dimension(160, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
    }

    private JPanel crearPanelCentral() {
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Barra superior con Título y Buscador
        JPanel barraSuperior = new JPanel(new BorderLayout(15, 0));
        lblIdiomaTitulo = new JLabel("🇪🇸 Español");
        lblIdiomaTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        barraSuperior.add(lblIdiomaTitulo, BorderLayout.WEST);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        txtBuscar = new JTextField(22);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por palabra o definición...");
        txtBuscar.putClientProperty("JTextField.showClearButton", true);
        txtBuscar.putClientProperty("JComponent.roundRect", true);

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        btnNuevo = new JButton("+ Nueva Palabra");
        btnNuevo.putClientProperty("JButton.buttonType", "roundRect");
        btnNuevo.setBackground(new Color(33, 115, 70));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.addActionListener(e -> limpiarFormulario());

        panelBusqueda.add(new JLabel("🔍"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnNuevo);

        barraSuperior.add(panelBusqueda, BorderLayout.EAST);
        panelCentral.add(barraSuperior, BorderLayout.NORTH);

        // División: Izquierda Tabla - Derecha Editor/Detalle
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);

        // Tabla
        String[] columnas = {"ID", "Palabra", "Fuente", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPalabras = new JTable(modeloTabla);
        // Ocultar visualmente la columna ID para el usuario final (permanece en el modelo de datos)
        tablaPalabras.removeColumn(tablaPalabras.getColumn("ID"));

        tablaPalabras.setRowHeight(28);
        tablaPalabras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPalabras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaPalabras.getSelectedRow() != -1) {
                int filaVisual = tablaPalabras.getSelectedRow();
                int filaModelo = tablaPalabras.convertRowIndexToModel(filaVisual);
                int id = (int) modeloTabla.getValueAt(filaModelo, 0);
                mostrarDetallePalabra(id);
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaPalabras);
        scrollTabla.putClientProperty("JScrollPane.smoothScrolling", true);
        splitPane.setLeftComponent(scrollTabla);

        // Panel de Edición/Detalle
        splitPane.setRightComponent(crearPanelEditor());

        panelCentral.add(splitPane, BorderLayout.CENTER);
        return panelCentral;
    }

    private JPanel crearPanelEditor() {
        JPanel panelEditor = new JPanel(new BorderLayout(10, 10));
        panelEditor.setBorder(new EmptyBorder(10, 15, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Palabra
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Palabra:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPalabra = new JTextField();
        txtPalabra.putClientProperty("JComponent.roundRect", true);
        form.add(txtPalabra, gbc);

        // Fila 2: Fuente
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Fuente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFuente = new JTextField();
        txtFuente.putClientProperty("JTextField.placeholderText", "Ej: RAE, Wikipedia, Libro...");
        txtFuente.putClientProperty("JComponent.roundRect", true);
        form.add(txtFuente, gbc);

        // Fila 3: Fecha
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFecha = new JTextField();
        txtFecha.setText(LocalDate.now().toString());
        txtFecha.putClientProperty("JTextField.placeholderText", "AAAA-MM-DD o DD/MM/AAAA");
        txtFecha.putClientProperty("JComponent.roundRect", true);

        JPanel panelFecha = new JPanel(new BorderLayout(6, 0));
        panelFecha.add(txtFecha, BorderLayout.CENTER);

        JButton btnHoy = new JButton("Hoy");
        btnHoy.setToolTipText("Establecer la fecha actual de hoy");
        btnHoy.putClientProperty("JButton.buttonType", "roundRect");
        btnHoy.addActionListener(e -> txtFecha.setText(LocalDate.now().toString()));
        panelFecha.add(btnHoy, BorderLayout.EAST);

        form.add(panelFecha, gbc);

        // Fila 4: Definición
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Definición:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtDefinicion = new JTextArea();
        txtDefinicion.setLineWrap(true);
        txtDefinicion.setWrapStyleWord(true);
        txtDefinicion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scrollDef = new JScrollPane(txtDefinicion);
        scrollDef.putClientProperty("JScrollPane.smoothScrolling", true);
        form.add(scrollDef, gbc);

        panelEditor.add(form, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.setForeground(new Color(230, 80, 80));
        btnEliminar.addActionListener(e -> accionEliminar());

        btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> accionGuardar());

        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelEditor.add(panelBotones, BorderLayout.SOUTH);

        return panelEditor;
    }

    private void cargarDatosPorIdioma(Idioma idioma) {
        repo.limpiar();
        try {
            storage.cargarEnRepositorio(repo, idioma);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer datos locales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTabla(repo.listar());
        limpiarFormulario();
    }

    private void actualizarTabla(List<Palabra> palabras) {
        modeloTabla.setRowCount(0);
        for (Palabra p : palabras) {
            modeloTabla.addRow(new Object[]{p.getIdPalabra(), p.getPalabra(), p.getFuente(), p.getFecha()});
        }
    }

    private void mostrarDetallePalabra(int id) {
        palabraSeleccionada = repo.buscarPorId(id);
        if (palabraSeleccionada != null) {
            txtPalabra.setText(palabraSeleccionada.getPalabra());
            txtFuente.setText(palabraSeleccionada.getFuente());
            txtFecha.setText(palabraSeleccionada.getFecha().toString());
            txtDefinicion.setText(palabraSeleccionada.getDefinicion());
            btnEliminar.setEnabled(true);
            btnGuardar.setText("Actualizar");
        }
    }

    private void limpiarFormulario() {
        palabraSeleccionada = null;
        txtPalabra.setText("");
        txtFuente.setText("");
        txtFecha.setText(LocalDate.now().toString());
        txtDefinicion.setText("");
        btnEliminar.setEnabled(false);
        btnGuardar.setText("Guardar");
        tablaPalabras.clearSelection();
        txtPalabra.requestFocus();
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            actualizarTabla(repo.listar());
        } else {
            List<Palabra> filtradas = BuscarPalabras.buscar(repo.listar(), texto);
            actualizarTabla(filtradas);
        }
    }

    public static LocalDate parsearFecha(String fechaStr) throws DateTimeParseException {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        String texto = fechaStr.trim();
        DateTimeFormatter[] formatos = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,         // yyyy-MM-dd
                DateTimeFormatter.ofPattern("d/M/yyyy"),   // d/M/yyyy o dd/MM/yyyy
                DateTimeFormatter.ofPattern("d-M-yyyy"),   // d-M-yyyy o dd-MM-yyyy
                DateTimeFormatter.ofPattern("yyyy/M/d")    // yyyy/M/d o yyyy/MM/dd
        };
        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(texto, formato);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new DateTimeParseException("Formato de fecha no reconocido: " + texto, texto, 0);
    }

    private void accionGuardar() {
        String palabraStr = txtPalabra.getText().trim();
        String definicionStr = txtDefinicion.getText().trim();
        String fuenteStr = txtFuente.getText().trim();

        if (palabraStr.isEmpty() || definicionStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La palabra y la definición no pueden estar vacías.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaSeleccionada;
        try {
            fechaSeleccionada = parsearFecha(txtFecha.getText());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "La fecha ingresada no es válida.\n"
                    + "Por favor use el formato AAAA-MM-DD o DD/MM/AAAA (ej: "
                    + LocalDate.now() + " o 28/10/2026).",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE
            );
            txtFecha.requestFocus();
            return;
        }

        try {
            if (palabraSeleccionada == null) {
                // Nueva palabra
                Palabra nueva = new Palabra(palabraStr, definicionStr, fuenteStr, fechaSeleccionada, idiomaActual);
                storage.agregarPalabra(nueva);
            } else {
                // Editar existente
                palabraSeleccionada.setPalabra(palabraStr);
                palabraSeleccionada.setDefinicion(definicionStr);
                palabraSeleccionada.setFuente(fuenteStr);
                palabraSeleccionada.setFecha(fechaSeleccionada);
                storage.guardarTodo(idiomaActual, repo.listar());
            }
            cargarDatosPorIdioma(idiomaActual);
            JOptionPane.showMessageDialog(this, "¡Guardado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminar() {
        if (palabraSeleccionada == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar '" + palabraSeleccionada.getPalabra() + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                repo.eliminar(palabraSeleccionada);
                storage.guardarTodo(idiomaActual, repo.listar());
                cargarDatosPorIdioma(idiomaActual);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Inicializar tema moderno FlatLaf Dark
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            DiksyVentanaPrincipal ventana = new DiksyVentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}

