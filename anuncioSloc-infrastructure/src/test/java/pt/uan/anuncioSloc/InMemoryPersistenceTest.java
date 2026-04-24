package pt.uan.anuncioSloc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.repository.AnuncioRepository;
import pt.uan.anuncioSloc.persistence.repository.CoordenadaRepository;
import pt.uan.anuncioSloc.persistence.repository.InMemoryPersistenceStore;
import pt.uan.anuncioSloc.persistence.repository.InfraestruturaRepository;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import pt.uan.anuncioSloc.persistence.repository.RestricaoRepository;
import pt.uan.anuncioSloc.persistence.repository.UtilizadorInfraRepository;
import pt.uan.anuncioSloc.service.AnuncioService;
import pt.uan.anuncioSloc.service.InfraestruturaService;
import pt.uan.anuncioSloc.service.UtilizadorService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryPersistenceTest {

    private InfraestruturaService infraestruturaService;
    private AnuncioService anuncioService;
    private UtilizadorService utilizadorService;

    private InfraestruturaRepository infraestruturaRepository;
    private LocalRepository localRepository;
    private RestricaoRepository restricaoRepository;
    private CoordenadaRepository coordenadaRepository;
    private AnuncioRepository anuncioRepository;
    private UtilizadorInfraRepository utilizadorRepository;

    @BeforeEach
    void setUp() {
        InMemoryPersistenceStore.clear();
        infraestruturaService = new InfraestruturaService();
        anuncioService = new AnuncioService();
        utilizadorService = new UtilizadorService();
        infraestruturaRepository = new InfraestruturaRepository();
        localRepository = new LocalRepository();
        restricaoRepository = new RestricaoRepository();
        coordenadaRepository = new CoordenadaRepository();
        anuncioRepository = new AnuncioRepository();
        utilizadorRepository = new UtilizadorInfraRepository();
    }

    @AfterEach
    void tearDown() {
        InMemoryPersistenceStore.clear();
    }

    @Test
    void shouldPersistInfrastructureHierarchyInMemory() {
        Infraestrutura infraestrutura = infraestruturaService.criarInfraestrutura(
                "Infra A",
                "http://infra-a",
                10,
                7
        );

        Local local = infraestruturaService.criarLocal(infraestrutura.getId(), "Centro");
        local.adicionarCoordenadaGPS(38.72, -9.13, 150);
        localRepository.update(local);

        Restricao restricao = infraestruturaService.adicionarRestricao(
                infraestrutura.getId(),
                "Sem spam",
                Restricao.TipoRestricao.FILTRO_CONTEUDO
        );

        assertAll(
                () -> assertNotNull(infraestrutura.getId()),
                () -> assertNotNull(local.getId()),
                () -> assertNotNull(restricao.getId()),
                () -> assertEquals(1, infraestruturaRepository.count()),
                () -> assertEquals(1, localRepository.count()),
                () -> assertEquals(1, restricaoRepository.count()),
                () -> assertEquals(1, coordenadaRepository.count()),
                () -> assertEquals(1, infraestruturaService.listarLocaisDaInfraestrutura(infraestrutura.getId()).size()),
                () -> assertEquals(1, infraestruturaService.listarRestricoesDaInfraestrutura(infraestrutura.getId()).size()),
                () -> assertEquals(1, coordenadaRepository.findGPSByLocalId(local.getId()).size()),
                () -> assertTrue(infraestruturaService.obterInfoDetalhada(infraestrutura.getId()).contains("Locais registados: 1"))
        );
    }

    @Test
    void shouldCreateAnuncioAndUpdateStatistics() {
        Infraestrutura infraestrutura = infraestruturaService.criarInfraestrutura(
                "Infra B",
                "http://infra-b",
                12,
                5
        );
        Local local = infraestruturaService.criarLocal(infraestrutura.getId(), "Biblioteca");
        utilizadorService.criarUtilizador("user@site.com", infraestrutura.getId());

        Anuncio anuncio = anuncioService.criarAnuncio(
                "user@site.com",
                infraestrutura.getId(),
                local.getId(),
                "Oferta disponível"
        );

        assertAll(
                () -> assertNotNull(anuncio.getId()),
                () -> assertEquals(1, anuncioRepository.count()),
                () -> assertEquals(1, anuncioService.contarAnunciosPorUtilizador("user@site.com")),
                () -> assertEquals(1, anuncioService.contarAnunciosPorInfraestrutura(infraestrutura.getId())),
                () -> assertEquals(1L, infraestruturaRepository.findById(infraestrutura.getId()).getTotalAnuncios()),
                () -> assertEquals(1L, localRepository.findById(local.getId()).getTotalAnuncios()),
                () -> assertEquals(Integer.valueOf(10), utilizadorService.obterSaldo("user@site.com", infraestrutura.getId())),
                () -> assertEquals(1L, utilizadorRepository.findByEmailAndInfraId("user@site.com", infraestrutura.getId()).getTotalAunsioPublicados())
        );

        anuncioService.marcarEntregue(anuncio.getId(), infraestrutura.getId());

        Anuncio atualizado = anuncioService.obterAnuncio(anuncio.getId());
        assertAll(
                () -> assertEquals(1L, atualizado.getTotalEntregas()),
                () -> assertEquals(1L, infraestruturaRepository.findById(infraestrutura.getId()).getTotalEntregas()),
                () -> assertEquals(1L, localRepository.findById(local.getId()).getTotalEntregas()),
                () -> assertEquals(5, anuncioService.obterAnuncio(anuncio.getId()).getPontosGerados()),
                () -> assertEquals(Integer.valueOf(15), utilizadorService.obterSaldo("user@site.com", infraestrutura.getId()))
        );
    }

    @Test
    void shouldFindAndRemoveExpiredAnuncios() {
        Infraestrutura infraestrutura = infraestruturaService.criarInfraestrutura(
                "Infra C",
                "http://infra-c",
                8,
                3
        );
        Local local = infraestruturaService.criarLocal(infraestrutura.getId(), "Praça");
        utilizadorService.criarUtilizador("expirado@site.com", infraestrutura.getId());

        Anuncio anuncio = anuncioService.criarAnuncio(
                "expirado@site.com",
                infraestrutura.getId(),
                local.getId(),
                "Anúncio com prazo"
        );
        anuncio.setDataExpiracao(System.currentTimeMillis() - 1_000L);
        anuncioRepository.update(anuncio);

        assertEquals(1, anuncioRepository.findExpired().size());

        anuncioService.removerAnunciosExpirados();

        Anuncio removido = anuncioService.obterAnuncio(anuncio.getId());
        assertAll(
                () -> assertNotNull(removido),
                () -> assertFalse(removido.getAtivo()),
                () -> assertEquals(0, anuncioRepository.findAtivesByLocalId(local.getId()).size())
        );
    }
}