package org.example;



import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.InstanceList;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MalletTextClassifierTip {

    private Classifier classifier;

    private Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<>();
        pipeList.add(new Target2Label());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}+")));
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));
        pipeList.add(new TokenSequence2FeatureSequence());
        pipeList.add(new FeatureSequence2FeatureVector());
        return new SerialPipes(pipeList);
    }

    public void trainModel(String trainingDataFilePath) throws IOException {
        Pipe pipe = buildPipe();
        InstanceList instances = new InstanceList(pipe);

        instances.addThruPipe(new CsvIterator(new FileReader(new File(trainingDataFilePath)),
                Pattern.compile("^(\\S*)[\\s,]*(.*)$"), 2, 1, -1));

        ClassifierTrainer<?> trainer = new MaxEntTrainer();
        classifier = trainer.train(instances);
    }

    public String classifyText(String text) {
        Classification classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }

    // Збереження моделі
    public void saveModel(String modelFilePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath))) {
            oos.writeObject(classifier);
        }
    }
    public String classifyText(Classifier classifier,String text) {
        return classifier.classify(text).getLabeling().getBestLabel().toString();
    }
    public static void main(String[] args) {
        try {
            MalletTextClassifierTip classifier = new MalletTextClassifierTip();
            // Додайте нові дані в trainingDataALL.csv
            classifier.trainModel("documents.csv");
            classifier.saveModel("classifier_model_tip.dat");

            // Тексти для класифікації
            String[] texts = {
                    "Начальнику служби інфраструктури наказую",
                    "Прошу вашого клопотання перед вищим керівництвом",
                    "Висновок під час проведення робіт з місцевими робітниками",
                    "Схема топології представляє собою структуру мережі, що відображає, як пристрої з’єднані один з одним. Основні типи топологій:",
                    "Донесення міністра інфраструктури Міністерство інфраструктури України Дата: 3.12.2060 Міністр: Тупой Тупорили Вступ Цим донесенням інформую про стан та основні результати роботи Міністерства інфраструктури за [період], а також про ключові виклики, які постали перед нами у сфері розвитку інфраструктури країни."
            };

            // тут беремо данні з навчаногї моделі
            Classifier classifierTip = classifier.loadModel("classifier_model_tip.dat");
            for (String text : texts) {
                String predictedCategory = classifier.classifyText(classifierTip,text);
                System.out.println("Передбачена категорія: " + predictedCategory);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Classifier loadModel(String modelFilePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFilePath))) {
            return (Classifier) ois.readObject();
        }
    }
}
