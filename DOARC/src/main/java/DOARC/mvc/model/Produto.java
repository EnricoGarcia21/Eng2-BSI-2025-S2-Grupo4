    package DOARC.mvc.model;

    import DOARC.mvc.dao.ProdutoDAO;
    import DOARC.mvc.util.Conexao;
    import java.util.List;

    public class Produto {
        private int prodId;
        private String prodNome;
        private String prodDescricao;
        private String prodInformacoesAdicionais;
        private int prodQuant;
        private int categoriaCatId;
        private String categoriaNome;

        public Produto() {}

        public Produto(String prodNome, String prodDescricao, String prodInformacoesAdicionais,
                       int prodQuant, int categoriaCatId) {
            this.prodNome = prodNome;
            this.prodDescricao = prodDescricao;
            this.prodInformacoesAdicionais = prodInformacoesAdicionais;
            this.prodQuant = prodQuant;
            this.categoriaCatId = categoriaCatId;
        }

        // ===== DAO integrado =====
        private static final ProdutoDAO dao = new ProdutoDAO();

        public Produto gravar(Conexao conexao) { return dao.gravar(this, conexao); }
        public Produto alterar(Conexao conexao) { return dao.alterar(this, conexao); }
        public boolean apagar(Conexao conexao) { return dao.apagar(this, conexao); }
        public static Produto get(int id, Conexao conexao) { return dao.get(id, conexao); }
        public static List<Produto> get(String filtro, Conexao conexao) { return dao.get(filtro, conexao); }

        // ===== Getters e Setters =====
        public int getProdId() { return prodId; }
        public void setProdId(int prodId) { this.prodId = prodId; }

        public String getProdNome() { return prodNome; }
        public void setProdNome(String prodNome) { this.prodNome = prodNome; }

        public String getProdDescricao() { return prodDescricao; }
        public void setProdDescricao(String prodDescricao) { this.prodDescricao = prodDescricao; }

        public String getProdInformacoesAdicionais() { return prodInformacoesAdicionais; }
        public void setProdInformacoesAdicionais(String prodInformacoesAdicionais) { this.prodInformacoesAdicionais = prodInformacoesAdicionais; }

        public int getProdQuant() { return prodQuant; }
        public void setProdQuant(int prodQuant) { this.prodQuant = prodQuant; }

        public int getCategoriaCatId() { return categoriaCatId; }
        public void setCategoriaCatId(int categoriaCatId) { this.categoriaCatId = categoriaCatId; }

        public String getCategoriaNome() { return categoriaNome; }
        public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
    }
