import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {


    public static BigInteger decode(String value, int base) {
        return new BigInteger(value, base);
    }

    // Lagrange interpolation to find f(0)
    public static BigInteger lagrangeInterpolation(List<Integer> x, List<BigInteger> y, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i);

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = BigInteger.valueOf(-x.get(j)); // 0 - x[j]
                    BigInteger denominator = BigInteger.valueOf(x.get(i) - x.get(j)); // x[i] - x[j]
                    term = term.multiply(numerator).divide(denominator);
                }
            }

            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            // Read the file line by line
            BufferedReader reader = new BufferedReader(new FileReader("test_case2.json"));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            reader.close();

            String json = jsonBuilder.toString();

            // Parse the keys
            int n = Integer.parseInt(json.split("\"n\":")[1].split(",")[0].trim());
            int k = Integer.parseInt(json.split("\"k\":")[1].split("}")[0].trim());

            // Debugging output for n and k
            System.out.println("Parsed n: " + n + ", k: " + k);

            // Ensure k <= n
            if (k > n) {
                System.out.println("Error: k cannot be greater than n.");
                return;
            }

            List<Integer> x = new ArrayList<>();
            List<BigInteger> y = new ArrayList<>();

            // Parse each key-value pair
            for (int i = 1; i <= n; i++) {
                String index = "\"" + i + "\":";
                int startIndex = json.indexOf(index) + index.length();
                int endIndex = json.indexOf("}", startIndex);

                String point = json.substring(startIndex, endIndex + 1);

                // Extract the base value, ensuring proper trimming of quotes and spaces
                String baseStr = point.split("\"base\":")[1].split(",")[0].trim();
                if (baseStr.isEmpty()) {
                    System.out.println("Failed to extract base for index " + i);
                    continue;
                }

                baseStr = baseStr.replaceAll("\"", "").trim();
                int base = Integer.parseInt(baseStr);

                // Extract the value, ensuring proper trimming of quotes
                String valueStr = point.split("\"value\":")[1].split("\"")[1].trim();
                if (valueStr.isEmpty()) {
                    System.out.println("Failed to extract value for index " + i);
                    continue;
                }

                valueStr = valueStr.replaceAll("\"", "").trim();

                // Debugging output for each parsed entry
                System.out.println("Index: " + i + ", Base: " + base + ", Value: " + valueStr);

                x.add(i); // x is the key (1, 2, 3, ..., n)
                y.add(decode(valueStr, base)); // Decode y based on the given base
            }

            if (x.size() < k || y.size() < k) {
                System.out.println("Error: Insufficient data for interpolation. x or y size is less than k.");
                return;
            }

            // Debugging output for the x and y lists
            System.out.println("x: " + x);
            System.out.println("y: " + y);

            // Use the first k points for interpolation
            BigInteger constantTerm = lagrangeInterpolation(x.subList(0, k), y.subList(0, k), k);

            System.out.println("The constant term (f(0)) is: " + constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// output for testcase 1 : 3
// output for testcase 2 : 79836264049764