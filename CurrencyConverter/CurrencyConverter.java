import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {

    static final Map<String, Double> RATES_VS_USD = new HashMap<>();
    static final Map<String, String> CURRENCY_SYMBOLS = new HashMap<>();

    static {
        RATES_VS_USD.put("USD", 1.0);
        RATES_VS_USD.put("INR", 83.50);
        RATES_VS_USD.put("EUR", 0.92);
        RATES_VS_USD.put("GBP", 0.79);
        RATES_VS_USD.put("JPY", 149.50);
        RATES_VS_USD.put("AUD", 1.54);
        RATES_VS_USD.put("CAD", 1.36);
        RATES_VS_USD.put("SGD", 1.34);
        RATES_VS_USD.put("AED", 3.67);
        RATES_VS_USD.put("CNY", 7.24);

        CURRENCY_SYMBOLS.put("USD", "$");
        CURRENCY_SYMBOLS.put("INR", "₹");
        CURRENCY_SYMBOLS.put("EUR", "€");
        CURRENCY_SYMBOLS.put("GBP", "£");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        CURRENCY_SYMBOLS.put("AUD", "A$");
        CURRENCY_SYMBOLS.put("CAD", "C$");
        CURRENCY_SYMBOLS.put("SGD", "S$");
        CURRENCY_SYMBOLS.put("AED", "د.إ");
        CURRENCY_SYMBOLS.put("CNY", "¥");
    }

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        listCurrencies();

        boolean running = true;
        while (running) {
            System.out.println("\n  1. Convert Currency");
            System.out.println("  2. View Exchange Rates Table");
            System.out.println("  3. Exit");
            System.out.print("  Choose: ");

            switch (input.nextLine().trim()) {
                case "1": performConversion(); break;
                case "2": showRatesTable(); break;
                case "3": running = false; break;
                default: System.out.println("  ⚠ Invalid choice.");
            }
        }

        System.out.println("\n  Goodbye! 💸");
        input.close();
    }

    static void performConversion() {
        System.out.print("\n  Enter base currency (e.g., USD): ");
        String from = input.nextLine().trim().toUpperCase();

        System.out.print("  Enter target currency (e.g., INR): ");
        String to = input.nextLine().trim().toUpperCase();

        if (!RATES_VS_USD.containsKey(from)) {
            System.out.println("  ❌ Currency not supported: " + from);
            return;
        }
        if (!RATES_VS_USD.containsKey(to)) {
            System.out.println("  ❌ Currency not supported: " + to);
            return;
        }

        System.out.print("  Enter amount in " + from + ": ");
        double amount = readPositiveDouble();

        double converted = convertAmount(amount, from, to);
        double rate = getRate(from, to);

        String fromSymbol = CURRENCY_SYMBOLS.getOrDefault(from, from);
        String toSymbol = CURRENCY_SYMBOLS.getOrDefault(to, to);

        System.out.println("\n  ┌─────────────────────────────────────┐");
        System.out.printf("  │  %s %.2f  →  %s %.2f  │%n", fromSymbol, amount, toSymbol, converted);
        System.out.printf("  │  Rate: 1 %s = %.4f %s               │%n", from, rate, to);
        System.out.println("  └─────────────────────────────────────┘");
    }

    static double convertAmount(double amount, String from, String to) {
        // Convert from → USD → to
        double inUSD = amount / RATES_VS_USD.get(from);
        return inUSD * RATES_VS_USD.get(to);
    }

    static double getRate(String from, String to) {
        return RATES_VS_USD.get(to) / RATES_VS_USD.get(from);
    }

    static void showRatesTable() {
        System.out.println("\n  Exchange Rates (Base: USD)");
        System.out.println("  ─────────────────────────────────────");
        System.out.printf("  %-6s  %-8s  %s%n", "CODE", "SYMBOL", "RATE vs USD");
        System.out.println("  ─────────────────────────────────────");
        for (Map.Entry<String, Double> entry : RATES_VS_USD.entrySet()) {
            String code = entry.getKey();
            System.out.printf("  %-6s  %-8s  %.4f%n",
                code,
                CURRENCY_SYMBOLS.getOrDefault(code, code),
                entry.getValue());
        }
        System.out.println("  ─────────────────────────────────────");
        System.out.println("  ℹ Rates are indicative. For live rates,");
        System.out.println("    integrate ExchangeRate-API or Fixer.io.");
    }

    static void listCurrencies() {
        System.out.print("  Supported Currencies: ");
        System.out.println(String.join(", ", RATES_VS_USD.keySet()));
    }

    static double readPositiveDouble() {
        while (true) {
            try {
                double val = Double.parseDouble(input.nextLine().trim());
                if (val > 0) return val;
                System.out.print("  ⚠ Enter a positive amount: ");
            } catch (NumberFormatException e) {
                System.out.print("  ⚠ Invalid. Enter a number: ");
            }
        }
    }

    static void printBanner() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      💱 CURRENCY CONVERTER 💱       ║");
        System.out.println("║   Fast • Simple • Multi-currency     ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
