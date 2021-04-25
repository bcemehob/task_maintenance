CREATE TABLE IF NOT EXISTS public.task
(
    id SERIAL PRIMARY KEY,
    description TEXT COLLATE pg_catalog."default" NOT NULL,
    name VARCHAR(255) COLLATE pg_catalog."default" NOT NULL,
    status VARCHAR(255) COLLATE pg_catalog."default",
    CONSTRAINT uk_lerptdo9d67pejjpbfau899tm UNIQUE (name)
)
    TABLESPACE pg_default;

ALTER TABLE public.task
    OWNER to taskapp_admin;

INSERT INTO public.task(name, description, status) VALUES ('Task #1','Task #1 description', 'AVAILABLE') ON CONFLICT DO NOTHING;
INSERT INTO public.task(name, description, status) VALUES ('Task #2','Task #2 description', 'AVAILABLE') ON CONFLICT DO NOTHING;
INSERT INTO public.task(name, description, status) VALUES ('Task #3','Task #3 description', 'AVAILABLE') ON CONFLICT DO NOTHING;
INSERT INTO public.task(name, description, status) VALUES ('Task #4','Task #4 description', 'AVAILABLE') ON CONFLICT DO NOTHING;
