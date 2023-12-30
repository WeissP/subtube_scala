CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS
$$
    begin
      NEW.updated_at = now();
      return NEW;
    end;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_updated_at(tablename regclass)
RETURNS void AS
$$
    begin
      execute format('CREATE TRIGGER set_updated_at
                     BEFORE UPDATE
                     ON %s
                     FOR EACH ROW
                     WHEN (OLD is distinct from NEW)
                     EXECUTE FUNCTION set_updated_at();', tablename);
    end;
$$ LANGUAGE plpgsql;

CREATE TABLE tag
(
    tag_id serial PRIMARY KEY,
    tag_name varchar(30) NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"tag"');

CREATE TABLE media
(
    media_id serial PRIMARY KEY,
    introduction text,
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"media"');

CREATE TABLE ytb_channel (
    ytb_channel_id text PRIMARY KEY,
    channel_name text NOT NULL,
    description text NOT NULL,
    sub_count integer NOT NULL,
    introduction text,
    cached_at timestamp NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"ytb_channel"');

CREATE TABLE tag_ytb_channel (
    tag_id int REFERENCES tag (tag_id),
    ytb_channel_id text REFERENCES ytb_channel (ytb_channel_id),
    PRIMARY KEY (tag_id, ytb_channel_id)
);

CREATE TABLE ytb_video (
    ytb_video_id text PRIMARY KEY,
    ytb_channel_id text NOT NULL,
    video_title text NOT NULL,
    video_length integer NOT NULL,
    description text NOT NULL,
    published timestamp NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"ytb_video"');

CREATE TABLE tag_ytb_video (
    tag_id int REFERENCES tag (tag_id),
    ytb_video_id text REFERENCES ytb_video (ytb_video_id),
    PRIMARY KEY (tag_id, ytb_video_id)
);
