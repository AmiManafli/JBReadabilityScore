import java.util.Scanner;
class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int charNumber = input.length();
        double cgNum = 0;
        for (int i = 0; i < charNumber; i++) {
            char ch = input.charAt(i);
            if (ch == 'g' || ch == 'G' || ch == 'c' || ch == 'C') {
                cgNum++;
            }
        }
        System.out.print(cgNum * 100 / charNumber);
    }
}
