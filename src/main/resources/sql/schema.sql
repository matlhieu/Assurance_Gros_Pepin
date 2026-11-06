DROP TABLE IF EXISTS paiements;
DROP TABLE IF EXISTS pieces_jointes;
DROP TABLE IF EXISTS sinistres;
DROP TABLE IF EXISTS contrat_garanties;
DROP TABLE IF EXISTS contrats;
DROP TABLE IF EXISTS type_garanties;
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

CREATE TABLE contrats (
    id_contrat INT AUTO_INCREMENT PRIMARY KEY,
    numero_contrat VARCHAR(50) NOT NULL UNIQUE,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    prime DECIMAL(10, 2) NOT NULL,
    statut VARCHAR(20) NOT NULL, -- plusieurs status : EN_ATTENTE, ACTIF, SUSPENDU, EXPIRE, RESILIE
    id_utilisateur INT NOT NULL,
    id_type INT NOT NULL,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur),
    FOREIGN KEY (id_type) REFERENCES types_assurance(id_type)
);

CREATE TABLE contrat_garanties (
    id_contrat INT NOT NULL,
    id_garantie INT NOT NULL,
    PRIMARY KEY (id_contrat, id_garantie),
    FOREIGN KEY (id_contrat) REFERENCES contrats(id_contrat) ON DELETE CASCADE,
    FOREIGN KEY (id_garantie) REFERENCES garanties(id_garantie) ON DELETE CASCADE
);

CREATE TABLE sinistres (
    id_sinistre INT AUTO_INCREMENT PRIMARY KEY,
    numero_sinistre VARCHAR(50) NOT NULL UNIQUE,
    date_declaration DATE NOT NULL,
    description TEXT,
    montant_estime DECIMAL(10, 2) NOT NULL,
    type_sinistre VARCHAR(50),
    statut VARCHAR(20) NOT NULL, -- plusieurs états : DECLARE, EN_ANALYSE, ACCEPTE, REFUSE, INDEMNISE
    montant_indemnise DECIMAL(10, 2),
    commentaire_agent TEXT,
    id_contrat INT NOT NULL,
    FOREIGN KEY (id_contrat) REFERENCES contrats(id_contrat)
);

CREATE TABLE pieces_jointes (
    id_piece INT AUTO_INCREMENT PRIMARY KEY,
    nom_fichier VARCHAR(255) NOT NULL,
    chemin_fichier VARCHAR(255) NOT NULL,
    id_sinistre INT NOT NULL,
    FOREIGN KEY (id_sinistre) REFERENCES sinistres(id_sinistre) ON DELETE CASCADE
);

CREATE TABLE paiements (
    id_paiement INT AUTO_INCREMENT PRIMARY KEY,
    montant DECIMAL(10, 2) NOT NULL,
    date_paiement DATE NOT NULL,
    mode_paiement VARCHAR(30) NOT NULL, -- 3 types : CARTE, VIREMENT, PRELEVEMENT
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, EFFECTUE, ECHOUE
    id_contrat INT NOT NULL,
    FOREIGN KEY (id_contrat) REFERENCES contrats(id_contrat)
);