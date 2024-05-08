class ColoredChar {
    char character;
    String color;

    public ColoredChar(char character, String color) {
        this.character = character;
        this.color = color;
    }

    @Override
    public String toString() {
        return color + character + "\033[0m";  // Reset to default color after each character
    }
}
