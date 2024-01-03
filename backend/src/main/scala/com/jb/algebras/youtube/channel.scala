package com.jb.algebras.youtube

import cats.Applicative
import cats.effect.*
import cats.syntax.all.*
import com.jb.algebras.YtbChannelAlg
import com.jb.domain.*
import com.jb.resources.DbPool
import com.jb.domain.given
import com.jb.domain.YtbChannelEntry.given
import io.github.iltotore.iron.skunk.given
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import java.util.UUID
import com.jb.algebras.YtbVideoEntry.given

object YtbChannel {
  private val byChannelID: Query[String, UUIDEntry[YtbChannelEntry]] = sql"""
SELECT media_id,ytb_channel_id,channel_name,description,sub_count,introduction,cached_at
FROM ytb_channel
WHERE ytb_channel_id = $text
""".query(UUIDEntry.codec)

  private val byMediaID: Query[UUID, UUIDEntry[YtbChannelEntry]] = sql"""
SELECT media_id,ytb_channel_id,channel_name,description,sub_count,introduction,cached_at
FROM ytb_channel
WHERE media_id = $uuid
""".query(UUIDEntry.codec)

  private def upsert(using
      insert: Encoder[YtbChannelEntry],
  ): Query[YtbChannelEntry, UUID] = {
    sql"""
INSERT INTO ytb_channel (ytb_channel_id, channel_name, description, sub_count, introduction, cached_at)
VALUES ($insert)
ON CONFLICT (ytb_channel_id)
DO UPDATE SET 
    channel_name = EXCLUDED.channel_name,
    description = EXCLUDED.description,
    sub_count = EXCLUDED.sub_count,
    introduction = EXCLUDED.introduction,
    cached_at = EXCLUDED.cached_at,
    updated_at = now()
RETURNING media_id;
""".query(uuid)
  }

  def make[F[_]: Async](p: DbPool[F]): Resource[F, YtbChannelAlg[F]] = {
    p.flatMap(s => {
      (
        s.prepareR(byChannelID),
        s.prepareR(byMediaID),
        s.prepareR(upsert),
      ).parMapN((pByChannelID, pByMediaID, pUpsert) => {

        new YtbChannelAlg[F] {

          def byChannelID(id: YtbChannelID): F[Option[UUIDEntry[YtbChannelEntry]]] =
            pByChannelID.option(id.value)

          def byMediaID(id: MediaID): F[Option[UUIDEntry[YtbChannelEntry]]] =
            pByMediaID.option(id)

          def upsert(chan: YtbChannelEntry): F[MediaID] = {
            pUpsert.unique(chan)
          }
        }

      })
    })
  }
}
