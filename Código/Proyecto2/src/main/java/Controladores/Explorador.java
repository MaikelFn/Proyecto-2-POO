package Controladores;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class Explorador extends JPanel {

    private JTree fileTree; //Arbol para mostrar los datos
    private JLabel rutaLabel; // JLabel para mostrar la ruta actual
    private JTable fileTable; // JTable para mostrar detalles de los archivos
    private DefaultTableModel tableModel; // Modelo para el JTable

    public Explorador(String rootPath) {
        setLayout(new BorderLayout());

        // Crear el JLabel para mostrar la ruta actual y añadirlo en la parte superior
        rutaLabel = new JLabel("Ruta actual: " + rootPath);
        add(rutaLabel, BorderLayout.NORTH);

        // Crear el árbol de archivos y añadirlo al panel
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootPath);
        fileTree = new JTree(root);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);

        // Crear el JTable para mostrar detalles de los archivos
        tableModel = new DefaultTableModel(new String[]{"Nombre", "Tamaño", "Tipo", "Fecha de creación"}, 0);
        fileTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(fileTable);

        // Añadir ambos componentes en un JSplitPane para dividir la vista
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(350); // Ajusta el tamaño del divisor
        add(splitPane, BorderLayout.CENTER);

        // Cargar archivos desde la carpeta raíz especificada
        File fileRoot = new File(rootPath);
        createNodes(root, fileRoot);

        // Añadir un listener para actualizar la ruta en el JLabel y la tabla al seleccionar un nodo
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                StringBuilder fullPath = new StringBuilder();

                // Construir la ruta completa a partir del TreePath
                for (Object part : path.getPath()) {
                    fullPath.append(part.toString()).append(File.separator);
                }

                // Actualizar el JLabel con la ruta actual
                rutaLabel.setText("Ruta actual: " + fullPath.toString());

                // Actualizar la tabla con los detalles de los archivos de la carpeta seleccionada
                updateFileTable(new File(fullPath.toString()));
            }
        });
    }

    // Método para crear los nodos del árbol de archivos
    private void createNodes(DefaultMutableTreeNode node, File fileRoot) {
        File[] files = fileRoot.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(file.getName());
                node.add(child);
                if (file.isDirectory()) {
                    createNodes(child, file);
                }
            }
        }
    }

    // Actualizar la tabla con los detalles de los archivos de una carpeta
    private void updateFileTable(File folder) {
    // Limpiar la tabla antes de agregar nuevos datos
    tableModel.setRowCount(0);

    File[] files = folder.listFiles();
    if (files != null) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (File file : files) {
            String name = file.getName();
            String size; 
            String type = file.isDirectory() ? "Carpeta" : "Archivo";

            // Si es un directorio, contar cuántos elementos tiene
            if (file.isDirectory()) {
                String[] contents = file.list(); // Obtener los elementos en el directorio
                size = contents != null ? contents.length + " elementos" : "0 elementos";
            } else {
                // Si es un archivo, mostrar su tamaño en bytes
                size = file.length() + " bytes";
            }

            // Fecha de creación
            String creationDate = "-";
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                creationDate = sdf.format(attrs.creationTime().toMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Agregar fila con los detalles del archivo en la tabla
            tableModel.addRow(new Object[]{name, size, type, creationDate});
        }
    }
  }

}
