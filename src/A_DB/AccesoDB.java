package A_DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccesoDB {

    private static final String URL = "jdbc:postgresql://localhost:5432/streamflix";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public ResultSet cargarDatosEntrenamiento() throws SQLException {
        Connection cn = conectar();
        PreparedStatement ps = cn.prepareStatement(
            "SELECT u.genero_favorito, u.idioma, p.genero, p.duracion, " +
            "p.rating_promedio, h.calificacion, h.vio_completa " +
            "FROM historial h " +
            "JOIN usuarios u ON h.id_usuario = u.id " +
            "JOIN peliculas p ON h.id_pelicula = p.id"
        );
        return ps.executeQuery();
    }
}