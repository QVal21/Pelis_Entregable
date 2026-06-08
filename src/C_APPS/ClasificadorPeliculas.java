package C_APPS;

import B_DATASET_UTIL.DataSetPeliculasUtil;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ClasificadorPeliculas {

    public static void recomendarRecursivo(Instances data, J48 model, 
            String[] generosDisponibles, int indice) throws Exception {

        if (indice >= generosDisponibles.length) {
            System.out.println("fin de recomendaciones recursivas");
            return;
        }

        Instance instancia = new DenseInstance(data.numAttributes());
        instancia.setDataset(data);
        instancia.setValue(0, generosDisponibles[indice]);
        instancia.setValue(1, "español");
        instancia.setValue(2, generosDisponibles[indice]);
        instancia.setValue(3, "larga");
        instancia.setValue(4, 4.5);
        instancia.setValue(5, 5);

        double resultado = model.classifyInstance(instancia);
        String clase = data.classAttribute().value((int) resultado);

        System.out.println("Genero [" + generosDisponibles[indice] + 
                          "] → Recomendacion: " + clase);

        recomendarRecursivo(data, model, generosDisponibles, indice + 1);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("---STREAMFLIX - Sistema de Recomendacion---");
        System.out.println("Generando dataset desde base de datos...");
        new DataSetPeliculasUtil().generarDataset();

        Instances data = DataSource.read("datapeliculas.arff");
        System.out.println("Instancias cargadas: " + data.numInstances());
        data.setClassIndex(data.numAttributes() - 1);

        System.out.println("\nEntrenando arbol de decisiones J48...");
        J48 model = new J48();
        model.buildClassifier(data);

        System.out.println("\nPrediccion para usuario nuevo");
        Instance usuarioNuevo = new DenseInstance(data.numAttributes());
        usuarioNuevo.setDataset(data);
        usuarioNuevo.setValue(0, "accion");
        usuarioNuevo.setValue(1, "español");
        usuarioNuevo.setValue(2, "accion");
        usuarioNuevo.setValue(3, "larga");
        usuarioNuevo.setValue(4, 4.5);
        usuarioNuevo.setValue(5, 5);

        double indice = model.classifyInstance(usuarioNuevo);
        String recomendacion = data.classAttribute().value((int) indice);
        System.out.println("¿Se recomienda esta pelicula? → " + recomendacion);

        System.out.println("\nRecorrido recursivo por generos");
        String[] generos = {"accion", "romance", "terror", 
                           "comedia", "drama", "ciencia_ficcion"};
        recomendarRecursivo(data, model, generos, 0);

        System.out.println("\nEvaluacion con Cross-Validation");
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

        System.out.println("\nEstructura del arbol J48");
        System.out.println(model.graph());
    }
}