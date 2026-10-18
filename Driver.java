import java.io.*;
import java.util.*;

public class Driver {
    private static Process loggerProcess;
    private static Process encryptProcess;
    private static PrintWriter toLogger;
    private static PrintWriter toEncrypt;
    private static Scanner fromEncrypt;
    private static List<String> history = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: Driver <logfile>");
            System.exit(1);
        }

        try {
            // Launch logger process
            ProcessBuilder loggerPB = new ProcessBuilder("java", "Logger", args[0]);
            loggerProcess = loggerPB.start();
            toLogger = new PrintWriter(loggerProcess.getOutputStream(), true);

            // Launch encryption process
            ProcessBuilder encryptPB = new ProcessBuilder("java", "Encryption");
            encryptProcess = encryptPB.start();
            toEncrypt = new PrintWriter(encryptProcess.getOutputStream(), true);
            fromEncrypt = new Scanner(encryptProcess.getInputStream());

            log("START", "Driver started");
            runMenu();

        } catch (IOException e) {
            System.err.println("Error launching processes: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private static void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. password");
            System.out.println("2. encrypt");
            System.out.println("3. decrypt");
            System.out.println("4. history");
            System.out.println("5. quit");
            System.out.print("Command: ");

            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "password":
                case "1":
                    handlePassword(scanner);
                    break;
                case "encrypt":
                case "2":
                    handleEncryptDecrypt(scanner, true);
                    break;
                case "decrypt":
                case "3":
                    handleEncryptDecrypt(scanner, false);
                    break;
                case "history":
                case "4":
                    showHistory();
                    break;
                case "quit":
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private static void handlePassword(Scanner scanner) {
        System.out.println("1. Enter new password");
        System.out.println("2. Use from history");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        String password = null;
        if (choice.equals("1")) {
            System.out.print("Enter password: ");
            password = scanner.nextLine().trim();
            if (!isValidLetters(password)) {
                System.out.println("Error: Password must contain only letters");
                return;
            }
        } else if (choice.equals("2")) {
            password = selectFromHistory(scanner);
            if (password == null) return;
        }

        if (password != null) {
            sendToEncrypt("PASS " + password.toUpperCase());
            String response = readFromEncrypt();
            log("COMMAND", "password set");
            log("RESULT", response);
            System.out.println(response);
        }
    }

    private static void handleEncryptDecrypt(Scanner scanner, boolean encrypt) {
        String cmd = encrypt ? "encrypt" : "decrypt";
        System.out.println("1. Enter new string");
        System.out.println("2. Use from history");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        String text = null;
        if (choice.equals("1")) {
            System.out.print("Enter string: ");
            text = scanner.nextLine().trim();
            if (!isValidLetters(text)) {
                System.out.println("Error: String must contain only letters");
                return;
            }
            history.add(text);
        } else if (choice.equals("2")) {
            text = selectFromHistory(scanner);
            if (text == null) return;
        }

        if (text != null) {
            String encCmd = encrypt ? "ENCRYPT " : "DECRYPT ";
            sendToEncrypt(encCmd + text.toUpperCase());
            String response = readFromEncrypt();
            log("COMMAND", cmd + " " + text);
            log("RESULT", response);
            System.out.println(response);

            if (response.startsWith("RESULT ")) {
                String result = response.substring(7);
                history.add(result);
            }
        }
    }

    private static String selectFromHistory(Scanner scanner) {
        if (history.isEmpty()) {
            System.out.println("History is empty");
            return null;
        }
        showHistory();
        System.out.print("Enter number (0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null;
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < history.size()) {
                return history.get(index);
            }
        } catch (NumberFormatException e) {}
        System.out.println("Invalid selection");
        return null;
    }

    private static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("History is empty");
            return;
        }
        System.out.println("=== History ===");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    private static boolean isValidLetters(String s) {
        return !s.isEmpty() && s.chars().allMatch(Character::isLetter);
    }

    private static void sendToEncrypt(String cmd) {
        toEncrypt.println(cmd);
        toEncrypt.flush();
    }

    private static String readFromEncrypt() {
        if (fromEncrypt.hasNextLine()) {
            return fromEncrypt.nextLine();
        }
        return "ERROR No response";
    }

    private static void log(String action, String message) {
        toLogger.println(action + " " + message);
        toLogger.flush();
    }

    private static void cleanup() {
        try {
            if (toEncrypt != null) {
                toEncrypt.println("QUIT");
                toEncrypt.flush();
            }
            if (toLogger != null) {
                toLogger.println("QUIT");
                toLogger.flush();
            }
            if (encryptProcess != null) encryptProcess.waitFor();
            if (loggerProcess != null) loggerProcess.waitFor();
        } catch (Exception e) {
            // ignore cleanup errors
        }
    }
}
