package C_APPS;

import B_DATASET_UTIL.DataSetPeliculasUtil;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ClasificadorPeliculas {

    // FUNCION RECURSIVA: recorre el arbol de recomendaciones
    public static void recomendarRecursivo(Instances data, J48 model, 
            String[] generosDisponibles, int indice) throws Exception {

        // caso base: ya revisamos todos los generos
        if (indice >= generosDisponibles.length) {
            System.out.println("--- Fin de recomendaciones recursivas ---");
            return;
        }

        // crear instancia para este genero
        Instance instancia = new DenseInstance(data.numAttributes());
        instancia.setDataset(data);
        instancia.setValue(0, generosDisponibles[indice]); // genero_favorito
        instancia.setValue(1, "español");                   // idioma
        instancia.setValue(2, generosDisponibles[indice]); // genero_pelicula
        instancia.setValue(3, "larga");                     // duracion
        instancia.setValue(4, 4.5);                         // rating_promedio
        instancia.setValue(5, 5);                           // calificacion

        double resultado = model.classifyInstance(instancia);
        String clase = data.classAttribute().value((int) resultado);

        System.out.println("Genero [" + generosDisponibles[indice] + 
                          "] → Recomendacion: " + clase);

        // llamada recursiva con el siguiente genero
        recomendarRecursivo(data, model, generosDisponibles, indice + 1);
    }

    public static void main(String[] args) throws Exception {

        // PASO 1: generar dataset desde PostgreSQL
        System.out.println("=== STREAMFLIX - Sistema de Recomendacion ===");
        System.out.println("Generando dataset desde base de datos...");
        new DataSetPeliculasUtil().generarDataset();

        // PASO 2: cargar el archivo arff generado
        Instances data = DataSource.read("datapeliculas.arff");
        System.out.println("Instancias cargadas: " + data.numInstances());
        data.setClassIndex(data.numAttributes() - 1);

        // PASO 3: entrenar el arbol de decisiones J48
        System.out.println("\nEntrenando arbol de decisiones J48...");
        J48 model = new J48();
        model.buildClassifier(data);

        // PASO 4: predecir para un usuario nuevo
        System.out.println("\n--- Prediccion para usuario nuevo ---");
        Instance usuarioNuevo = new DenseInstance(data.numAttributes());
        usuarioNuevo.setDataset(data);
        usuarioNuevo.setValue(0, "accion");   // genero favorito
        usuarioNuevo.setValue(1, "español");  // idioma
        usuarioNuevo.setValue(2, "accion");   // genero pelicula
        usuarioNuevo.setValue(3, "larga");    // duracion
        usuarioNuevo.setValue(4, 4.5);        // rating promedio
        usuarioNuevo.setValue(5, 5);          // calificacion

        double indice = model.classifyInstance(usuarioNuevo);
        String recomendacion = data.classAttribute().value((int) indice);
        System.out.println("¿Se recomienda esta pelicula? → " + recomendacion);

        // PASO 5: recursividad sobre todos los generos
        System.out.println("\n--- Recorrido recursivo por generos ---");
        String[] generos = {"accion", "romance", "terror", 
                           "comedia", "drama", "ciencia_ficcion"};
        recomendarRecursivo(data, model, generos, 0);
        
        
     // PASO 6: cross-validation (evaluacion del modelo)
        System.out.println("\n--- Evaluacion con Cross-Validation (10 folds) ---");
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
        eval.crossValidateModel(model, data, 10, new java.util.Random(1));

        System.out.println("Precision general: " + 
            String.format("%.2f", eval.pctCorrect()) + "%");
        System.out.println("Error medio absoluto: " + 
            String.format("%.4f", eval.meanAbsoluteError()));
        System.out.println("Instancias correctas: " + 
            (int) eval.correct() + " de " + data.numInstances());
        System.out.println("Instancias incorrectas: " + 
            (int) eval.incorrect());

     // PASO 7: mostrar el arbol generado
        System.out.println("\n--- Estructura del arbol J48 ---");
        System.out.println(model.graph());
    }
}