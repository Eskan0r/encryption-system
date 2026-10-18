import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Logger {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: Logger <logfile>");
            System.exit(1);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(args[0], true));
             Scanner scanner = new Scanner(System.in)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("QUIT")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2);
                String action = parts[0];
                String message = parts.length > 1 ? parts[1] : "";

                String timestamp = LocalDateTime.now().format(formatter);
                writer.println(timestamp + " [" + action + "] " + message);
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }
}
