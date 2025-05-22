-- Insertion des types de congés
INSERT INTO leave_types ( active, default_days, description, name) VALUES
                                                                          (true, 25, 'Congé payé annuel', 'Congé annuel'),
                                                                          (true, 15, 'Congé pour raison médicale', 'Congé maladie'),
                                                                          (true, 0, 'Congé non rémunéré', 'Congé sans solde'),
                                                                          (true, 90, 'Congé pour maternité', 'Congé maternité'),
                                                                          (true, 8, 'Congé pour paternité', 'Congé paternité');

-- Insertion des employés
INSERT INTO employees (id, email, name) VALUES
    (1, 'chakir@agilisys.com', 'chakir');

-- Insertion des soldes de congés
INSERT INTO leave_balances (id, available_days, used_days, year, employee_id, leave_type_id) VALUES
                                                                                                 (1, 25, 0, 2025, 1, 1),
                                                                                                 (2, 15, 0, 2025, 1, 2),
                                                                                                 (3, 0, 0, 2025, 1, 3),
                                                                                                 (4, 90, 0, 2025, 1, 4);