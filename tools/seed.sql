BEGIN;
DELETE FROM messages;
DELETE FROM contacts;
DELETE FROM sqlite_sequence WHERE name IN ('contacts', 'messages');

INSERT INTO contacts (first_name, last_name, company, phone, phone_country, address, birthday, note) VALUES
  ('Colette', 'Martin', '42 Lyon', '+33601020304', 'FR',
   '104 Route de Paris 69260 Charbonnières-les-Bains', '1925-04-12', 'Peer'),

  ('Bob', 'Durand', NULL, '+33605060708', 'FR',
   NULL, NULL, NULL),

  ('Chloé', 'Bernard', 'Ubisoft', '+33611223344', 'FR',
   '12 rue de Rivoli 75004 Paris', '1990-11-03', NULL),

  ('David', 'Johnson', NULL, '+12125550123', 'US',
   NULL, '1988-02-29', 'Leap year birthday'),

  ('Hugo', 'Suzuki', 'Doctolib', '+33633445566', 'FR',
   '5 av. Foch 69006 Lyon', NULL, NULL),

  ('Emily', 'Walker', NULL, '+15145550147', 'CA',
   NULL, NULL, NULL),

  ('Gabriel', 'Moreau', '42 Paris', '+33655667788', 'FR',
   NULL, '2000-01-01', NULL),

  ('Naomi', 'Sato', 'Agence impériale', '+819012345678', 'JP',
   '1-1 Chiyoda, Chiyoda City, Tokyo 100-8111', NULL, NULL),

  ('Isabelle', 'Simon', 'BlaBlaCar', '+33677889900', 'FR',
   NULL, '1997-07-14', NULL),

  ('Jules', 'Michel', NULL, '+33688990011', 'FR',
   NULL, NULL, 'Met at the pool'),

  ('Kenji', 'Tanaka', NULL, '+818023456789', 'JP',
   NULL, '1993-05-05', NULL),

  ('Léa', 'Fontaine', 'Mistral AI', '+33700112233', 'FR',
   '8 quai de Seine, Paris', NULL, NULL),

  ('Mathis', NULL, NULL, '+33612987654', 'FR',
   NULL, NULL, 'Climbing gym'),

  ('Boulangerie Painpote', NULL, NULL, '+33478123456', 'FR',
   '22 Rue Tronchet, 69006 Lyon', NULL, 'Opens at 7am');

-- 25 messages with Colette (id 1): minutes_ago, is_incoming, content
WITH m(minutes_ago, is_incoming, content) AS (
  VALUES
    (4320, 1, 'Bonjour ! C''est Colette, de l''atelier de cuisine. Vous m''aviez donné votre numéro la semaine dernière.'),
    (4310, 0, 'Bonjour Colette ! Oui bien sûr, ça me fait plaisir d''avoir de vos nouvelles 😊'),
    (4300, 1, 'Je voulais vous envoyer la recette du clafoutis. Vous l''avez trouvé bon ?'),
    (4295, 0, 'Délicieux !'),
    (4294, 0, 'Je veux bien la recette, oui'),
    (4200, 1, 'Alors : 500 g de cerises, 3 œufs, 100 g de sucre, 60 g de farine, 25 cl de lait, une pincée de sel. Four à 180 °C, 35 min. Ne dénoyautez pas les cerises !'),
    (4190, 0, 'Pourquoi pas les dénoyauter ?'),
    (4180, 1, 'Les noyaux donnent un petit goût d''amande. C''est le secret de ma mère.'),
    (4170, 0, 'Je note, merci beaucoup !'),
    (1500, 1, 'Vous l''avez essayé ?'),
    (1440, 0, 'Pas encore, je n''ai pas trouvé de cerises au marché ce matin'),
    (1430, 1, 'Ah, c''est la fin de la saison. Prenez des cerises surgelées, ça marche aussi très bien. Ou des pommes, mais alors ça ne s''appelle plus un clafoutis !'),
    (1425, 0, 'Ça s''appelle comment alors ?'),
    (1420, 1, 'Une flaugnarde 😉'),
    (1418, 0, 'Haha, j''apprends des choses'),
    (300,  1, 'Il y a un atelier jeudi prochain à 14h, vous venez ?'),
    (290,  0, 'Oui !'),
    (289,  0, 'Je dois apporter quelque chose ?'),
    (280,  1, 'Un tablier et un torchon. Et votre bonne humeur.'),
    (275,  0, 'Ça, c''est facile 😄'),
    (60,   1, 'Au fait, pourriez-vous me rappeler votre adresse e-mail ? Mon petit-fils veut m''installer une messagerie sur la tablette, mais je n''y comprends rien.'),
    (55,   0, 'Bien sûr, je vous l''envoie juste après'),
    (50,   1, 'Merci, vous êtes un amour'),
    (5,    0, 'À jeudi Colette !'),
    (1,    1, '👍')
)
INSERT INTO messages (contact_id, is_incoming, created_at, content)
SELECT 1, is_incoming, strftime('%s','now') * 1000 - minutes_ago * 60000, content
FROM m;

COMMIT;
