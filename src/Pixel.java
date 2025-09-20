class Pixel {

    private int x;
    private int y;

    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}

// Interface para estruturas de dados
interface EstruturaPixel {
    void adicionar(Pixel pixel);
    Pixel remover();
    boolean estaVazia();
}