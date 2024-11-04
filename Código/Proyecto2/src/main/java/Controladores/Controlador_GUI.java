package Controladores;

public class Controlador_GUI {
    
    public Explorador crearExploradorDeArchivos() {
        // Inicializar el explorador de archivos en la ruta "C:/"
        return new Explorador("C:/");
    }
}
