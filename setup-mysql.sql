-- Script para criar a base de dados MySQL para AnunciosLoc
-- Database: anunciossloc
-- User: root
-- Password: Password@0

-- Criar a base de dados
CREATE DATABASE IF NOT EXISTS anunciossloc
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar a base de dados
USE anunciossloc;

-- As tabelas serão criadas automaticamente pelo Hibernate
-- através da propriedade: hibernate.hbm2ddl.auto = update
-- no arquivo persistence.xml

-- Mas você pode criar manualmente as tabelas se necessário:

-- Tabela Infraestrutura
CREATE TABLE IF NOT EXISTS infraestrutura (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL UNIQUE,
    url_servidor VARCHAR(255) NOT NULL UNIQUE,
    capacidade_maxima INT NOT NULL,
    premio_entrega INT NOT NULL,
    total_anuncios INT DEFAULT 0,
    total_entregas INT DEFAULT 0,
    INDEX idx_nome (nome),
    INDEX idx_url (url_servidor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela Local
CREATE TABLE IF NOT EXISTS local (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    infraestrutura_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    total_anuncios INT DEFAULT 0,
    total_entregas INT DEFAULT 0,
    FOREIGN KEY (infraestrutura_id) REFERENCES infraestrutura(id) ON DELETE CASCADE,
    INDEX idx_infraestrutura (infraestrutura_id),
    INDEX idx_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela Coordenada
CREATE TABLE IF NOT EXISTS coordenada (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    local_id BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    raio INT,
    ssid VARCHAR(255),
    FOREIGN KEY (local_id) REFERENCES local(id) ON DELETE CASCADE,
    INDEX idx_local (local_id),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela UtilizadorInfra
CREATE TABLE IF NOT EXISTS utilizador_infra (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    infraestrutura_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    saldo INT NOT NULL DEFAULT 10,
    total_anuncios_publicados INT DEFAULT 0,
    total_pontos_ganhos INT DEFAULT 0,
    ultima_atividade BIGINT,
    FOREIGN KEY (infraestrutura_id) REFERENCES infraestrutura(id) ON DELETE CASCADE,
    UNIQUE KEY unique_email_infra (email, infraestrutura_id),
    INDEX idx_infraestrutura (infraestrutura_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela Anuncio
CREATE TABLE IF NOT EXISTS anuncio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    infraestrutura_id BIGINT NOT NULL,
    local_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    conteudo VARCHAR(2000) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao BIGINT,
    data_expiracao BIGINT,
    total_entregas INT DEFAULT 0,
    pontos_gerados INT DEFAULT 0,
    FOREIGN KEY (infraestrutura_id) REFERENCES infraestrutura(id) ON DELETE CASCADE,
    FOREIGN KEY (local_id) REFERENCES local(id) ON DELETE CASCADE,
    INDEX idx_infraestrutura (infraestrutura_id),
    INDEX idx_local (local_id),
    INDEX idx_email (email),
    INDEX idx_ativo (ativo),
    INDEX idx_expiracao (data_expiracao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela Restricao
CREATE TABLE IF NOT EXISTS restricao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    infraestrutura_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    descricao VARCHAR(255),
    FOREIGN KEY (infraestrutura_id) REFERENCES infraestrutura(id) ON DELETE CASCADE,
    INDEX idx_infraestrutura (infraestrutura_id),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verificar as tabelas criadas
SHOW TABLES;

-- Verificar a estrutura de uma tabela (opcional)
-- DESCRIBE infraestrutura;
