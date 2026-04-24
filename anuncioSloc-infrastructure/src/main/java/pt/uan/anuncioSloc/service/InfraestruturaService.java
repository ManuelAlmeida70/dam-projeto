package pt.uan.anuncioSloc.service;

import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.repository.InfraestruturaRepository;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import pt.uan.anuncioSloc.persistence.repository.RestricaoRepository;
import java.util.List;

/**
 * Serviço de lógica de negócio para Infraestrutura
 */
public class InfraestruturaService {

    private final InfraestruturaRepository infraRepo = new InfraestruturaRepository();
    private final LocalRepository localRepo = new LocalRepository();
    private final RestricaoRepository restricaoRepo = new RestricaoRepository();

    /**
     * Cria uma nova infraestrutura
     */
    public Infraestrutura criarInfraestrutura(String nome, String urlServidor, Integer capacidade, Integer premio) {
        // Validações
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome da infraestrutura não pode ser vazio");
        }
        if (urlServidor == null || urlServidor.isEmpty()) {
            throw new IllegalArgumentException("URL do servidor não pode ser vazia");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que 0");
        }
        if (premio < 0) {
            throw new IllegalArgumentException("Prémio não pode ser negativo");
        }

        // Verifica se já existe infraestrutura com este nome
        if (infraRepo.findByNome(nome) != null) {
            throw new IllegalArgumentException("Infraestrutura com nome '" + nome + "' já existe");
        }

        Infraestrutura infra = new Infraestrutura(nome, urlServidor, capacidade, premio);
        return infraRepo.save(infra);
    }

    /**
     * Obtém uma infraestrutura por ID
     */
    public Infraestrutura obterInfraestrutura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da infraestrutura inválido");
        }
        return infraRepo.findById(id);
    }

    /**
     * Obtém uma infraestrutura por nome
     */
    public Infraestrutura obterInfraestruturaByNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Nome da infraestrutura não pode ser vazio");
        }
        return infraRepo.findByNome(nome);
    }

    /**
     * Lista todas as infraestruturas
     */
    public List<Infraestrutura> listarTodas() {
        return infraRepo.findAll();
    }

    /**
     * Lista infraestruturas ativas
     */
    public List<Infraestrutura> listarAtivas() {
        return infraRepo.findAllAtivas();
    }

    /**
     * Atualiza uma infraestrutura
     */
    public Infraestrutura atualizarInfraestrutura(Infraestrutura infraestrutura) {
        if (infraestrutura == null || infraestrutura.getId() == null) {
            throw new IllegalArgumentException("Infraestrutura ou ID inválido");
        }
        return infraRepo.update(infraestrutura);
    }

    /**
     * Deleta uma infraestrutura
     */
    public void deletarInfraestrutura(Long id) {
        Infraestrutura infra = obterInfraestrutura(id);
        if (infra != null) {
            infraRepo.delete(infra);
        }
    }

    /**
     * Obtém informações detalhadas da infraestrutura
     */
    public String obterInfoDetalhada(Long infraId) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra == null) {
            return null;
        }

        long totalLocais = localRepo.countByInfraestrutura(infraId);
        StringBuilder info = new StringBuilder();
        info.append("Infraestrutura: ").append(infra.getNome()).append("\n");
        info.append("Capacidade: ").append(infra.getCapacidadeMaxima()).append("\n");
        info.append("Prémio por entrega: ").append(infra.getPremioEntrega()).append(" pontos\n");
        info.append("Locais registados: ").append(totalLocais).append("\n");
        info.append("Total de anúncios: ").append(infra.getTotalAnuncios()).append("\n");
        info.append("Total de entregas: ").append(infra.getTotalEntregas()).append("\n");

        return info.toString();
    }

    /**
     * Cria um novo local na infraestrutura
     */
    public Local criarLocal(Long infraId, String nomeLocal) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra == null) {
            throw new IllegalArgumentException("Infraestrutura não encontrada");
        }
        if (nomeLocal == null || nomeLocal.isEmpty()) {
            throw new IllegalArgumentException("Nome do local não pode ser vazio");
        }

        Local local = new Local(nomeLocal, infra);
        return localRepo.save(local);
    }

    /**
     * Obtém um local
     */
    public Local obterLocal(Long localId) {
        return localRepo.findById(localId);
    }

    /**
     * Lista locais de uma infraestrutura
     */
    public List<Local> listarLocaisDaInfraestrutura(Long infraId) {
        return localRepo.findByInfraestruturaId(infraId);
    }

    /**
     * Adiciona restrição a uma infraestrutura
     */
    public Restricao adicionarRestricao(Long infraId, String descricao, Restricao.TipoRestricao tipo) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra == null) {
            throw new IllegalArgumentException("Infraestrutura não encontrada");
        }

        Restricao restricao = new Restricao(infra, descricao, tipo);
        return restricaoRepo.save(restricao);
    }

    /**
     * Lista restrições de uma infraestrutura
     */
    public List<Restricao> listarRestricoesDaInfraestrutura(Long infraId) {
        return restricaoRepo.findByInfraId(infraId);
    }

    /**
     * Remove uma restrição
     */
    public void removerRestricao(Long restricaoId) {
        Restricao restricao = restricaoRepo.findById(restricaoId);
        if (restricao != null) {
            restricaoRepo.delete(restricao);
        }
    }

    /**
     * Incrementa o contador de anúncios da infraestrutura
     */
    public void incrementarTotalAnuncios(Long infraId) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra != null) {
            infra.incrementarTotalAnuncios();
            atualizarInfraestrutura(infra);
        }
    }

    /**
     * Incrementa o contador de entregas da infraestrutura
     */
    public void incrementarTotalEntregas(Long infraId) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra != null) {
            infra.incrementarTotalEntregas();
            atualizarInfraestrutura(infra);
        }
    }

    /**
     * Retorna o número de conexões disponíveis
     */
    public Integer obterConexoesDisponiveis(Long infraId) {
        Infraestrutura infra = obterInfraestrutura(infraId);
        if (infra == null) {
            return null;
        }
        return infra.getConexoesDisponiveis();
    }
}
