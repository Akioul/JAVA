import java.util.Scanner;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DatabaseManager db = new DatabaseManager();

        while (true) {
            System.out.println(GREEN + "\n=== Xou Dou Qi - Menu Principal ===" + RESET);
            System.out.println("1. Nouvelle partie");
            System.out.println("2. Historique des parties");
            System.out.println("3. Quitter");
            System.out.print(YELLOW + "Choix : " + RESET);

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String player1 = authenticatePlayer(sc, db, 1);
                    if (player1 == null) continue;

                    String player2 = authenticatePlayer(sc, db, 2);
                    if (player2 == null) continue;

                    playGame(sc, db, player1, player2);
                    break;

                case 2:
                    db.showHistory();
                    break;

                case 3:
                    System.out.println(CYAN + "Au revoir !" + RESET);
                    sc.close();
                    return;

                default:
                    System.out.println(RED + "Choix invalide !" + RESET);
            }
        }
    }

    private static String authenticatePlayer(Scanner sc, DatabaseManager db, int playerNum) {
        System.out.printf(CYAN + "\n--- Joueur %d ---\n" + RESET, playerNum);
        System.out.print("Nom d'utilisateur : ");
        String username = sc.nextLine();
        System.out.print("Mot de passe : ");
        String password = sc.nextLine();

        if (!db.authenticate(username, password)) {
            System.out.println("Compte inexistant. Création...");
            db.insertUser(username, password);
            System.out.println("Compte créé avec succès !");
        }
        return username;
    }

    private static void playGame(Scanner sc, DatabaseManager db, String player1, String player2) {
        GameBoard board = new GameBoard();
        boolean isPlayer1Turn = true;
        String winner = null;

        System.out.println(GREEN + "\n=== Début de la partie ===" + RESET);
        System.out.printf("%s (Joueur 1) vs %s (Joueur 2)\n", player1, player2);

        while (winner == null) {
            board.displayBoard(true);
            String currentPlayer = isPlayer1Turn ? player1 : player2;
            System.out.printf("\n%s (%s), entrez votre mouvement (x1 y1 x2 y2) : ",
                    currentPlayer, isPlayer1Turn ? "P1" : "P2");

            try {
                int x1 = sc.nextInt();
                int y1 = sc.nextInt();
                int x2 = sc.nextInt();
                int y2 = sc.nextInt();
                sc.nextLine();

                if (!board.move(x1, y1, x2, y2, isPlayer1Turn)) {
                    System.out.println(RED + "❌ Mouvement invalide !" + RESET);
                } else {
                    if (board.checkVictory()) {
                        winner = currentPlayer;
                        System.out.printf("\n🏆 %s a gagné !\n", winner);
                        db.insertGame(player1, player2, winner);
                    } else {
                        isPlayer1Turn = !isPlayer1Turn;
                    }
                }
            } catch (Exception e) {
                System.out.println(RED + "Erreur de saisie. Format attendu : x1 y1 x2 y2" + RESET);
                sc.nextLine();
            }
        }

        System.out.print("\nVoir l'historique ? (o/n) : ");
        if (sc.nextLine().equalsIgnoreCase("o")) {
            db.showHistory();
        }
    }

    public static String colorize(String text, boolean isPlayer1) {
        return (isPlayer1 ? BLUE : RED) + text + RESET;
    }
}
