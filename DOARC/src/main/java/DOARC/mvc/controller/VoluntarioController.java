package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoluntarioController {

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private AcessoController auth;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> addVoluntario(Map<String, Object> dados) {

        Voluntario novo = new Voluntario(
                (String) dados.get("vol_nome"),
                (String) dados.get("vol_bairro"),
                (String) dados.get("vol_numero"),
                (String) dados.get("vol_rua"),
                (String) dados.get("vol_telefone"),
                (String) dados.get("vol_cidade"),
                (String) dados.get("vol_cep"),
                (String) dados.get("vol_uf"),
                (String) dados.get("vol_email"),
                (String) dados.get("vol_cpf"),
                (String) dados.get("vol_datanasc"),
                (String) dados.get("vol_sexo")
        );

        Voluntario gravado = voluntarioModel.gravar(novo, getConexao());

        if (gravado == null)
            return Map.of("erro", "Erro ao cadastrar voluntário");


        String login = gravado.getVol_email(); // ou CPF ou outro campo
        String senhaPadrao = "123456"; // você escolhe
        String nivelAcesso = "USER";

        auth.registrarUsuario(
                gravado.getVol_id(),
                login,
                senhaPadrao,
                nivelAcesso
        );

        // Retornar dado do voluntário + aviso
        Map<String, Object> resp = mapVoluntario(gravado);
        resp.put("loginCriado", login);
        resp.put("senhaPadrao", senhaPadrao);

        return resp;
    }


    // ✅ UPDATE via MAP
    public Map<String, Object> updtVoluntario(int id, Map<String, Object> dados) {

        Conexao conexao = getConexao();
        Voluntario v = voluntarioModel.consultar(id, conexao);

        if (v == null)
            return Map.of("erro", "Voluntário não encontrado");

        v.setVol_nome((String) dados.get("vol_nome"));
        v.setVol_bairro((String) dados.get("vol_bairro"));
        v.setVol_numero((String) dados.get("vol_numero"));
        v.setVol_rua((String) dados.get("vol_rua"));
        v.setVol_telefone((String) dados.get("vol_telefone"));
        v.setVol_cidade((String) dados.get("vol_cidade"));
        v.setVol_cep((String) dados.get("vol_cep"));
        v.setVol_uf((String) dados.get("vol_uf"));
        v.setVol_email((String) dados.get("vol_email"));
        v.setVol_cpf((String) dados.get("vol_cpf"));
        v.setVol_datanasc((String) dados.get("vol_datanasc"));
        v.setVol_sexo((String) dados.get("vol_sexo"));

        Voluntario atualizado = voluntarioModel.alterar(v, conexao);

        return atualizado == null ?
                Map.of("erro", "Erro ao atualizar") :
                mapVoluntario(atualizado);
    }

    // ✅ GET by ID correto
    public Map<String, Object> getVoluntarioById(int id) {
        Voluntario v = voluntarioModel.consultar(id, getConexao());
        return v == null ? Map.of("erro", "Voluntário não encontrado") : mapVoluntario(v);
    }

    // ✅ listagem mantida
    public List<Map<String, Object>> getVoluntarios() {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Voluntario v : voluntarioModel.consultar("", getConexao())) {
            out.add(mapVoluntario(v));
        }
        return out;
    }
    // --- DELETE ---
    public Map<String, Object> deletarVoluntario(int id) {
        Conexao conexao = getConexao();

        Voluntario v = voluntarioModel.consultar(id, conexao);

        if (v == null) return Map.of("erro", "Voluntário não encontrado");

        boolean deletado = voluntarioModel.apagar(v, conexao);

        return deletado
                ? Map.of("mensagem", "Voluntário removido com sucesso")
                : Map.of("erro", "Erro ao remover o voluntário");
    }


    // --- MAPEAR RESPOSTA PADRÃO ---
    private Map<String, Object> mapVoluntario(Voluntario v) {
        Map<String, Object> json = new HashMap<>();

        json.put("id", v.getVol_id());
        json.put("nome", v.getVol_nome());
        json.put("bairro", v.getVol_bairro());
        json.put("numero", v.getVol_numero());
        json.put("rua", v.getVol_rua());
        json.put("telefone", v.getVol_telefone());
        json.put("cidade", v.getVol_cidade());
        json.put("cep", v.getVol_cep());
        json.put("uf", v.getVol_uf());
        json.put("email", v.getVol_email());
        json.put("cpf", v.getVol_cpf());
        json.put("data_nasc", v.getVol_datanasc());
        json.put("sexo", v.getVol_sexo());

        return json;
    }



}
