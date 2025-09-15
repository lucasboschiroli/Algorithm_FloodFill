import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ProcessadorImagem {
    private String diretorioSaida;

    public ProcessadorImagem(String diretorioSaida) {
        this.diretorioSaida = diretorioSaida;
        criarDiretorio();
    }

    private void criarDiretorio() {
        File dir = new File(diretorioSaida);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Novo método para carregar imagem de uma pasta específica
    public int[][] carregarImagemDaPasta(String pasta, String nomeArquivo) {
        // Extensões comuns para imagens em preto e branco
        String[] extensoes = {".png", ".jpg", ".jpeg", ".bmp", ".gif", ".tiff"};

        for (String extensao : extensoes) {
            String caminhoCompleto = pasta + File.separator + nomeArquivo + extensao;
            File arquivo = new File(caminhoCompleto);

            if (arquivo.exists()) {
                System.out.println("Tentando carregar: " + caminhoCompleto);
                return carregarImagem(caminhoCompleto);
            }
        }

        System.err.println("Arquivo não encontrado na pasta '" + pasta + "' com nome '" + nomeArquivo + "'");
        System.err.println("Extensões testadas: .png, .jpg, .jpeg, .bmp, .gif, .tiff");
        System.out.println("Criando matriz de exemplo...");
        return criarMatrizExemplo();
    }

    public int[][] carregarImagem(String caminhoArquivo) {
        try {
            BufferedImage imagem = ImageIO.read(new File(caminhoArquivo));
            int largura = imagem.getWidth();
            int altura = imagem.getHeight();
            int[][] matriz = new int[altura][largura];

            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    int rgb = imagem.getRGB(x, y);
                    // Para imagens P&B, converte para escala de cinza se necessário
                    matriz[y][x] = converterParaPretoBranco(rgb);
                }
            }

            System.out.println("Imagem carregada com sucesso: " + largura + "x" + altura + " pixels");
            return matriz;

        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
            return criarMatrizExemplo();
        }
    }

    // Método para garantir que a imagem seja tratada como P&B
    private int converterParaPretoBranco(int rgb) {
        // Extrai os componentes RGB
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // Calcula a luminância (escala de cinza)
        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

        // Converte para preto ou branco baseado em um threshold
        if (gray > 127) {
            return 0xFFFFFFFF; // Branco puro
        } else {
            return 0xFF000000; // Preto puro
        }
    }

    // Método para encontrar um pixel branco em uma área específica (centro da imagem)
    public int[] encontrarPixelBrancoNoCentro(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;

        // Começa do centro da imagem e expande em espiral
        int centroY = altura / 2;
        int centroX = largura / 2;

        System.out.println("Procurando pixel branco no centro da imagem...");
        System.out.println("Centro: (" + centroX + ", " + centroY + ")");

        // Verifica o centro primeiro
        if (matriz[centroY][centroX] == 0xFFFFFFFF) {
            System.out.println("Pixel branco encontrado no centro!");
            return new int[]{centroX, centroY};
        }

        // Expande em círculos concêntricos do centro
        for (int raio = 1; raio < Math.max(largura, altura) / 2; raio++) {
            for (int dy = -raio; dy <= raio; dy++) {
                for (int dx = -raio; dx <= raio; dx++) {
                    // Verifica se está na borda do círculo atual
                    if (Math.abs(dx) == raio || Math.abs(dy) == raio) {
                        int y = centroY + dy;
                        int x = centroX + dx;

                        // Verifica limites
                        if (y >= 0 && y < altura && x >= 0 && x < largura) {
                            if (matriz[y][x] == 0xFFFFFFFF) {
                                System.out.println("Pixel branco encontrado em: (" + x + ", " + y + ")");
                                return new int[]{x, y};
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Nenhum pixel branco encontrado, usando centro da imagem");
        return new int[]{centroX, centroY};
    }

    // Método alternativo para encontrar pixels brancos em áreas fechadas
    public int[] encontrarPixelBrancoEmAreaFechada(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;

        System.out.println("Procurando pixel branco em área fechada...");

        // Procura por pixels brancos que estão cercados por pixels pretos
        for (int y = 1; y < altura - 1; y++) {
            for (int x = 1; x < largura - 1; x++) {
                if (matriz[y][x] == 0xFFFFFFFF) {
                    // Verifica se está em uma área relativamente fechada
                    // (tem pixels pretos nas proximidades)
                    boolean temPretoProximo = false;
                    for (int dy = -5; dy <= 5 && !temPretoProximo; dy++) {
                        for (int dx = -5; dx <= 5 && !temPretoProximo; dx++) {
                            int ny = y + dy;
                            int nx = x + dx;
                            
                            if (ny >= 0 && ny < altura && nx >= 0 && nx < largura) {
                                if (matriz[ny][nx] == 0xFF000000) {
                                    temPretoProximo = true;
                                }
                            }
                        }
                    }

                    if (temPretoProximo) {
                        System.out.println("Pixel branco em área fechada encontrado: (" + x + ", " + y + ")");
                        return new int[]{x, y};
                    }
                }
            }
        }

        // Se não encontrar, usa o método do centro
        return encontrarPixelBrancoNoCentro(matriz);
    }

    private int[][] criarMatrizExemplo() {
        System.out.println("Criando matriz de exemplo 10x10 em preto e branco...");
        int[][] matriz = new int[10][10];

        // Preenche com branco
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matriz[i][j] = 0xFFFFFFFF; // Branco
            }
        }

        // Adiciona uma linha diagonal preta
        for (int i = 0; i < 10; i++) {
            if (i < 10) {
                matriz[i][i] = 0xFF000000; // Preto
            }
        }

        // Adiciona algumas bordas pretas
        for (int i = 0; i < 10; i++) {
            matriz[0][i] = 0xFF000000; // Borda superior
            matriz[9][i] = 0xFF000000; // Borda inferior
            matriz[i][0] = 0xFF000000; // Borda esquerda
            matriz[i][9] = 0xFF000000; // Borda direita
        }

        return matriz;
    }

    public void salvarImagemAnimacao(int[][] matriz, int frame, String tipo) {
        try {
            BufferedImage imagem = criarBufferedImage(matriz);
            String nomeArquivo = String.format("%s/animacao_%s_frame_%04d.png",
                    diretorioSaida, tipo.toLowerCase(), frame);
            ImageIO.write(imagem, "PNG", new File(nomeArquivo));
        } catch (IOException e) {
            System.err.println("Erro ao salvar frame de animação: " + e.getMessage());
        }
    }

    public void salvarImagemFinal(int[][] matriz, String tipo) {
        try {
            BufferedImage imagem = criarBufferedImage(matriz);
            String nomeArquivo = String.format("%s/resultado_final_%s.png",
                    diretorioSaida, tipo.toLowerCase());
            ImageIO.write(imagem, "PNG", new File(nomeArquivo));
            System.out.println("Imagem final salva: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar imagem final: " + e.getMessage());
        }
    }

    private BufferedImage criarBufferedImage(int[][] matriz) {
        int altura = matriz.length;
        int largura = matriz[0].length;
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                imagem.setRGB(x, y, matriz[y][x]);
            }
        }

        return imagem;
    }
}