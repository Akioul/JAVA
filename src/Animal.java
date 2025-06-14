public class Animal {
    public enum Type {
        RAT, CHAT, LOUP, CHIEN, PANTHERE, TIGRE, LION, ELEPHANT
    }

    private Type type;
    private int force;
    private boolean isPlayer1;

    public Animal(Type type, boolean isPlayer1) {
        this.type = type;
        this.isPlayer1 = isPlayer1;
        this.force = getForceFromType(type);
    }

    private int getForceFromType(Type type) {
        return switch (type) {
            case ELEPHANT -> 8;
            case LION -> 7;
            case TIGRE -> 6;
            case PANTHERE -> 5;
            case CHIEN -> 4;
            case LOUP -> 3;
            case CHAT -> 2;
            case RAT -> 1;
        };
    }

    public Type getType() {
        return type;
    }

    public int getForce() {
        return force;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    @Override
    public String toString() {
        return (isPlayer1 ? "P1-" : "P2-") + type.name().charAt(0);
    }
}
