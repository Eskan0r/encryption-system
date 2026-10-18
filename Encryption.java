import java.util.Scanner;

public class Encryption {
    private static String passkey = null;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2);
                String command = parts[0].toUpperCase();
                String argument = parts.length > 1 ? parts[1] : "";

                switch (command) {
                    case "PASS":
                        passkey = argument;
                        System.out.println("RESULT");
                        break;
                    case "ENCRYPT":
                        if (passkey == null) {
                            System.out.println("ERROR Password not set");
                        } else {
                            System.out.println("RESULT " + vigenere(argument, passkey, true));
                        }
                        break;
                    case "DECRYPT":
                        if (passkey == null) {
                            System.out.println("ERROR Password not set");
                        } else {
                            System.out.println("RESULT " + vigenere(argument, passkey, false));
                        }
                        break;
                    case "QUIT":
                        return;
                    default:
                        System.out.println("ERROR Unknown command");
                }
                System.out.flush();
            }
        }
    }

    private static String vigenere(String text, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                if (!encrypt) shift = 26 - shift;
                result.append((char) ((c - base + shift) % 26 + base));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
