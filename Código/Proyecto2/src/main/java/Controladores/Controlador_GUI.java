package Controladores;

public class Controlador_GUI {
    
    public Explorador crearExploradorDeArchivos() {
        // Iniciar el explorador de archivos en la ruta "C:/ por defecto"
        return new Explorador("C:/");
    }
}
