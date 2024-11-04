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

    private JTree fileTree;
    private JLabel rutaLabel; // JLabel para la ruta actual
    private JTable fileTable; // JTable para mostrar detalles de los archivos
    private DefaultTableModel tableModel; // Modelo para el JTable

    public Explorador(String rootPath) {
        setLayout(new BorderLayout());

        // Crear el JLabel para mostrar la ruta actual y añadirlo
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

        // Añadir ambos componentes en un JSplitPane para dividir la interfaz
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(350); // Tamaño del divisor
        add(splitPane, BorderLayout.CENTER);

        // Cargar archivos desde la carpeta raíz especificada
        File fileRoot = new File(rootPath);
        createNodes(root, fileRoot);

        // Añadir un listener para actualizar la ruta en el JLabel y la tabla al seleccionar un nodo/archivo
        fileTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                StringBuilder fullPath = new StringBuilder();

                // Construir la ruta completa a partir del TreePath (rita actual)
                for (Object part : path.getPath()) {
                    fullPath.append(part.toString()).append(File.separator);
                }

                // Actualizar el JLabel con la ruta actual 
                rutaLabel.setText("Ruta actual: " + fullPath.toString());

                // Actualizar la tabla con los detalles del archivo o carpeta seleccionada
                updateFileTable(new File(fullPath.toString()));
            }
        });
    }

    // Crear los nodos del árbol de archivos
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

    // Actualizar la tabla con los detalles del archivo o carpeta seleccionada
    private void updateFileTable(File folder) {
        // Limpiar la tabla antes de agregar nuevos datos
        tableModel.setRowCount(0);

        if (folder.isDirectory()) {
            // Si se selecciona una carpeta, mostrar el número de elementos
            File[] files = folder.listFiles();
            if (files != null) {
                // Contar los archivos de la carpeta y mostrar sus detalles
                int fileCount = 0;
                int folderCount = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
                for (File file : files) {
                    if (file.isDirectory()) {
                        folderCount++;
                    } else {
                        fileCount++;
                        String name = file.getName();
                        String size = file.length() + " bytes";
                        String type = getFileExtension(file);
                        String creationDate = getFileCreationDate(file, sdf);

                        // Agregar fila con los detalles del archivo
                        tableModel.addRow(new Object[]{name, size, type, creationDate});
                    }
                }

                // Si la carpeta tiene archivos, mostrar el número de archivos
                tableModel.addRow(new Object[]{"Carpeta: " + folder.getName(), fileCount + " archivos, " + folderCount + " carpetas", "", ""});
            }
        } else {
            // Si se selecciona un archivo, mostrar sus detalles
            String name = folder.getName();
            String size = folder.length() + " bytes";
            String type = getFileExtension(folder);
            String creationDate = getFileCreationDate(folder, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));

            // Agregar fila con los detalles del archivo
            tableModel.addRow(new Object[]{name, size, type, creationDate});
        }
    }

    // Obtener la extensión del archivo
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        return (lastIndexOfDot > 0) ? name.substring(lastIndexOfDot + 1).toUpperCase() : "Sin extensión";
    }

    // Obtener la fecha de creación
    private String getFileCreationDate(File file, SimpleDateFormat sdf) {
        String creationDate = "-";
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            creationDate = sdf.format(attrs.creationTime().toMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return creationDate;
    }
}
