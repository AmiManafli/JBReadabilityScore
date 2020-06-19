import java.util.Scanner;
class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        int n = scanner.nextInt();
        char[] toMove = new char[n];

        if (input.length() > n) {
            for (int i = 0; i < n; i++) {
                toMove[i] = input.charAt(i);
            }

            String ending = String.valueOf(toMove);
            String beginning = "";

            for (int i = n; i < input.length(); i++) {
                beginning += input.charAt(i);
            }
            System.out.println(beginning + ending);
        } else {
            System.out.println(input);
        }
    }
}
