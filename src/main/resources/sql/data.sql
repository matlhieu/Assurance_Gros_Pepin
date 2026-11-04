-- utilisateurs :
INSERT INTO utilisateurs (email, mot_de_passe, nom, prenom, role) VALUES
('projetadmin@assurance.fr', 'admin123', 'Chef', 'Admin', 'ADMIN'),
('agent@assurance.fr','agent123','Bond','James','CLIENT'),
('robbstark@gmail.com','KingInTheNorth','Stark','Robb','CLIENT'),
('daenerys@gmail.com','Dracaris','Targaryen','Daenerys','CLIENT');

INSERT INTO types_assurance (nom, description, prix_base) VALUES
('Automobile', 'Assurance auto tous risques et tiers', 300.00),
('Habitation', 'Assurance logement principal et secondaire', 180.00),
('Sante', 'Complementaire sante et mutuelle', 120.00),
('Voyage', 'Assurance rapatriement et sejour', 40.00),
('Professionnelle', 'Responsabilite civile pro et locaux', 450.00);



