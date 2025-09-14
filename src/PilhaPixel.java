class PilhaPixel implements EstruturaPixel {
    private static class No {
        Pixel pixel;
        No proximo;

        No(Pixel pixel) {
            this.pixel = pixel;
            this.proximo = null;
        }
    }

    private No topo;

    public PilhaPixel() {
        this.topo = null;
    }

    @Override
    public void adicionar(Pixel pixel) {
        No novoNo = new No(pixel);
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