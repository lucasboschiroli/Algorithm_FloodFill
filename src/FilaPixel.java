class FilaPixel implements EstruturaPixel {
    private static class No {
        Pixel pixel;
        No proximo;

        No(Pixel pixel) {
            this.pixel = pixel;
            this.proximo = null;
        }
    }

    private No inicio;
    private No fim;

    public FilaPixel() {
        this.inicio = null;
        this.fim = null;
    }

    @Override
    public void adicionar(Pixel pixel) {
        No novoNo = new No(pixel);

        if (estaVazia()) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.proximo = novoNo;
            fim = novoNo;
        }
    }

    @Override
    public Pixel remover() {
        if (estaVazia()) {
            return null;
        }

        Pixel pixel = inicio.pixel;
        inicio = inicio.proximo;

        if (inicio == null) {
            fim = null;
        }

        return pixel;
    }

    @Override
    public boolean estaVazia() {
        return inicio == null;
    }
}