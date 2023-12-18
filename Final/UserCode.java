import java.io.*; public class UserCode {
    public static void main(String[] args) {long startTime = System.currentTimeMillis();
        int count = 0;
        int number = 2;

        while (count < 5000000) {
            if (isPrime(number)) {
                count++;
                if (count == 5000000) {
                    System.out.println("5000000번째 소수: " + number);
                    break;
                }
            }
            number++;
        }
    long endTime = System.currentTimeMillis(); Runtime.getRuntime().gc(); long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); String form = String.format("/Runtime: %f/Used Memory: %d", (endTime - startTime) / 1000.0, usedMem); try (FileWriter writer = new FileWriter("output.txt")) { 	writer.write(form); } catch (IOException e) { 	e.printStackTrace(); }}

    private static boolean isPrime(int num) {
        if (num < 2) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }


