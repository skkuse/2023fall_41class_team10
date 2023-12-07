import java.io.*; public class UserCode {
    public static void main(String[] args) {long startTime = System.currentTimeMillis();
        // ū ũ���� 2���� �迭 �Ҵ� ����
        int rows = 20000;    // ���� ��
        int columns = 20000; // ���� ��

        // 2���� �迭 �Ҵ�
        int[][] largeArray = new int[rows][columns];

        // �迭�� �� �Ҵ� �Ǵ� ��� �� �پ��� �۾� ����
        // ���� ���, ��� ���ҿ� 1�� �Ҵ��ϴ� ���:
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                largeArray[i][j] = 1;
            }
        }

        // �迭�� �Ϻθ� ����ϴ� ���:
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(largeArray[i][j] + " ");
            }
            System.out.println();
        }
    long endTime = System.currentTimeMillis(); Runtime.getRuntime().gc(); long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); String form = String.format("/Runtime: %f/Used Memory: %d", (endTime - startTime) / 1000.0, usedMem); try (FileWriter writer = new FileWriter("output.txt")) { 	writer.write(form); } catch (IOException e) { 	e.printStackTrace(); }}
}

