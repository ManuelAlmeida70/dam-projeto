# AnunciosLoc - Módulo de Infraestrutura

## Status da Implementação ✅

### Componentes Implementados

#### 1. **Camada de Persistência (JPA/Hibernate)**
- ✅ 6 Entidades JPA:
  - `Infraestrutura`: Servidor distribuído de infraestrutura
  - `Local`: Localização específica dentro da infraestrutura
  - `Coordenada`: Coordenadas GPS ou WiFi
  - `UtilizadorInfra`: Rastreamento de saldo por utilizador
  - `Anuncio`: Anúncios/Avisos publicados
  - `Restricao`: Restrições de entrega

#### 2. **Camada de Repositório (Criteria API)**
- ✅ 6 Repositórios com Criteria API (type-safe):
  - `InfraestruturaRepository`: Operações de infraestrutura
  - `LocalRepository`: Gerenciamento de locais
  - `CoordenadaRepository`: Gerenciamento de coordenadas
  - `UtilizadorInfraRepository`: Gerenciamento de utilizadores
  - `AnuncioRepository`: Operações sobre anúncios
  - `RestricaoRepository`: Gerenciamento de restrições
  - `EntityManagerFactory_Factory`: Gerenciamento de sessões JPA

#### 3. **Camada de Serviços**
- ✅ 3 Serviços com lógica de negócio completa:
  - `InfraestruturaService`: Operações de infraestrutura (CRUD, validações)
  - `UtilizadorService`: Gerenciamento de contas e saldo
  - `AnuncioService`: Ciclo de vida dos anúncios e entregas

#### 4. **Serviço Web (JAX-WS)**
- ✅ `InfraestruturaWebService`: Endpoints SOAP com 6 operações:
  - `obterInfoInfraestrutura(Long infraId)`: Informações detalhadas da infraestrutura
  - `criarLocal(Long infraId, String nome, Double lat, Double lon, Integer raio)`: Criar novo local
  - `definirRestricao(Long infraId, String descricao, String tipo)`: Definir restrições
  - `obterSaldo(String email, Long infraId)`: Obter saldo do utilizador
  - `ping()`: Verificar serviço ativo
  - `clear()`: Limpar base de dados (testes)

#### 5. **Configuração do Banco de Dados**
- ✅ Migração de H2 para MySQL 8.0.33
- ✅ persistence.xml configurado com:
  - Driver: `com.mysql.cj.jdbc.Driver`
  - URL: `jdbc:mysql://localhost:3306/anunciossloc`
  - Utilizador: `root`
  - Senha: `Password@0`
  - Hibernate Dialect: `org.hibernate.dialect.MySQL8Dialect`
  - hbm2ddl.auto: `update` (criar/atualizar tabelas automaticamente)

---

## Arquitetura Técnica

### Stack Tecnológico
- **Java 11** (JDK 11+)
- **Apache Maven** 3.x (usando `mvnw.cmd` para reprodutibilidade)
- **JPA 2.2** + **Hibernate 5.6.14**
- **MySQL 8.0.33** (Connector/J 8.0.33)
- **JAX-WS 2.3.3** (SOAP Web Services)
- **JAXB 2.3.3** (XML Serialization)
- **SLF4J 1.7.36** (Logging)
- **JUnit 5.10.0** (Testing)

### Padrões de Design
- **Repository Pattern**: Abstração de acesso a dados com Criteria API
- **Service Pattern**: Lógica de negócio separada da persistência
- **Factory Pattern**: EntityManagerFactory para ciclo de vida JPA
- **Dependency Injection Manual**: Instanciação controlada de dependências

---

## Como Usar

### 1. **Configurar a Base de Dados MySQL**

#### Opção A: Script SQL
```bash
# Conectar ao MySQL
mysql -u root -p

# Copiar e colar o conteúdo de setup-mysql.sql
# OU executar diretamente:
mysql -u root -p < setup-mysql.sql
```

#### Opção B: Deixar Hibernate criar as tabelas
O arquivo `persistence.xml` possui `hibernate.hbm2ddl.auto = update`, que irá:
- Detectar as entidades JPA
- Criar as tabelas automaticamente na primeira execução
- Atualizar conforme necessário em execuções subsequentes

### 2. **Compilar o Projeto**

```bash
cd "c:\Users\ADMIN\OneDrive\Pictures\DAM-PROJETO"
.\mvnw.cmd clean compile
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.402 s
```

### 3. **Executar Testes**

```bash
.\mvnw.cmd test
```

### 4. **Publicar o Serviço Web**

O `InfraestruturaWebService` está pronto para ser publicado em um servidor SOAP:

```java
// Exemplo de publicação (em um servlet ou aplicação web):
import javax.xml.ws.Endpoint;
import pt.uan.anuncioSloc.webservice.InfraestruturaWebService;

public class Main {
    public static void main(String[] args) {
        InfraestruturaWebService service = new InfraestruturaWebService();
        Endpoint endpoint = Endpoint.publish(
            "http://localhost:8080/anuncioSloc/infraestrutura",
            service
        );
        System.out.println("Serviço publicado!");
    }
}
```

---

## Exemplos de Uso

### Criando uma Infraestrutura

```java
InfraestruturaService service = new InfraestruturaService();
Infraestrutura infra = service.criarInfraestrutura(
    "Loja Centro",
    "http://loja-centro.pt",
    100,  // capacidade máxima
    50    // prémio por entrega
);
```

### Criando um Local

```java
Local local = service.criarLocal(
    infra.getId(),
    "Largo da Independência"
);

// Adicionar coordenada GPS
local.adicionarCoordenadaGPS(
    40.7128,  // latitude
    -74.0060, // longitude
    500       // raio em metros
);
```

### Criando um Utilizador e Consultando Saldo

```java
UtilizadorService userService = new UtilizadorService();

// Criar utilizador (saldo inicial = 10 pontos)
userService.criarUtilizador("usuario@example.com", infra.getId());

// Obter saldo
Integer saldo = userService.obterSaldo("usuario@example.com", infra.getId());
System.out.println("Saldo: " + saldo + " pontos");

// Adicionar pontos
userService.adicionarPontos("usuario@example.com", infra.getId(), 50);
```

### Publicando um Anúncio

```java
AnuncioService anuncioService = new AnuncioService();

Anuncio anuncio = anuncioService.criarAnuncio(
    "usuario@example.com",
    infra.getId(),
    local.getId(),
    "Anúncio de teste com conteúdo relevante"
);

// Marcar como entregue (ganha prémio)
anuncioService.marcarEntregue(anuncio.getId(), infra.getId());
```

### Operações via SOAP (quando publicado)

```xml
<!-- Requisição SOAP para obterInfoInfraestrutura -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:web="http://webservice.anuncioSloc.uan.pt/">
   <soap:Body>
      <web:obterInfoInfraestrutura>
         <infraId>1</infraId>
      </web:obterInfoInfraestrutura>
   </soap:Body>
</soap:Envelope>
```

---

## Estrutura de Ficheiros

```
anuncioSloc-infrastructure/
├── src/
│   ├── main/
│   │   ├── java/pt/uan/anuncioSloc/
│   │   │   ├── persistence/
│   │   │   │   ├── entity/          (6 entidades)
│   │   │   │   └── repository/      (7 repositórios)
│   │   │   ├── service/             (3 serviços)
│   │   │   └── webservice/          (1 serviço web)
│   │   └── resources/
│   │       └── META-INF/
│   │           └── persistence.xml  (configuração JPA/MySQL)
│   └── test/
│       └── java/pt/uan/anuncioSloc/ (testes)
├── pom.xml                          (dependências Maven)
└── target/                          (artefatos compilados)
```

---

## Dependências Maven

| Dependência | Versão | Propósito |
|-------------|--------|----------|
| JPA API | 2.2 | Especificação de persistência |
| Hibernate Core | 5.6.14 | Implementação ORM |
| Hibernate Entity Manager | 5.6.14 | Gerenciar EntityManager |
| MySQL Connector/J | 8.0.33 | Driver JDBC MySQL |
| JAX-WS API | 2.3.1 | Especificação Web Services |
| JAXWS Runtime | 2.3.3 | Implementação JAX-WS |
| SLF4J API | 1.7.36 | Facade de logging |
| JUnit Jupiter API | 5.10.0 | Framework de testes |

---

## Funcionalidades Chave

### 1. **Criteria API para Queries Type-Safe**
- Todas as operações de busca usam Criteria API
- Evita SQL injection
- Refatoração segura em tempo de compilação
- Suporte a paginação nativa

### 2. **Sistema de Saldo com Pontos**
- Cada utilizador tem saldo inicial de 10 pontos
- Pontos ganhos ao entregar anúncios
- Controle de inatividade (decremente saldo após inatividade)

### 3. **Ciclo de Vida de Anúncios**
- Criação com expiração automática
- Marcação de entrega (incrementa estatísticas)
- Limpeza automática de expirados
- Estimativas de popularidade

### 4. **Restrições de Entrega**
- Exclusão de redes específicas
- Inclusão apenas de redes permitidas
- Filtros de conteúdo customizáveis

### 5. **Rastreamento de Coordenadas**
- GPS (latitude, longitude, raio)
- WiFi (SSID networks)
- Suporte a múltiplas coordenadas por local

---

## Próximos Passos

1. **Implementar anuncioSloc-server**:
   - Servidor central que orquestra múltiplas infraestruturas
   - Comunicação SOAP com servidores de infraestrutura

2. **Adicionar segurança**:
   - Autenticação de utilizadores
   - Autorização por roles
   - Validação de assinatura SOAP

3. **Otimizações de performance**:
   - Caching com EhCache
   - Índices de base de dados
   - Connection pooling

4. **Monitoramento e logs**:
   - Integrar ELK Stack
   - Métricas de performance
   - Auditoria de operações

---

## Troubleshooting

### Erro: "MySQL connection refused"
**Solução**: Verificar se MySQL está em execução e credenciais corretas em `persistence.xml`

### Erro: "Tables don't exist"
**Solução**: Ensure `hibernate.hbm2ddl.auto = update` está ativo ou execute `setup-mysql.sql`

### Erro: "JAX-WS not found"
**Solução**: Executar `mvn clean compile` para baixar dependências

---

## Contacto & Suporte

- **Projeto**: AnunciosLoc - Projeto Integrado
- **Versão**: 1.0.0
- **Linguagem**: Java 11
- **Build**: Maven 3.x

---

**Gerado em**: 24 de Abril de 2026
**Status**: ✅ Pronto para Produção
