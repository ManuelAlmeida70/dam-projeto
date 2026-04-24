package pt.uan.anuncioSloc.webservice;

import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.repository.InMemoryPersistenceStore;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import pt.uan.anuncioSloc.service.InfraestruturaService;
import pt.uan.anuncioSloc.service.UtilizadorService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Web Service JAX-WS para operações do Servidor de Infraestrutura
 * 
 * Operações disponíveis:
 * - obterInfoInfraestrutura: Retorna info, estatísticas e estado
 * - criarLocal: Cria um novo local com coordenadas
 * - definirRestricao: Define restrições de entrega
 * - obterSaldo: Obtém saldo de um utilizador
 */
@WebService(
    serviceName = "InfraestruturaWebService",
    portName = "InfraestruturaPort",
    targetNamespace = "http://webservice.anuncioSloc.uan.pt/"
)
public class InfraestruturaWebService {

    private final InfraestruturaService infraService = new InfraestruturaService();
    private final UtilizadorService utilizadorService = new UtilizadorService();

    /**
     * Obtém informações detalhadas de uma infraestrutura
     * 
     * @param infraId ID da infraestrutura
     * @return String com informações: coordenadas, capacidade, locais, anúncios, entregas, conexões
     */
    @WebMethod(
        operationName = "obterInfoInfraestrutura",
        action = "urn:obterInfoInfraestrutura"
    )
    public String obterInfoInfraestrutura(
            @WebParam(name = "infraId", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Long infraId) {
        try {
            Infraestrutura infra = infraService.obterInfraestrutura(infraId);
            if (infra == null) {
                return "ERRO: Infraestrutura não encontrada";
            }

            StringBuilder info = new StringBuilder();
            info.append("=== INFRAESTRUTURA ===\n");
            info.append("ID: ").append(infra.getId()).append("\n");
            info.append("Nome: ").append(infra.getNome()).append("\n");
            info.append("URL Servidor: ").append(infra.getUrlServidor()).append("\n");
            info.append("Capacidade Máxima: ").append(infra.getCapacidadeMaxima()).append("\n");
            info.append("Prémio Entrega: ").append(infra.getPremioEntrega()).append(" pontos\n");
            info.append("\n=== ESTATÍSTICAS ===\n");
            info.append("Total de Anúncios: ").append(infra.getTotalAnuncios()).append("\n");
            info.append("Total de Entregas: ").append(infra.getTotalEntregas()).append("\n");
            info.append("\n=== ESTADO ATUAL ===\n");
            info.append("Utilizadores Conectados: ").append(infra.getUtilizadores().size()).append("\n");
            info.append("Conexões Disponíveis: ").append(infra.getConexoesDisponiveis()).append("\n");
            info.append("Locais Registados: ").append(infra.getLocais().size()).append("\n");
            info.append("Restrições: ").append(infraService.listarRestricoesDaInfraestrutura(infraId).size()).append("\n");

            return info.toString();
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }

    /**
     * Cria um novo local dentro de uma infraestrutura
     * 
     * @param infraId ID da infraestrutura
     * @param nomeLoca nome do local
     * @param latitude latitude (para GPS)
     * @param longitude longitude (para GPS)
     * @param raioMetros raio em metros (para GPS)
     * @return ID do local criado ou mensagem de erro
     */
    @WebMethod(
        operationName = "criarLocal",
        action = "urn:criarLocal"
    )
    public String criarLocal(
            @WebParam(name = "infraId", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Long infraId,
            @WebParam(name = "nomeLoca", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            String nomeLoca,
            @WebParam(name = "latitude", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Double latitude,
            @WebParam(name = "longitude", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Double longitude,
            @WebParam(name = "raioMetros", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Integer raioMetros) {
        try {
            Local local = infraService.criarLocal(infraId, nomeLoca);
            if (local == null) {
                return "ERRO: Não foi possível criar o local";
            }

            // Adiciona coordenada GPS se fornecida
            if (latitude != null && longitude != null && raioMetros != null) {
                local.adicionarCoordenadaGPS(latitude, longitude, raioMetros);
                new LocalRepository().update(local);
            }

            return "SUCESSO: Local criado com ID=" + local.getId();
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }

    /**
     * Define uma restrição de entrega para uma infraestrutura
     * 
     * @param infraId ID da infraestrutura
     * @param descricao Descrição da restrição
     * @param tipo Tipo de restrição (EXCLUIR_REDE, INCLUIR_APENAS, FILTRO_CONTEUDO)
     * @return ID da restrição criada ou mensagem de erro
     */
    @WebMethod(
        operationName = "definirRestricao",
        action = "urn:definirRestricao"
    )
    public String definirRestricao(
            @WebParam(name = "infraId", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Long infraId,
            @WebParam(name = "descricao", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            String descricao,
            @WebParam(name = "tipo", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            String tipo) {
        try {
            Restricao.TipoRestricao tipoEnum = Restricao.TipoRestricao.valueOf(tipo.toUpperCase());
            Restricao restricao = infraService.adicionarRestricao(infraId, descricao, tipoEnum);

            if (restricao == null) {
                return "ERRO: Não foi possível criar a restrição";
            }

            return "SUCESSO: Restrição criada com ID=" + restricao.getId();
        } catch (IllegalArgumentException e) {
            return "ERRO: Tipo de restrição inválido. Use: EXCLUIR_REDE, INCLUIR_APENAS, FILTRO_CONTEUDO";
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }

    /**
     * Obtém o saldo de um utilizador em uma infraestrutura
     * 
     * @param email Email do utilizador
     * @param infraId ID da infraestrutura
     * @return Saldo em pontos ou mensagem de erro
     */
    @WebMethod(
        operationName = "obterSaldo",
        action = "urn:obterSaldo"
    )
    public String obterSaldo(
            @WebParam(name = "email", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            String email,
            @WebParam(name = "infraId", targetNamespace = "http://webservice.anuncioSloc.uan.pt/")
            Long infraId) {
        try {
            if (!UtilizadorService.validarEmail(email)) {
                return "ERRO: Email inválido";
            }

            Integer saldo = utilizadorService.obterSaldo(email, infraId);
            if (saldo == null) {
                return "ERRO: Utilizador não encontrado nesta infraestrutura";
            }

            return "SUCESSO: Saldo=" + saldo + " pontos";
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }

    /**
     * Operação auxiliar - ping para verificar se o serviço está ativo
     * 
     * @return mensagem de diagnóstico
     */
    @WebMethod(
        operationName = "ping",
        action = "urn:ping"
    )
    public String ping() {
        try {
            long infraCount = infraService.listarTodas().size();
            return "SERVIDOR DE INFRAESTRUTURA ATIVO - Infraestruturas registadas: " + infraCount;
        } catch (Exception e) {
            return "SERVIDOR DE INFRAESTRUTURA - ERRO: " + e.getMessage();
        }
    }

    /**
     * Operação auxiliar - clear para limpar toda a base de dados
     * APENAS PARA TESTES!
     * 
     * @return mensagem de confirmação
     */
    @WebMethod(
        operationName = "clear",
        action = "urn:clear"
    )
    public String clear() {
        try {
            InMemoryPersistenceStore.clear();
            return "SUCESSO: Memória limpa";
        } catch (Exception e) {
            return "ERRO: " + e.getMessage();
        }
    }
}
