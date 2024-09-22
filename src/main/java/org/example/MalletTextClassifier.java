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

public class MalletTextClassifier {

    private Classifier classifier;

    // Створення послідовності Pipes для обробки тексту
    private Pipe buildPipe() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        // Перетворюємо текст у токени
        pipeList.add(new Target2Label());  // Позначаємо цільові метки
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}+"))); // Токенізація
        pipeList.add(new TokenSequenceLowercase());  // Зниження регістру
        pipeList.add(new TokenSequenceRemoveStopwords(false, false));  // Видалення стоп-слів
        pipeList.add(new TokenSequence2FeatureSequence());  // Перетворення токенів у послідовність ознак
        pipeList.add(new FeatureSequence2FeatureVector());  // Перетворення послідовності ознак у вектор ознак

        return new SerialPipes(pipeList);
    }

    // Навчання моделі на основі CSV-файлу
    public void trainModel(String trainingDataFilePath) throws IOException {
        Pipe pipe = buildPipe();  // Створюємо послідовність Pipe для обробки даних
        InstanceList instances = new InstanceList(pipe);

        // CSV формат: category, text
        instances.addThruPipe(new CsvIterator(new FileReader(new File(trainingDataFilePath)),
                Pattern.compile("^(\\S*)[\\s,]*(.*)$"), 2, 1, -1));

        ClassifierTrainer<?> trainer = new MaxEntTrainer();
        classifier = trainer.train(instances);
    }

    // Класифікація тексту
    public String classifyText(String text) {
        Classification classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }
    public String classifyText(Classification classification,String text) {
        classification = classifier.classify(text);
        return classification.getLabeling().getBestLabel().toString();
    }
    // Збереження моделі
    public void saveModel(String modelFilePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFilePath))) {
            oos.writeObject(classifier);
        }
    }

    // Завантаження моделі
    public void loadModel(String modelFilePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFilePath))) {
            classifier = (Classifier) ois.readObject();
        }
    }
    public static void main(String[] args) throws IOException {
        try {
            MalletTextClassifier classifier = new MalletTextClassifier();
            classifier.saveModel("classifier_model.dat");

            // Навчання моделі
            classifier.trainModel("trainingDataALL.csv");
            String[] Text =
                    {
                            "Метою місії була доставка важливого секретного вантажу до військово-морської бази у Гібралтарі (Пункт Б), розташованій на координатах 36.1° N, 5.3° W. Згідно з розпорядженням, корабель мав дотримуватися маршруту через Ірландське море, обійти мис Фіністерре на північному заході Іспанії, а потім прямувати через Гібралтарську протоку до місця призначення.",
                            "Жив собі маленький будиночок на околиці лісу. Він був не звичайним будинком, а справжнім другом для всіх, хто проходив повз. Його червоний дах сяяв на сонці, вікна світилися теплим світлом, і завжди, коли до нього підходив хтось втомлений чи сумний, будиночок запрошував їх до себе, щоб відпочити.",
                            "Цим донесенням інформую про стан та основні результати роботи Міністерства інфраструктури за [період], а також про ключові виклики, які постали перед нами у сфері розвитку інфраструктури країни",
                            "У останні кілька місяців в Україні та ряді інших країн було зафіксовано новий вид захворювання, яке викликає серйозну стурбованість серед медичних і ветеринарних служб. Це захворювання вражає як людей, так і тварин, що ускладнює його контроль і лікування.",
                            "Під час руху з бази до Сентервіля не було зафіксовано сторонніх об'єктів чи підозрілих ситуацій. Однак, о 10:45 EDT, за 80 км на південь від Вашингтона, було виявлено невідомий літальний об'єкт, що наближався з південного заходу. Об'єкт зник після активації системи радіоелектронної боротьби.",
                            //Аналіз стану організації та забезпечення захисту інформації
                            "Організація захисту інформації в державних та військових структурах забезпечує базові стандарти безпеки, проте виявлено ряд слабких місць, які можуть становити загрозу конфіденційності даних. Зокрема, проблеми пов'язані з застарілими системами захисту, недостатнім контролем доступу та низькою кібербезпекою. Системи захисту інформації не завжди відповідають новітнім викликам у сфері кібербезпеки.",
                            //Аналіз стану організації та безпеки
                            "Організація систем безпеки на державних та військових об’єктах в цілому відповідає встановленим нормам і регламентам. Однак, під час перевірок виявлено певні проблеми у сфері координації між підрозділами, фізичної охорони об’єктів та забезпечення сучасних засобів захисту інформації. Особлива увага приділяється кібербезпеці, яка на сьогоднішній день є найбільш уразливим аспектом.",
                            //Аналіз стану організації та безпеки засекреченого зв’язку
                            "Система засекреченого зв’язку, що обслуговує стратегічні підрозділи та командні штаби, забезпечує стабільний і захищений обмін інформацією між військовими та урядовими установами. Організація зв'язку базується на сучасних криптографічних методах, включаючи AES-256, а також на мультиплікаційних методах шифрування, що запобігають несанкціонованому доступу до інформації.",
                            //Звіт про стан спеціального зв'язку
                            "Станом на 18 вересня 2024 року, система спеціального зв’язку працює на належному рівні. Усі основні канали захищеного зв'язку функціонують без збоїв. Система базується на криптографічних протоколах шифрування AES-256 для забезпечення високого рівня безпеки передачі інформації.\n" +
                                    "Віддалені підрозділи мають стабільне з’єднання з командними центрами через захищені VPN-тунелі, що забезпечує цілісність і конфіденційність переданих даних. Також, спеціальний зв'язок підтримується через супутникові канали на основі передових систем глобальної комунікації.\n",
                            "О 07:00 JST 15 вересня 2024 року підрозділ 1-го полку піхоти вирушив з військової бази в префектурі Нагано (координати 36.2° N, 138.2° E). Метою операції була доставка важливих військових вантажів до авіабази в Осаці (Пункт Б), розташованої на координатах 34.7° N, 135.5° E. Згідно з планом, підрозділ мав дотримуватися маршруту через гірський хребет Хакусан, обійти місто Кіото і прямувати до кінцевого пункту через місто Нара."
                    };
            classifier.saveModel("classifier_model.dat");

            for (String text : Text) {
                // Класифікація нового тексту
                String predictedCategory = classifier.classifyText(text);
                System.out.println("Передбачена категорія UK: " + predictedCategory);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
