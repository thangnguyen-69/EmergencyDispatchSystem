INSERT INTO ambulance_provider (phone, email, provider_name)
VALUES
  ('555-1234', 'john@example.com', 'John Ambulance'),
  ('555-5678', 'jane@example.com', 'Jane Ambulance'),
  ('555-9012', 'bob@example.com', 'Bob Ambulance'),
  ('555-3456', 'alice@example.com', 'Alice Ambulance'),
  ('555-7890', 'tom@example.com', 'Tom Ambulance');


-- this is long,lat,not lat,long lmao
INSERT INTO ambulance (provider_id, car_number, is_available, location)
VALUES 
  -- (1, 'ABC123', true, ST_GeogFromText('POINT(10.400528 107.119116)')),
  (1, 'ABC123', true, ST_Point(107.115116,10.403528,4326)::geography),

--   (1, 'ABC12341', true, ST_SetSRID(ST_MakePoint(10.400528, 107.119116), 4326)::geography),
  (1, 'ABC12341', true, ST_Point(107.119116,10.400528,4326)::geography),
  (1, 'DEF456', true, ST_GeogFromText('POINT(107.129116 10.450528)')),
  (1, 'GHI789', true, ST_GeogFromText('POINT(30 30)')),
  (2, 'JKL012', true, ST_GeogFromText('POINT(40 40)')),
  (2, 'MNO345', true, ST_GeogFromText('POINT(50 50)'));


INSERT INTO ambulance_user (user_name, address, phone_number)
VALUES
  ('John', '123 Main St', '555-1234'),
  ('Jane', '456 Oak Ave', '555-5678'),
  ('Bob', '789 Elm St', '555-9012'),
  ('Alice', '321 Pine Rd', '555-3456'),
  ('Tom', '654 Maple Dr', '555-7890');