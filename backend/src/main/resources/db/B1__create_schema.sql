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

CREATE TABLE tag (
    tag_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    tag_name text UNIQUE NOT NULL,
    introduction text,
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"tag"');

CREATE TABLE ytb_channel (
    media_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ytb_channel_id text UNIQUE NOT NULL,
    channel_name text NOT NULL,
    description text NOT NULL,
    sub_count int4 NOT NULL,
    introduction text,
    cached_at int8 NOT NULL,  -- unix timestamp
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"ytb_channel"');

CREATE TABLE ytb_video (
    media_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    ytb_video_id text UNIQUE NOT NULL,
    ytb_channel_id text NOT NULL,
    video_title text NOT NULL,
    video_length int4 NOT NULL,
    description text NOT NULL,
    published int8 NOT NULL,  -- unix timestamp
    updated_at timestamptz NOT NULL DEFAULT now()
);
SELECT trigger_updated_at('"ytb_video"');

CREATE TYPE taggingmethod AS ENUM ('youtubevideo', 'youtubechannel');

CREATE TABLE tag_media (
    tag_id uuid REFERENCES tag (tag_id),
    media_id uuid NOT NULL,
    tagged_by taggingmethod NOT NULL,
    updated_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (tag_id,media_id,tagged_by)
);
SELECT trigger_updated_at('"tag_media"');
