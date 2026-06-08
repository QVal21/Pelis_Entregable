package B_DATASET_UTIL;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import A_DB.AccesoDB;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class DataSetPeliculasUtil {

    public void generarDataset() throws SQLException, IOException {

        ResultSet rs = new AccesoDB().cargarDatosEntrenamiento();

        ArrayList<Attribute> atributos = new ArrayList<>();

        ArrayList<String> generosUsuario = new ArrayList<>();
        generosUsuario.add("accion");
        generosUsuario.add("romance");
        generosUsuario.add("terror");
        generosUsuario.add("comedia");
        generosUsuario.add("drama");
        generosUsuario.add("ciencia_ficcion");
        atributos.add(new Attribute("genero_favorito", generosUsuario));

        ArrayList<String> idiomas = new ArrayList<>();
        idiomas.add("español");
        idiomas.add("ingles");
        atributos.add(new Attribute("idioma", idiomas));

        ArrayList<String> generosPelicula = new ArrayList<>();
        generosPelicula.add("accion");
        generosPelicula.add("romance");
        generosPelicula.add("terror");
        generosPelicula.add("comedia");
        generosPelicula.add("drama");
        generosPelicula.add("ciencia_ficcion");
        atributos.add(new Attribute("genero_pelicula", generosPelicula));

        ArrayList<String> duraciones = new ArrayList<>();
        duraciones.add("corta");
        duraciones.add("media");
        duraciones.add("larga");
        atributos.add(new Attribute("duracion", duraciones));

        atributos.add(new Attribute("rating_promedio"));

        atributos.add(new Attribute("calificacion"));

        ArrayList<String> vioCompleta = new ArrayList<>();
        vioCompleta.add("si");
        vioCompleta.add("no");
        atributos.add(new Attribute("recomendar", vioCompleta));

        Instances data = new Instances("streamflix", atributos, 0);
        data.setClassIndex(data.numAttributes() - 1);

        while (rs.next()) {
            Instance inst = new DenseInstance(data.numAttributes());
            inst.setValue(atributos.get(0), rs.getString(1));
            inst.setValue(atributos.get(1), rs.getString(2));
            inst.setValue(atributos.get(2), rs.getString(3));
            inst.setValue(atributos.get(3), rs.getString(4));
            inst.setValue(atributos.get(4), rs.getDouble(5));
            inst.setValue(atributos.get(5), rs.getInt(6));
            inst.setValue(atributos.get(6), rs.getString(7));
            inst.setDataset(data);
            data.add(inst);
        }

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File("datapeliculas.arff"));
        saver.writeBatch();

        System.out.println("Dataset generado: " + data.numInstances() + " instancias.");
    }
}