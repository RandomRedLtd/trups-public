CREATE TYPE role_name AS ENUM ('ROLE_ADMIN', 'ROLE_USER');

CREATE TYPE ml_model_type AS ENUM (
    'LOGISTIC_REGRESSION',
    'LINEAR_REGRESSION',
    'DECISION_TREE_CLASSIFIER',
    'DECISION_TREE_REGRESSOR'
    );

CREATE TYPE ml_model_status AS ENUM (
    'IN_QUEUE',
    'TRAINING_IN_PROGRESS',
    'TRAINING_SUCCESS',
    'TRAINING_FAILURE'
    );

CREATE TABLE public.app_user (
    id integer UNIQUE GENERATED ALWAYS AS IDENTITY,
    username text UNIQUE NOT NULL,
    password text UNIQUE NOT NULL,
    role role_name NOT NULL,
    enabled bool NOT NULL DEFAULT false,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    deleted bool NOT NULL DEFAULT false
);

CREATE TABLE public.ml_model (
    id int4 UNIQUE GENERATED ALWAYS AS IDENTITY,
    owner_id int4 NOT NULL REFERENCES app_user(id),
    name text NOT NULL,
    description text,
    enabled bool NOT NULL DEFAULT true,
    public_access bool NOT NULL DEFAULT true,
    type ml_model_type NOT NULL,
    status ml_model_status NOT NULL,
    score float4,
    input_fields jsonb,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    deleted bool NOT NULL DEFAULT false
);

CREATE TABLE public.ml_model_access (
    app_user_id int4 NOT NULL REFERENCES app_user(id),
    ml_model_id int4 NOT NULL references ml_model(id),
    PRIMARY KEY (app_user_id, ml_model_id),
    enabled bool NOT NULL DEFAULT true,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    deleted bool NOT NULL DEFAULT false
);

COMMIT;
