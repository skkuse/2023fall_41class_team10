import java.io.*; public class UserCode {
    public static void main(String[] args) {long startTime = System.currentTimeMillis();
        // 큰 크기의 2차원 배열 할당 예제
        int rows = 20000;    // 행의 수
        int columns = 20000; // 열의 수

        // 2차원 배열 할당
        int[][] largeArray = new int[rows][columns];

        // 배열에 값 할당 또는 출력 등 다양한 작업 수행
        // 예를 들어, 모든 원소에 1을 할당하는 경우:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                largeArray[i][j] = 1;
            }
        }

        // 배열의 일부를 출력하는 경우:
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(largeArray[i][j] + " ");
            }
            System.out.println();
        }
    long endTime = System.currentTimeMillis(); Runtime.getRuntime().gc(); long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); String form = String.format("/Runtime: %f/Used Memory: %d", (endTime - startTime) / 1000.0, usedMem); try (FileWriter writer = new FileWriter("output.txt")) { 	writer.write(form); } catch (IOException e) { 	e.printStackTrace(); }}
}

