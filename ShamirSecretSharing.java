import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {


    public static BigInteger decode(String value, int base) {
        return new BigInteger(value, base);
    }

    public static BigInteger lagrangeInterpolation(List<Integer> x, List<BigInteger> y, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i);

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger numerator = BigInteger.valueOf(-x.get(j)); 
                    BigInteger denominator = BigInteger.valueOf(x.get(i) - x.get(j)); 
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            
            BufferedReader reader = new BufferedReader(new FileReader("test_case2.json"));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            reader.close();

            String json = jsonBuilder.toString();
            int n = Integer.parseInt(json.split("\"n\":")[1].split(",")[0].trim());
            int k = Integer.parseInt(json.split("\"k\":")[1].split("}")[0].trim());
            System.out.println("Parsed n: " + n + ", k: " + k);
            if (k > n) {
                System.out.println("Error: k cannot be greater than n.");
                return;
            }

            List<Integer> x = new ArrayList<>();
            List<BigInteger> y = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                String index = "\"" + i + "\":";
                int startIndex = json.indexOf(index) + index.length();
                int endIndex = json.indexOf("}", startIndex);

                String point = json.substring(startIndex, endIndex + 1);

                String baseStr = point.split("\"base\":")[1].split(",")[0].trim();
                if (baseStr.isEmpty()) {
                    System.out.println("Failed to extract base for index " + i);
                    continue;
                }

                baseStr = baseStr.replaceAll("\"", "").trim();
                int base = Integer.parseInt(baseStr);

                String valueStr = point.split("\"value\":")[1].split("\"")[1].trim();
                if (valueStr.isEmpty()) {
                    System.out.println("Failed to extract value for index " + i);
                    continue;
                }

                valueStr = valueStr.replaceAll("\"", "").trim();

                System.out.println("Index: " + i + ", Base: " + base + ", Value: " + valueStr);

                x.add(i); 
                y.add(decode(valueStr, base)); 
            }

            if (x.size() < k || y.size() < k) {
                System.out.println("Error: Insufficient data for interpolation. x or y size is less than k.");
                return;
            }

            System.out.println("x: " + x);
            System.out.println("y: " + y);

            BigInteger constantTerm = lagrangeInterpolation(x.subList(0, k), y.subList(0, k), k);

            System.out.println("The constant term (f(0)) is: " + constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// output for testcase 1 : 3
// output for testcase 2 : 79836264049764
