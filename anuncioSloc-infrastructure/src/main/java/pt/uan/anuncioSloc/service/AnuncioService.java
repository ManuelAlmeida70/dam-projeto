package pt.uan.anuncioSloc.service;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.repository.AnuncioRepository;
import pt.uan.anuncioSloc.persistence.repository.InfraestruturaRepository;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de lógica de negócio para Anúncio
 */
public class AnuncioService {

    private final AnuncioRepository anuncioRepo = new AnuncioRepository();
    private final InfraestruturaRepository infraRepo = new InfraestruturaRepository();
    private final LocalRepository localRepo = new LocalRepository();
    private final UtilizadorService utilizadorService = new UtilizadorService();

    /**
     * Cria um novo anúncio
     */
    public Anuncio criarAnuncio(String email, Long infraId, Long localId, String conteudo) {
        // Validações
        if (!UtilizadorService.validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }
        if (conteudo == null || conteudo.isEmpty()) {
            throw new IllegalArgumentException("Conteúdo do anúncio não pode ser vazio");
        }
        if (conteudo.length() > 2000) {
            throw new IllegalArgumentException("Conteúdo do anúncio muito longo (máximo 2000 caracteres)");
        }

        Infraestrutura infra = infraRepo.findById(infraId);
        if (infra == null) {
            throw new IllegalArgumentException("Infraestrutura não encontrada");
        }

        Local local = localRepo.findById(localId);
        if (local == null || !local.getInfraestrutura().getId().equals(infraId)) {
            throw new IllegalArgumentException("Local não encontrado ou não pertence a esta infraestrutura");
        }

        // Cria o anúncio
        Anuncio anuncio = new Anuncio(email, infra, local, conteudo);
        Anuncio salvo = anuncioRepo.save(anuncio);

        // Atualiza estatísticas
        infra.incrementarTotalAnuncios();
        infraRepo.update(infra);

        local.incrementarTotalAnuncios();
        localRepo.update(local);

        // Atualiza utilizador
        utilizadorService.incrementarAnunsiosPublicados(email, infraId);
        utilizadorService.atualizarUltimaAtividade(email, infraId);

        return salvo;
    }

    /**
     * Obtém um anúncio por ID
     */
    public Anuncio obterAnuncio(Long id) {
        return anuncioRepo.findById(id);
    }

    /**
     * Lista anúncios de um utilizador
     */
    public List<Anuncio> listarAnunciosPorUtilizador(String email) {
        return anuncioRepo.findByEmail(email);
    }

    /**
     * Lista anúncios ativos de uma infraestrutura
     */
    public List<Anuncio> listarAnunciosAtivos(Long infraId) {
        return anuncioRepo.findAtivesByInfraId(infraId);
    }

    /**
     * Lista anúncios ativos de um local
     */
    public List<Anuncio> listarAnunciosAtivosDoLocal(Long localId) {
        return anuncioRepo.findAtivesByLocalId(localId);
    }

    /**
     * Marca um anúncio como entregue
     */
    public void marcarEntregue(Long anuncioId, Long infraId) {
        Anuncio anuncio = obterAnuncio(anuncioId);
        if (anuncio == null) {
            throw new IllegalArgumentException("Anúncio não encontrado");
        }

        // Incrementa contadores
        anuncio.incrementarTotalEntregas();
        anuncioRepo.update(anuncio);

        // Atualiza estatísticas do local
        Local local = anuncio.getLocal();
        local.incrementarTotalEntregas();
        localRepo.update(local);

        // Atualiza estatísticas da infraestrutura
        Infraestrutura infra = infraRepo.findById(infraId);
        if (infra != null) {
            infra.incrementarTotalEntregas();
            infraRepo.update(infra);
        }

        // Adiciona prémio ao autor
        if (infra != null) {
            utilizadorService.adicionarPontos(anuncio.getEmail(), infraId, infra.getPremioEntrega());
            anuncio.adicionarPontosGerados(infra.getPremioEntrega());
            anuncioRepo.update(anuncio);
        }
    }

    /**
     * Desativa um anúncio
     */
    public void desativarAnuncio(Long anuncioId) {
        Anuncio anuncio = obterAnuncio(anuncioId);
        if (anuncio != null) {
            anuncio.setAtivo(false);
            anuncioRepo.update(anuncio);
        }
    }

    /**
     * Remove anúncios expirados
     */
    public void removerAnunciosExpirados() {
        List<Anuncio> expirados = anuncioRepo.findExpired();
        for (Anuncio anuncio : expirados) {
            desativarAnuncio(anuncio.getId());
        }
    }

    /**
     * Retorna estatísticas de um anúncio
     */
    public String obterEstatisticasAnuncio(Long anuncioId) {
        Anuncio anuncio = obterAnuncio(anuncioId);
        if (anuncio == null) {
            return null;
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Anúncio ID: ").append(anuncio.getId()).append("\n");
        stats.append("Autor: ").append(anuncio.getEmail()).append("\n");
        stats.append("Conteúdo: ").append(anuncio.getConteudo()).append("\n");
        stats.append("Total de entregas: ").append(anuncio.getTotalEntregas()).append("\n");
        stats.append("Pontos gerados: ").append(anuncio.getPontosGerados()).append("\n");
        stats.append("Status: ").append(anuncio.getAtivo() ? "Ativo" : "Inativo").append("\n");

        if (anuncio.getDataExpiracao() != null) {
            long tempoRestante = anuncio.getDataExpiracao() - System.currentTimeMillis();
            stats.append("Tempo restante: ").append(Math.max(0, tempoRestante / 1000)).append(" segundos\n");
        }

        return stats.toString();
    }

    /**
     * Conta anúncios de um utilizador
     */
    public long contarAnunciosPorUtilizador(String email) {
        return anuncioRepo.countByEmail(email);
    }

    /**
     * Conta anúncios de uma infraestrutura
     */
    public long contarAnunciosPorInfraestrutura(Long infraId) {
        return anuncioRepo.countByInfraId(infraId);
    }

    /**
     * Retorna os anúncios mais recentes de um local
     */
    public List<Anuncio> obterAnunciosMaisRecentes(Long localId, int quantidade) {
        List<Anuncio> anuncios = anuncioRepo.findAtivesByLocalId(localId);
        return anuncios.stream()
                .sorted((a, b) -> Long.compare(b.getDataPublicacao(), a.getDataPublicacao()))
                .limit(quantidade)
                .collect(Collectors.toList());
    }

    /**
     * Retorna os anúncios mais populares (mais entregas)
     */
    public List<Anuncio> obterAnunciosMaisPopulares(Long infraId, int quantidade) {
        List<Anuncio> anuncios = anuncioRepo.findAtivesByInfraId(infraId);
        return anuncios.stream()
                .sorted((a, b) -> Long.compare(b.getTotalEntregas(), a.getTotalEntregas()))
                .limit(quantidade)
                .collect(Collectors.toList());
    }
}
