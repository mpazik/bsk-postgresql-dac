TRUNCATE ranga_konkursu RESTART IDENTITY CASCADE;
INSERT INTO ranga_konkursu (nazwa, waga) VALUES 
('międzynarodowy', 1),
('ogólnopolski', 2),
('wojewódzki', 3),
('powiatowy', 4),
('gminny', 5),
('szkolny', 6);