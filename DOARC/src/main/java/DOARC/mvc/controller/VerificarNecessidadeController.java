package DOARC.mvc.controller;

import DOARC.mvc.model.VerificarNecessidade;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB; // Para obter a conexão
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VerificarNecessidadeController {

    @Autowired
    private VerificarNecessidade verificarNecessidadeModel;

    // --- MÉTODO AUXILIAR PARA OBTER A CONEXÃO (Controller como Gerente) ---
    private Conexao getConexao() {
        return SingletonDB.conectar();
    }
    // -----------------------------------------------------------------------

    // --- GET ALL ---
    public List<Map<String, Object>> getVerificacao(String filtro) {
        Conexao conexao = getConexao(); // INSTANCIA
        List<VerificarNecessidade> lista = verificarNecessidadeModel.consultar(filtro != null ? filtro : "", conexao); // PASSA CONEXAO

        List<Map<String, Object>> result = new ArrayList<>();

        for (VerificarNecessidade v : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", v.getVerId());
            json.put("data", v.getVerData());
            json.put("observacao", v.getVerObs());
            json.put("resultado", v.getVerResultado());
            json.put("vol_id", v.getVolId());
            json.put("dona_id", v.getDonaId()); // CORREÇÃO
            result.add(json);
        }
        return result;
    }

    // --- GET BY ID ---
    public Map<String, Object> getVerificacao(int id) {
        Conexao conexao = getConexao(); // INSTANCIA
        VerificarNecessidade v = verificarNecessidadeModel.consultar(id, conexao); // PASSA CONEXAO
        if (v == null) return Map.of("erro", "Registro de Verificação não encontrado");

        Map<String, Object> json = new HashMap<>();
        json.put("id", v.getVerId());
        json.put("data", v.getVerData());
        json.put("observacao", v.getVerObs());
        json.put("resultado", v.getVerResultado());
        json.put("vol_id", v.getVolId());
        json.put("dona_id", v.getDonaId()); // CORREÇÃO
        return json;
    }

    // --- ADD ---
    public Map<String, Object> addVerificacao(String data, String observacao, String resultado, int volId, int donaId) { // CORREÇÃO

        VerificarNecessidade nova = new VerificarNecessidade(data, observacao, resultado, volId, donaId); // CORREÇÃO

        Conexao conexao = getConexao(); // INSTANCIA
        VerificarNecessidade gravada = verificarNecessidadeModel.gravar(nova, conexao); // PASSA CONEXAO

        if (gravada == null) return Map.of("erro", "Erro ao cadastrar a Verificação de Necessidade");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravada.getVerId());
        json.put("data", gravada.getVerData());
        json.put("mensagem", "Registro de Verificação cadastrado com sucesso!");
        return json;
    }

    // --- UPDATE ---
    public Map<String, Object> updtVerificacao(int id, String data, String observacao, String resultado, int volId, int donaId) { // CORREÇÃO

        Conexao conexao = getConexao(); // INSTANCIA

        VerificarNecessidade existente = verificarNecessidadeModel.consultar(id, conexao); // PASSA CONEXAO
        if (existente == null) return Map.of("erro", "Registro de Verificação não encontrado");

        existente.setVerData(data);
        existente.setVerObs(observacao);
        existente.setVerResultado(resultado);
        existente.setVolId(volId);
        existente.setDonaId(donaId); // CORREÇÃO

        VerificarNecessidade atualizada = verificarNecessidadeModel.alterar(existente, conexao); // PASSA CONEXAO
        if (atualizada == null) return Map.of("erro", "Erro ao atualizar a Verificação de Necessidade");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizada.getVerId());
        json.put("data", atualizada.getVerData());
        json.put("mensagem", "Registro de Verificação atualizado com sucesso!");
        return json;
    }

    // --- DELETE ---
    public Map<String, Object> deletarVerificacao(int id) {
        Conexao conexao = getConexao(); // INSTANCIA
        VerificarNecessidade v = verificarNecessidadeModel.consultar(id, conexao); // PASSA CONEXAO

        if (v == null) return Map.of("erro", "Registro de Verificação não encontrado");

        boolean deletado = verificarNecessidadeModel.apagar(v, conexao); // PASSA CONEXAO
        return deletado ? Map.of("mensagem", "Registro de Verificação removido com sucesso") : Map.of("erro", "Erro ao remover o Registro de Verificação");
    }
}