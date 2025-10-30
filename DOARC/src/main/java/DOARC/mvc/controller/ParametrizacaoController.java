package DOARC.mvc.controller;

import DOARC.mvc.model.Parametrizacao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParametrizacaoController {
    @Autowired
    private Parametrizacao paramModel;

    public Map<String,Object> getParam(int id) {
        SingletonDB conexao = SingletonDB.getInstancia();
        if(conexao.conectar()){
            try {
                Parametrizacao param = paramModel.get(id);
                if (param != null){
                    Map<String,Object> json = new HashMap<>();
                    json.put("id", param.getId());
                    json.put("razaoSocial", param.getRazaoSocial());
                    json.put("nomeFantasia", param.getNomeFantasia());
                    json.put("cnpj", param.getCnpj());
                    json.put("rua", param.getRua());
                    json.put("numero", param.getNumero());
                    json.put("bairro", param.getBairro());
                    json.put("cidade", param.getCidade());
                    // REMOVIDO: json.put("estado", param.getEstado());
                    json.put("uf", param.getUf());
                    json.put("cep", param.getCep());
                    json.put("telefone", param.getTelefone());
                    json.put("email", param.getEmail());
                    json.put("site", param.getSite());
                    return json;
                }
                return null;
            } finally {
                conexao.Desconectar();
            }
        }
        return null;
    }

    public Map<String,Object> addParam(String razaoSocial, String nomeFantasia, String cnpj, String rua,
                                       String numero, String bairro, String cidade, String uf,
                                       String cep, String telefone, String email, String site,
                                       MultipartFile file) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String,Object> json = new HashMap<>();

        if(conexao.conectar()){
            // regra de negocio - verificar se já existe empresa cadastrada
            if (isEmpty(conexao)) {
                try{
                    // Upload do arquivo
                    File uploadFolder = new File("DOARC/src/main/resources/templates/img/");
                    if (!uploadFolder.exists())
                        uploadFolder.mkdirs();
                    if (file != null && !file.isEmpty()) {
                        file.transferTo(new File(uploadFolder.getAbsoluteFile() + File.separator + "logo.png"));
                    }

                    // Formatação dos dados
                    cnpj = cnpj.replaceAll("[^\\d]", "");
                    cep = cep.replaceAll("[^\\d]", "");
                    telefone = telefone.replaceAll("[^\\d]", "");

                    // Cria objeto Parametrizacao SEM estado
                    Parametrizacao param = new Parametrizacao(
                            cnpj, razaoSocial, nomeFantasia, rua, cidade,
                            bairro, Integer.parseInt(numero), uf, cep, email, site, telefone
                    );

                    Parametrizacao resultado = paramModel.gravar(param);
                    if (resultado != null){
                        json.put("id", resultado.getId());
                        json.put("razaoSocial", resultado.getRazaoSocial());
                        json.put("nomeFantasia", resultado.getNomeFantasia());
                        json.put("cnpj", resultado.getCnpj());
                        json.put("rua", resultado.getRua());
                        json.put("numero", resultado.getNumero());
                        json.put("bairro", resultado.getBairro());
                        json.put("cidade", resultado.getCidade());
                        // REMOVIDO: json.put("estado", resultado.getEstado());
                        json.put("uf", resultado.getUf());
                        json.put("cep", resultado.getCep());
                        json.put("telefone", resultado.getTelefone());
                        json.put("email", resultado.getEmail());
                        json.put("site", resultado.getSite());
                    }
                    else{
                        json.put("erro", "Erro ao cadastrar a Empresa");
                    }
                } catch (Exception e) {
                    json.put("erro", "Erro ao armazenar o arquivo. " + e.getMessage());
                }
            } else {
                json.put("erro", "Já existe uma Empresa cadastrada");
            }
            conexao.Desconectar();
        }
        else{
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String,Object> updtParam(int id, String razaoSocial, String nomeFantasia, String cnpj,
                                        String rua, String numero, String bairro, String cidade,
                                        String uf, String cep, String telefone, String email,
                                        String site, MultipartFile file) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String,Object> json = new HashMap<>();

        if(conexao.conectar()){
            try{
                // Upload do arquivo
                File uploadFolder = new File("casoftWebSpring/src/main/resources/templates/img/");
                if (!uploadFolder.exists())
                    uploadFolder.mkdirs();
                if (file != null && !file.isEmpty()) {
                    file.transferTo(new File(uploadFolder.getAbsoluteFile() + File.separator + "logo.png"));
                }

                // Formatação dos dados
                cnpj = cnpj.replaceAll("[^\\d]", "");
                cep = cep.replaceAll("[^\\d]", "");
                telefone = telefone.replaceAll("[^\\d]", "");

                // Cria objeto Parametrizacao SEM estado
                Parametrizacao param = new Parametrizacao(
                        id, cnpj, razaoSocial, nomeFantasia, rua, cidade,
                        bairro, Integer.parseInt(numero), uf, cep, email, site, telefone
                );

                Parametrizacao resultado = paramModel.alterar(param);
                if (resultado != null){
                    json.put("id", resultado.getId());
                    json.put("razaoSocial", resultado.getRazaoSocial());
                    json.put("nomeFantasia", resultado.getNomeFantasia());
                    json.put("cnpj", resultado.getCnpj());
                    json.put("rua", resultado.getRua());
                    json.put("numero", resultado.getNumero());
                    json.put("bairro", resultado.getBairro());
                    json.put("cidade", resultado.getCidade());
                    // REMOVIDO: json.put("estado", resultado.getEstado());
                    json.put("uf", resultado.getUf());
                    json.put("cep", resultado.getCep());
                    json.put("telefone", resultado.getTelefone());
                    json.put("email", resultado.getEmail());
                    json.put("site", resultado.getSite());
                }
                else {
                    json.put("erro", "Erro ao alterar a Empresa");
                }
            } catch (Exception e) {
                json.put("erro", "Erro ao armazenar o arquivo. " + e.getMessage());
            }
            conexao.Desconectar();
        }
        else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    // Método para verificar se já existe empresa cadastrada
    private boolean isEmpty(SingletonDB conexao) {
        if (conexao.conectar()) {
            try {
                List<Parametrizacao> parametrizacoes = paramModel.get(null);
                return parametrizacoes == null || parametrizacoes.isEmpty();
            } finally {
                conexao.Desconectar();
            }
        }
        return true;
    }
}