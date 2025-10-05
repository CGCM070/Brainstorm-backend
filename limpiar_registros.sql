-- Recorre todas las tablas en el esquema público y las trunca, reiniciando las identidades y cascada.
-- Luego, recorre todas las secuencias en el esquema público y las reinicia a 1.
DO $$
    DECLARE
rec RECORD;
BEGIN
FOR rec IN
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename NOT LIKE 'pg_%'
  AND tablename NOT LIKE 'sql_%'
    LOOP
                EXECUTE 'TRUNCATE TABLE public.' || quote_ident(rec.tablename) || ' RESTART IDENTITY CASCADE';
END LOOP;
END;
$$;



DO $$
    DECLARE
rec RECORD;
BEGIN
FOR rec IN
SELECT sequence_name
FROM information_schema.sequences
WHERE sequence_schema = 'public'
    LOOP
                EXECUTE 'ALTER SEQUENCE public.' || quote_ident(rec.sequence_name) || ' RESTART WITH 1';
END LOOP;
END;
$$;
