package DOARC.mvc.view;

import DOARC.mvc.dao.DoacaoDAO;
import DOARC.mvc.dao.DoadosProdutoDAO;
import DOARC.mvc.dao.ProdutoDAO;
import DOARC.mvc.model.Doacao;
import DOARC.mvc.model.DoadosProduto;
import DOARC.mvc.model.Produto;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.Mensagem;
import DOARC.mvc.util.SingletonDB;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin
@RestController
@RequestMapping("/apis/doacao")
public class DoacaoView {

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private final DoadosProdutoDAO doadosProdutoDAO = new DoadosProdutoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Object> getDoacoesDisponiveis(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) int pessoaId
    ) {
        Conexao conn = getConexaoDoSingleton();

        List<Doacao> lista = doacaoDAO.getDoacoesDisponiveis(conn);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Doacao d : lista) {
            Map<String, Object> json = new HashMap<>();

            json.put("id", d.getDoacaoId());
            json.put("obs", d.getObsDoacao());
            json.put("data", d.getDataDoacao().toString());


            json.put("produtos", getDetalhesProdutos(d.getDoaId(), conn));

            result.add(json);
        }

        return (result != null && !result.isEmpty())
                ? ResponseEntity.ok(result)
                : ResponseEntity.badRequest().body(new Mensagem("Nenhuma doação disponível encontrada."));
    }

    private List<Map<String, Object>> getDetalhesProdutos(int doaId, Conexao conn) {
        List<DoadosProduto> produtosRaw = doadosProdutoDAO.getProdutosPorDoacaoId(doaId, conn);
        List<Map<String, Object>> produtosDetalhe = new ArrayList<>();

        for(DoadosProduto dp : produtosRaw) {
            Produto p = produtoDAO.get(dp.getProdId(), conn);
            if (p != null) {
                Map<String, Object> prodMap = new HashMap<>();
                prodMap.put("prod", p.getProdNome());
                prodMap.put("qtde", dp.getDpQtde());
                prodMap.put("obs", p.getProdDescricao());
                produtosDetalhe.add(prodMap);
            }
        }
        return produtosDetalhe;
    }
}