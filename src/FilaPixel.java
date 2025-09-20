class NoFila {
    Pixel pixel;
    NoFila proximo;

    NoFila(Pixel pixel) {
        this.pixel = pixel;
        this.proximo = null;
    }
}

class FilaPixel implements EstruturaPixel {

    private NoFila inicio;
    private NoFila fim;

    public FilaPixel() {
        this.inicio = null;
        this.fim = null;
    }

    @Override
    public void adicionar(Pixel pixel) {
        NoFila novoNo = new NoFila(pixel);

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