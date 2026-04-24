package pt.uan.anuncioSloc;

import pt.uan.anuncioSloc.persistence.entity.Anuncio;
import pt.uan.anuncioSloc.persistence.entity.Infraestrutura;
import pt.uan.anuncioSloc.persistence.entity.Local;
import pt.uan.anuncioSloc.persistence.entity.Restricao;
import pt.uan.anuncioSloc.persistence.entity.UtilizadorInfra;
import pt.uan.anuncioSloc.persistence.repository.InMemoryPersistenceStore;
import pt.uan.anuncioSloc.persistence.repository.LocalRepository;
import pt.uan.anuncioSloc.service.AnuncioService;
import pt.uan.anuncioSloc.service.InfraestruturaService;
import pt.uan.anuncioSloc.service.UtilizadorService;

import java.util.List;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== AnunciosLoc Infrastructure CLI ===");
        System.out.println("Use este menu para cadastrar e pesquisar dados em memória.\n");

        boolean running = true;
        while (running) {
            printMenu();
            String opcao = scanner.nextLine().trim();

            try {
                switch (opcao) {
                    case "1":
                        printAllData(infraestruturaService, utilizadorService, anuncioService);
                        break;
                    case "2":
                        createInfraestrutura(scanner, infraestruturaService);
                        break;
                    case "3":
                        createLocal(scanner, infraestruturaService);
                        break;
                    case "4":
                        createUtilizador(scanner, utilizadorService);
                        break;
                    case "5":
                        createAnuncio(scanner, anuncioService);
                        break;
                    case "6":
                        searchInfraestrutura(scanner, infraestruturaService);
                        break;
                    case "7":
                        searchAnunciosAtivos(scanner, anuncioService);
                        break;
                    case "8":
                        InMemoryPersistenceStore.clear();
                        System.out.println("Memória limpa com sucesso.\n");
                        break;
                    case "9":
                        seedDemoData(infraestruturaService, utilizadorService, anuncioService);
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Opção inválida.\n");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage() + "\n");
            }
        }

        System.out.println("CLI finalizada.");
    }

    private static void printMenu() {
        System.out.println("----- MENU -----");
        System.out.println("1 - Listar todos os dados");
        System.out.println("2 - Cadastrar infraestrutura");
        System.out.println("3 - Cadastrar local");
        System.out.println("4 - Cadastrar utilizador");
        System.out.println("5 - Cadastrar anúncio");
        System.out.println("6 - Pesquisar infraestrutura por ID");
        System.out.println("7 - Pesquisar anúncios ativos por infraestrutura");
        System.out.println("8 - Limpar memória");
        System.out.println("9 - Inserir dados de demo");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private static void printAllData(InfraestruturaService infraestruturaService,
                                     UtilizadorService utilizadorService,
                                     AnuncioService anuncioService) {
        System.out.println("\n=== DADOS CADASTRADOS EM MEMÓRIA ===");
        List<Infraestrutura> infraestruturas = infraestruturaService.listarTodas();
        if (infraestruturas.isEmpty()) {
            System.out.println("Nenhum registo encontrado.\n");
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

    private static void createInfraestrutura(Scanner scanner, InfraestruturaService infraestruturaService) {
        System.out.print("Nome da infraestrutura: ");
        String nome = scanner.nextLine().trim();
        System.out.print("URL do servidor: ");
        String url = scanner.nextLine().trim();
        Integer capacidade = readInt(scanner, "Capacidade máxima: ");
        Integer premio = readInt(scanner, "Prémio por entrega: ");

        Infraestrutura infra = infraestruturaService.criarInfraestrutura(nome, url, capacidade, premio);
        System.out.println("Infraestrutura criada com ID=" + infra.getId() + "\n");
    }

    private static void createLocal(Scanner scanner, InfraestruturaService infraestruturaService) {
        Long infraId = readLong(scanner, "ID da infraestrutura: ");
        System.out.print("Nome do local: ");
        String nome = scanner.nextLine().trim();

        Local local = infraestruturaService.criarLocal(infraId, nome);

        System.out.print("Deseja adicionar coordenada GPS agora? (s/n): ");
        String gps = scanner.nextLine().trim();
        if ("s".equalsIgnoreCase(gps)) {
            Double latitude = readDouble(scanner, "Latitude: ");
            Double longitude = readDouble(scanner, "Longitude: ");
            Integer raio = readInt(scanner, "Raio (metros): ");
            local.adicionarCoordenadaGPS(latitude, longitude, raio);
            new LocalRepository().update(local);
        }

        System.out.println("Local criado com ID=" + local.getId() + "\n");
    }

    private static void createUtilizador(Scanner scanner, UtilizadorService utilizadorService) {
        System.out.print("Email do utilizador: ");
        String email = scanner.nextLine().trim();
        Long infraId = readLong(scanner, "ID da infraestrutura: ");

        UtilizadorInfra util = utilizadorService.criarUtilizador(email, infraId);
        System.out.println("Utilizador criado com ID=" + util.getId() + "\n");
    }

    private static void createAnuncio(Scanner scanner, AnuncioService anuncioService) {
        System.out.print("Email do autor: ");
        String email = scanner.nextLine().trim();
        Long infraId = readLong(scanner, "ID da infraestrutura: ");
        Long localId = readLong(scanner, "ID do local: ");
        System.out.print("Conteúdo do anúncio: ");
        String conteudo = scanner.nextLine().trim();

        Anuncio anuncio = anuncioService.criarAnuncio(email, infraId, localId, conteudo);
        System.out.println("Anúncio criado com ID=" + anuncio.getId() + "\n");
    }

    private static void searchInfraestrutura(Scanner scanner, InfraestruturaService infraestruturaService) {
        Long infraId = readLong(scanner, "ID da infraestrutura: ");
        String info = infraestruturaService.obterInfoDetalhada(infraId);
        if (info == null) {
            System.out.println("Infraestrutura não encontrada.\n");
            return;
        }
        System.out.println("\n" + info);
    }

    private static void searchAnunciosAtivos(Scanner scanner, AnuncioService anuncioService) {
        Long infraId = readLong(scanner, "ID da infraestrutura: ");
        List<Anuncio> anuncios = anuncioService.listarAnunciosAtivos(infraId);
        if (anuncios.isEmpty()) {
            System.out.println("Nenhum anúncio ativo encontrado.\n");
            return;
        }

        System.out.println("Anúncios ativos:");
        for (Anuncio anuncio : anuncios) {
            System.out.println("- ID=" + anuncio.getId()
                    + " | autor=" + anuncio.getEmail()
                    + " | local=" + (anuncio.getLocal() != null ? anuncio.getLocal().getNome() : "-")
                    + " | conteúdo=" + anuncio.getConteudo());
        }
        System.out.println();
    }

    private static Integer readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    private static Long readLong(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro longo.");
            }
        }
    }

    private static Double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número decimal.");
            }
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
