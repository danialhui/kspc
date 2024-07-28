import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class RandomObjectGenerator {

    private static final int DEFAULT_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Random random = new Random();

    public static String generateRandomObjects() {
        int randomNumber = random.nextInt(4); 

        switch (randomNumber) {
            case 0:
                return String.format("%d",randomInteger());
            case 1:
                return String.format("%.4f",randomNumber());
            case 2:
                return String.format("%s",randomAlphabets());
            default:
                return String.format("%s",randomAlphaNumeric());
        }
    }

    private static int randomInteger() {
        int min = -100000;
        int max = 100000;
        int intNum = random.nextInt(max - min + 1) + min; // Random int from 5 to 15
        return intNum;
    }

    private static double randomNumber() {
        double min = -100000.0;
        double max = 100000.0;
        double realNum = min + (max - min) * random.nextDouble();
        return realNum;
    }

    private static String randomAlphabets() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" ;
        return randomString(characters);

    }

    private static String randomAlphaNumeric() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" ;
        int spacesBefore = random.nextInt(11);
        int spacesAfter = (10-spacesBefore) > 0 ? random.nextInt(10-spacesBefore) : 0;
        characters = randomString(characters);
        characters = " ".repeat(spacesBefore) + characters + " ".repeat(spacesAfter);
        return characters;
    }

    private static String randomString(String characters) {
        return randomString2(characters, random.nextInt(19 - 8 + 1) + 8 );//random.nextInt(19));
    }

    private static String randomString(String characters, int resultLen) {
        return random.ints(resultLen, 0, characters.length())
                .mapToObj(characters::charAt)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomString2(String characters, int length) {
        if (length < 1) throw new IllegalArgumentException("Length must be positive.");

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }
    public static void main(String[] args) throws IOException {
        String outputFile = (args == null || args.length <= 0)  ? "random_objects.txt" : args[0];
        
        //String currentDir = System.getProperty("user.dir");
        //String fileSeparator = System.getProperty("file.separator");
        System.out.println("RandomObjectGenerator outputFile : "+outputFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            while (Files.size(Paths.get(outputFile)) < DEFAULT_SIZE) {
                String randomObj = generateRandomObjects();
                writer.write(randomObj);
                writer.write(",");
            }
            writer.close();
        }
    }
}
