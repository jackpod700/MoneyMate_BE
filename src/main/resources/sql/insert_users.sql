INSERT INTO user (user_uid, id, pw, name, phone_number, birthday)
VALUES (UNHEX(REPLACE('f47ac10b-58cc-4372-a567-0e02b2c3d479', '-', '')), 'abcd', '$2a$12$nYf0GxWXqvQhWCHN6CYfgODr03CVjiCYtNoJdDHwrHDOhb.35aaky', '홍길동', '010-0000-0000', '2000-01-01');

INSERT INTO user (user_uid, id, pw, name, phone_number, birthday)
VALUES (UNHEX(REPLACE('c9bf9e57-1685-4c89-bafb-ff5af830be8a', '-', '')), 'aaaa', '$2a$12$nYf0GxWXqvQhWCHN6CYfgODr03CVjiCYtNoJdDHwrHDOhb.35aaky', '김철수', '010-1111-1111', '2001-02-02');

INSERT INTO user (user_uid, id, pw, name, phone_number, birthday)
VALUES (UNHEX(REPLACE('a2cb3298-937e-463b-8476-7f0e5fd7e2f1', '-', '')), 'minji', '$2a$12$bdLgqmBsn4UwA6IenvhWeuoomzKR/i9qsGxEwFiSBKjmPAJxm51LC', '최민지', '010-2222-3333', '1999-06-15');

INSERT INTO user (user_uid, id, pw, name, phone_number, birthday)
VALUES (UNHEX(REPLACE('e4d1a9fa-6e60-48db-865a-5cf0b1b07e7e', '-', '')), 'jinho', '$2a$12$YdLlaHKrIh9mdfSInNtytuYNmIHlI5D5k2NFia7a1woP.5tRTqYzO', '박진호', '010-4444-5555', '1998-12-09');

--
-- f47ac10b-58cc-4372-a567-0e02b2c3d479
-- c9bf9e57-1685-4c89-bafb-ff5af830be8a
-- a2cb3298-937e-463b-8476-7f0e5fd7e2f1
-- e4d1a9fa-6e60-48db-865a-5cf0b1b07e7e