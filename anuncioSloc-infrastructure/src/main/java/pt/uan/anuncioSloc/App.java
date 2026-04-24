package pt.uan.anuncioSloc;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import pt.uan.anuncioSloc.service.AnuncioService;
import pt.uan.anuncioSloc.service.InfraestruturaService;
import pt.uan.anuncioSloc.service.UtilizadorService;

import java.util.List;

/**
 * Módulo de Infraestrutura - AnunciosLoc
 * 
 * Este módulo contém:
 * - Entidades para domínio e persistência em memória
 * - Repositories para acesso a dados
 * - Serviços de lógica de negócio
 */
public class App {

    public static void main(String[] args) {
        InfraestruturaService infraestruturaService = new InfraestruturaService();
        UtilizadorService utilizadorService = new UtilizadorService();
        AnuncioService anuncioService = new AnuncioService();

        if (infraestruturaService.listarTodas().isEmpty()) {
            seedDemoData(infraestruturaService, utilizadorService, anuncioService);
        }

        System.out.println("=== DADOS CADASTRADOS EM MEMÓRIA ===");
        List<Infraestrutura> infraestruturas = infraestruturaService.listarTodas();
        if (infraestruturas.isEmpty()) {
            System.out.println("Nenhum registo encontrado.");
            return;
        }

        for (Infraestrutura infraestrutura : infraestruturas) {
            Long infraId = infraestrutura.getId();
            System.out.println(infraestruturaService.obterInfoDetalhada(infraId));

            System.out.println("Locais:");
            for (Local local : infraestruturaService.listarLocaisDaInfraestrutura(infraId)) {
                System.out.println("- " + local.getNome()
                        + " | anúncios=" + local.getTotalAnuncios()
                        + " | entregas=" + local.getTotalEntregas());
            }

            System.out.println("Utilizadores:");
            for (UtilizadorInfra utilizador : utilizadorService.listarUtilizadoresDaInfraestrutura(infraId)) {
                System.out.println("- " + utilizador.getEmail()
                        + " | saldo=" + utilizador.getSaldo()
                        + " | publicados=" + utilizador.getTotalAunsioPublicados());
            }

            System.out.println("Restrições:");
            for (Restricao restricao : infraestruturaService.listarRestricoesDaInfraestrutura(infraId)) {
                System.out.println("- " + restricao.getTipo() + " | " + restricao.getDescricao());
            }

            System.out.println("Anúncios ativos:");
            for (Anuncio anuncio : anuncioService.listarAnunciosAtivos(infraId)) {
                System.out.println("- " + anuncio.getEmail()
                        + " | local=" + (anuncio.getLocal() != null ? anuncio.getLocal().getNome() : "-")
                        + " | conteúdo=" + anuncio.getConteudo()
                        + " | entregas=" + anuncio.getTotalEntregas());
            }

            System.out.println();
        }
    }

    private static void seedDemoData(InfraestruturaService infraestruturaService,
                                     UtilizadorService utilizadorService,
                                     AnuncioService anuncioService) {
        System.out.println("Sem dados cadastrados. A criar exemplo em memória...\n");

        Infraestrutura infraestrutura = infraestruturaService.criarInfraestrutura(
                "Infra Demo",
                "http://localhost/demo",
                20,
                5
        );

        Local local = infraestruturaService.criarLocal(infraestrutura.getId(), "Entrada Principal");
        local.adicionarCoordenadaGPS(38.7223, -9.1393, 150);
        new LocalRepository().update(local);

        infraestruturaService.adicionarRestricao(
                infraestrutura.getId(),
                "Sem spam",
                Restricao.TipoRestricao.FILTRO_CONTEUDO
        );

        utilizadorService.criarUtilizador("demo@site.com", infraestrutura.getId());

        Anuncio anuncio = anuncioService.criarAnuncio(
                "demo@site.com",
                infraestrutura.getId(),
                local.getId(),
                "Anúncio de demonstração"
        );
        anuncioService.marcarEntregue(anuncio.getId(), infraestrutura.getId());

        System.out.println("Exemplo criado. Registos em memória: " + infraestruturaService.listarTodas().size());
    }
}
