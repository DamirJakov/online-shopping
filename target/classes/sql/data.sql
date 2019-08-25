INSERT INTO users (ID, name, surname, username, password, email, role, active) VALUES
  ('1c82e03c-c55b-43de-b65c-d826399d9ae1', 'Ula', 'Wrzesniewska', 'ulka', '$2a$10$4YnNADRCW79p9qifIqTo7euxyrVt6mZet0/PfZWf/SDg750W98aa2', 'u@poczta.pl', 'USER', true),
  ('d6e5552a-5a99-473e-a49b-b8b76d73cb80', 'Andrzej', 'Andrzej', 'andrzejek', '$2a$10$J1SChTfmyHLz1nXDfsoy5euzXZSi5b1GHsMIOjQbQSwdCzcnJxjc2', 'a@poczta.pl', 'USER', true),
  ('ba92a4f8-6360-4e74-96f0-06e2649a3d8c', 'Damir', 'Damir', 'damir', '$2a$10$VZ/l4chvVkXozzsCpc2kh.LB0FOSRLA61YEoOChcnwx6wOxhZmNw6', 'd@poczta.pl', 'ADMIN', true);


INSERT INTO products(ID, name, description, price, owner_id, available,timestamp) values
  ('b0b06b75-e986-46d0-8dc8-6949d9f97531', 'car', 'green Volkswagen TDI', 2000, 'd6e5552a-5a99-473e-a49b-b8b76d73cb80', true,CURRENT_TIMESTAMP),
  ('94652b62-3a7c-46d9-87f1-00cb167077b1', 'computer', 'white Toshiba Satellite', 300, '1c82e03c-c55b-43de-b65c-d826399d9ae1', true,CURRENT_TIMESTAMP),
  ('bf4cbdb1-8a87-4ccb-8f7e-56460470cc89', 'telephone', 'broken Nokia 3310', 10, 'ba92a4f8-6360-4e74-96f0-06e2649a3d8c', true,CURRENT_TIMESTAMP);

