class NoPilha {
    Pixel pixel;
    NoPilha proximo;

    NoPilha(Pixel pixel) {
        this.pixel = pixel;
        this.proximo = null;
    }
}

class PilhaPixel implements EstruturaPixel {
    private NoPilha topo;

    public PilhaPixel() {
        this.topo = null;
    }

    @Override
    public void adicionar(Pixel pixel) {
        NoPilha novoNo = new NoPilha(pixel);
        novoNo.proximo = topo;
        topo = novoNo;
    }

    @Override
    public Pixel remover() {
        if (estaVazia()) {
            return null;
        }

        Pixel pixel = topo.pixel;
        topo = topo.proximo;
        return pixel;
    }

    @Override
    public boolean estaVazia() {
        return topo == null;
    }
}