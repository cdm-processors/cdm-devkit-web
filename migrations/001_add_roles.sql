-- +goose Up
INSERT INTO public.t_roles(id, name)
SELECT 1, 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM public.t_roles WHERE id = 1)
UNION ALL
SELECT 2, 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM public.t_roles WHERE id = 2);

-- +goose Down
DELETE FROM public.t_roles WHERE id IN (1, 2);