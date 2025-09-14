class GerenciarCores {
    public static boolean coresSaoIguais(int cor1, int cor2) {
        return cor1 == cor2;
    }

    public static int obterCorBranca() {
        return 0xFFFFFFFF; // Branco em formato ARGB
    }

    public static int obterCorVermelha() {
        return 0xFFFF0000; // Vermelho em formato ARGB
    }

    public static int obterCorAzul() {
        return 0xFF0000FF; // Azul em formato ARGB
    }

    public static int obterCorVerde() {
        return 0xFF00FF00; // Verde em formato ARGB
    }
}