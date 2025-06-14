public class GameBoard {
    private Animal[][] board = new Animal[9][7];
    private final int[][] sanctuaires = {{0, 3}, {8, 3}};
    private final int[][][] rivières = {
            {{3, 1}, {3, 2}, {4, 1}, {4, 2}, {5, 1}, {5, 2}},
            {{3, 4}, {3, 5}, {4, 4}, {4, 5}, {5, 4}, {5, 5}}
    };
    private final int[][][] pièges = {
            {{0, 2}, {0, 4}, {1, 3}},
            {{8, 2}, {8, 4}, {7, 3}}
    };

    public GameBoard() {
        initBoard();
    }

    public void displayBoard(boolean withColors) {
        System.out.println("\n   | 0    | 1    | 2    | 3    | 4    | 5    | 6    |");
        System.out.println("---+------+------+------+------+------+------+------+");

        for (int i = 0; i < 9; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < 7; j++) {
                String symbol;
                String color = Main.RESET;

                Animal a = board[i][j];
                if (a != null) {
                    symbol = "[" + a.toString().charAt(3) + "]";
                    color = a.isPlayer1() ? Main.BLUE : Main.RED;
                } else if (isSanctuaire(i, j)) {
                    symbol = "SSS";
                    color = Main.GREEN;
                } else if (isPiège(i, j)) {
                    symbol = "###";
                    color = Main.YELLOW;
                } else if (isRivière(i, j)) {
                    symbol = "~~~";
                    color = Main.CYAN;
                } else {
                    symbol = " .  ";
                }

                // Ajustement de l'espacement pour l'alignement
                if (symbol.length() == 3) symbol = " " + symbol + " ";
                if (symbol.length() == 4) symbol = " " + symbol;

                System.out.print(color + symbol + Main.RESET + " | ");
            }
            System.out.println();
            System.out.println("---+------+------+------+------+------+------+------+");
        }

        System.out.println("\nLégende :");
        System.out.println(Main.BLUE + "[X]" + Main.RESET + " = Pièces P1, " +
                Main.RED + "[X]" + Main.RESET + " = Pièces P2 | " +
                Main.GREEN + "SSS" + Main.RESET + " = Sanctuaire | " +
                Main.YELLOW + "###" + Main.RESET + " = Piège | " +
                Main.CYAN + "~~~" + Main.RESET + " = Rivière\n");
    }

    private void initBoard() {
        // Joueur 1 (en bas)
        board[2][0] = new Animal(Animal.Type.RAT, true);
        board[1][1] = new Animal(Animal.Type.CHAT, true);
        board[1][5] = new Animal(Animal.Type.LOUP, true);
        board[2][2] = new Animal(Animal.Type.CHIEN, true);
        board[2][4] = new Animal(Animal.Type.PANTHERE, true);
        board[0][6] = new Animal(Animal.Type.LION, true);
        board[0][0] = new Animal(Animal.Type.TIGRE, true);
        board[0][3] = new Animal(Animal.Type.ELEPHANT, true);

        // Joueur 2 (en haut)
        board[6][6] = new Animal(Animal.Type.RAT, false);
        board[7][5] = new Animal(Animal.Type.CHAT, false);
        board[7][1] = new Animal(Animal.Type.LOUP, false);
        board[6][4] = new Animal(Animal.Type.CHIEN, false);
        board[6][2] = new Animal(Animal.Type.PANTHERE, false);
        board[8][0] = new Animal(Animal.Type.LION, false);
        board[8][6] = new Animal(Animal.Type.TIGRE, false);
        board[8][3] = new Animal(Animal.Type.ELEPHANT, false);
    }

    public void displayBoard() {
        displayBoard(true);
    }

    public boolean move(int x1, int y1, int x2, int y2, boolean isPlayer1Turn) {
        if (!inBounds(x1, y1) || !inBounds(x2, y2)) return false;
        Animal a = board[x1][y1];
        if (a == null || a.isPlayer1() != isPlayer1Turn) return false;

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Mouvements autorisés (orthogonal ou saut lion/tigre)
        boolean sautOK = false;
        if (Math.abs(dx) + Math.abs(dy) == 1) {
            // Normal
        } else if ((a.getType() == Animal.Type.LION || a.getType() == Animal.Type.TIGRE) && (dx == 0 || dy == 0)) {
            sautOK = canJumpOverRiver(x1, y1, x2, y2);
            if (!sautOK) return false;
        } else {
            return false;
        }

        // Destination
        Animal cible = board[x2][y2];


        if (isSanctuaire(x2, y2) && isOwnSanctuaire(x2, y2, a.isPlayer1())) return false;


        if (cible != null) {
            if (cible.isPlayer1() == a.isPlayer1()) return false;

            boolean peutCapturer = a.getForce() >= cible.getForce();

            if (a.getType() == Animal.Type.RAT && cible.getType() == Animal.Type.ELEPHANT) {
                peutCapturer = true;
            }

            // Piège : la cible dans un piège devient vulnérable
            if (isPiège(x2, y2)) {
                peutCapturer = true;
            }

            // Rat ne peut capturer en sortant de la rivière
            if (a.getType() == Animal.Type.RAT && isRivière(x1, y1) && !isRivière(x2, y2)) {
                return false;
            }

            if (!peutCapturer) return false;
        }

        // Mouvements du rat uniquement autorisés dans la rivière
        if (isRivière(x2, y2) && a.getType() != Animal.Type.RAT) return false;

        // Effectuer le mouvement
        board[x2][y2] = a;
        board[x1][y1] = null;

        return true;
    }

    public boolean checkVictory() {
        Animal a1 = board[8][3]; // sanctuaire joueur 2
        Animal a2 = board[0][3]; // sanctuaire joueur 1
        return (a1 != null && a1.isPlayer1()) || (a2 != null && !a2.isPlayer1());
    }

    private boolean isSanctuaire(int x, int y) {
        for (int[] s : sanctuaires)
            if (s[0] == x && s[1] == y) return true;
        return false;
    }

    private boolean isOwnSanctuaire(int x, int y, boolean isPlayer1) {
        return (isPlayer1 && x == 0 && y == 3) || (!isPlayer1 && x == 8 && y == 3);
    }

    private boolean isPiège(int x, int y) {
        for (int[] p : pièges[0])
            if (p[0] == x && p[1] == y) return true;
        for (int[] p : pièges[1])
            if (p[0] == x && p[1] == y) return true;
        return false;
    }

    private boolean isRivière(int x, int y) {
        for (int[] r : rivières[0])
            if (r[0] == x && r[1] == y) return true;
        for (int[] r : rivières[1])
            if (r[0] == x && r[1] == y) return true;
        return false;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 7;
    }

    private boolean canJumpOverRiver(int x1, int y1, int x2, int y2) {
        int dx = Integer.signum(x2 - x1);
        int dy = Integer.signum(y2 - y1);
        int x = x1 + dx;
        int y = y1 + dy;

        while (x != x2 || y != y2) {
            // Vérifie si un Rat ennemi est dans la rivière
            if (isRivière(x, y) && board[x][y] != null &&
                    board[x][y].getType() == Animal.Type.RAT &&
                    board[x][y].isPlayer1() != board[x1][y1].isPlayer1()) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }
}