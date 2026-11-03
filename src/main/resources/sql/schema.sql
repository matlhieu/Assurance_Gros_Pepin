DROP TABLE IF EXISTS paiements;
DROP TABLE IF EXISTS pieces_jointes;
DROP TABLE IF EXISTS sinistres;
DROP TABLE IF EXISTS contrat_garanties;
DROP TABLE IF EXISTS contrats;
DROP TABLE IF EXISTS garanties;
DROP TABLE IF EXISTS types_assurance;
DROP TABLE IF EXISTS utilisateurs;

CREATE TABLE utilisateurs (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(100) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE types_assurance (
    id_type INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    description TEXT,
    prix_base DECIMAL(10, 2) NOT NULL
);

CREATE TABLE garanties (
    id_garantie INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE type_garanties (
    id_type INT NOT NULL,
    id_garantie INT NOT NULL,
    PRIMARY KEY (id_type, id_garantie),
    FOREIGN KEY (id_type) REFERENCES types_assurance(id_type) ON DELETE CASCADE,
    FOREIGN KEY (id_garantie) REFERENCES garanties(id_garantie) ON DELETE CASCADE
);