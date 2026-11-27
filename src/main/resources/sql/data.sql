-- utilisateurs : mots de passe hachés en BCrypt (comptes de test)
-- admin123 / agent123 / KingInTheNorth / Dracaris
INSERT INTO utilisateurs (email, mot_de_passe, nom, prenom, role) VALUES
('projetadmin@assurance.fr', '$2a$12$7vmY0yPbZ9SSRyP.R6QfluZOVRLqRt3WtwMKFzyISpLYfIpC48KSq', 'Chef', 'Admin', 'ADMIN'),
('agent@assurance.fr','$2a$12$9.3JblvJ4ztnnPnmRprgZOPITiQKJuHtvaDUK2NH/ufKCJCk91eT6','Bond','James','AGENT'),
('robbstark@gmail.com','$2a$12$5qJW54HDs6plOYhuNfF.N.8ALrpQdu3Yo/3vATgaeu4iRulSgQKKi','Stark','Robb','CLIENT'),
('daenerys@gmail.com','$2a$12$MlFViXR3N95uVN2JbHgn2eFiPbtBJOhzB.xctFISM.dPULtk8QCji','Targaryen','Daenerys','CLIENT');

INSERT INTO types_assurance (nom, description, prix_base) VALUES
('Automobile', 'Assurance auto tous risques et tiers', 300.00),
('Habitation', 'Assurance logement principal et secondaire', 180.00),
('Sante', 'Complementaire sante et mutuelle', 120.00),
('Voyage', 'Assurance rapatriement et sejour', 40.00),
('Professionnelle', 'Responsabilite civile pro et locaux', 450.00);

INSERT INTO garanties (nom, description) VALUES
('Responsabilite civile', 'Couvre les dommages causes aux tiers'),
('Vol', 'Indemnisation en cas de vol du bien'),
('Incendie', 'Couverture des degats suite a incendie'),
('Degats des eaux', 'Prise en charge des fuites et degats'),
('Assistance', 'Assistance depannage 24h/24 et 7j/7');

INSERT INTO type_garanties (id_type, id_garantie) VALUES
(1, 1), -- Auto : Responsabilite civile
(1, 2), -- Auto : Vol
(1, 5), -- Auto : Assistance
(2, 1), -- Habitation : Responsabilite civile
(2, 3), -- Habitation : Incendie
(2, 4); -- Habitation : Degats des eaux

INSERT INTO contrats (numero_contrat, date_debut, date_fin, prime, statut, id_utilisateur, id_type) VALUES
('CTR-2026-001', '2026-01-01', '2027-01-01', 350.00, 'ACTIF', 3, 1),
('CTR-2026-002', '2026-02-15', '2027-02-15', 200.00, 'ACTIF', 4, 2);

INSERT INTO contrat_garanties (id_contrat, id_garantie) VALUES
(1, 1),
(1, 2),
(1, 5),
(2, 1),
(2, 4);

INSERT INTO sinistres (numero_sinistre, date_declaration, description, montant_estime, type_sinistre, statut, montant_indemnise, commentaire_agent, id_contrat) VALUES
('SIN-2026-001', '2026-03-05', 'Pare-brise brise sur autoroute', 280.00, 'Accident', 'DECLARE', NULL, NULL, 1);

INSERT INTO paiements (montant, date_paiement, mode_paiement, statut, id_contrat) VALUES
(350.00, '2026-01-05', 'CARTE', 'EFFECTUE', 1),
(200.00, '2026-02-20', 'VIREMENT', 'EFFECTUE', 2),
(350.00, '2026-07-01', 'PRELEVEMENT', 'EN_ATTENTE', 1),
(200.00, '2026-08-15', 'VIREMENT', 'EN_ATTENTE', 2);


