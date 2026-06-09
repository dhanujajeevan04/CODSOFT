import java.util.Random;
import java.util.Scanner;
public class NumberGame {

    static final int RANGE_MIN = 1;
    static final int RANGE_MAX = 100;
    static final int MAX_ATTEMPTS = 7;

    static Scanner input = new Scanner(System.in);
    static Random rng = new Random();

    public static void main(String[] args) {
        printBanner();
        int totalRounds = 0;
        int roundsWon = 0;

        boolean keepPlaying = true;

        while (keepPlaying) {
            totalRounds++;
            boolean won = playRound(totalRounds);
            if (won) roundsWon++;

            System.out.print("\n🔁 Play another round? (yes/no): ");
            String choice = input.nextLine().trim().toLowerCase();
            keepPlaying = choice.equals("yes") || choice.equals("y");
        }

        printFinalScore(totalRounds, roundsWon);
        input.close();
    }

    static boolean playRound(int roundNumber) {
        int secretNumber = RANGE_MIN + rng.nextInt(RANGE_MAX - RANGE_MIN + 1);
        int attemptsLeft = MAX_ATTEMPTS;
        int attemptsTaken = 0;

        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("  ROUND " + roundNumber + " — Guess a number between " + RANGE_MIN + " and " + RANGE_MAX);
        System.out.println("  You have " + MAX_ATTEMPTS + " attempts.");
        System.out.println("╚══════════════════════════════╝");

        while (attemptsLeft > 0) {
            System.out.print("  Attempt " + (MAX_ATTEMPTS - attemptsLeft + 1) + "/" + MAX_ATTEMPTS + " → Enter guess: ");

            int guess = readInt();
            if (guess < RANGE_MIN || guess > RANGE_MAX) {
                System.out.println("  ⚠ Please enter a number between " + RANGE_MIN + " and " + RANGE_MAX + ".");
                continue;
            }

            attemptsTaken++;
            attemptsLeft--;

            if (guess == secretNumber) {
                int score = calculateScore(attemptsTaken);
                System.out.println("  ✅ Correct! You found it in " + attemptsTaken + " attempt(s).");
                System.out.println("  ⭐ Round score: " + score + " points");
                return true;
            } else if (guess < secretNumber) {
                System.out.println("  📈 Too low!" + hintMessage(attemptsLeft));
            } else {
                System.out.println("  📉 Too high!" + hintMessage(attemptsLeft));
            }
        }

        System.out.println("  ❌ Out of attempts! The number was: " + secretNumber);
        return false;
    }

    static String hintMessage(int attemptsLeft) {
        if (attemptsLeft == 1) return " (Last chance!)";
        if (attemptsLeft <= 2) return " (" + attemptsLeft + " attempts left — be careful!)";
        return " (" + attemptsLeft + " attempts remaining)";
    }

    static int calculateScore(int attempts) {
        // More points for fewer attempts
        return Math.max(10, (MAX_ATTEMPTS - attempts + 1) * 15);
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid input. Enter a whole number: ");
            }
        }
    }

    static void printBanner() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║        🎯  NUMBER  GAME  🎯        ║");
        System.out.println("║  Guess the secret number to win!   ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    static void printFinalScore(int rounds, int wins) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("          🏆 GAME OVER 🏆");
        System.out.printf("   Rounds Played : %d%n", rounds);
        System.out.printf("   Rounds Won    : %d%n", wins);
        System.out.printf("   Rounds Lost   : %d%n", rounds - wins);
        System.out.printf("   Win Rate      : %.0f%%%n", (wins * 100.0 / rounds));
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("  Thanks for playing! See you next time.");
    }
}
