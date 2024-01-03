package com.jb.algebras

import cats.data.NonEmptyList
import cats.effect.*
import cats.syntax.all.*
import com.jb.algebras.youtube.Invidious
import com.jb.config.AppConfig
import com.jb.domain.*
import com.jb.resources.DbPool
import com.jb.resources.AppResources
import org.http4s.client.Client
import org.typelevel.log4cats.Logger
import cats.effect.kernel.Resource
import com.jb.algebras.youtube.YtbChannel
import skunk.Session

trait YoutubeQueryAlg[F[_]] {
  def videosByChannel(
      id: YtbChannelID,
      order: MediaSortOrder,
      minNum: Int,
  ): F[List[YoutubeMedia]]
  def videoInfo(id: YtbVideoID): F[YoutubeMedia]
  def channelInfo(id: YtbChannelID): F[Channel]
  def searchChannels(query: String): F[List[Channel]]
}

trait YtbVideoAlg[F[_]] {
  def byMediaID(id: MediaID): F[Option[YtbVideoEntry]]
  def byYtbVideoID(id: YtbVideoID): F[Option[YtbVideoEntry]]
  def search(
      videoIDs: List[YtbVideoID],
      channelIDs: List[YtbChannelID],
  ): F[List[MediaInfo[YoutubeMedia]]]
  def upsert(media: MediaInfo[YoutubeMedia]): F[MediaID]
}

trait YtbChannelAlg[F[_]] {
  def byChannelID(id: YtbChannelID): F[Option[UUIDEntry[YtbChannelEntry]]]
  def byMediaID(id: MediaID): F[Option[UUIDEntry[YtbChannelEntry]]]
  def upsert(chan: YtbChannelEntry): F[MediaID]
}

trait TagAlg[F[_]] {
  def byName(title: MediaTag): F[Option[TagEntry]]
  def byId(title: MediaTagID): F[Option[TagEntry]]
  def upsert(title: MediaTag, introduction: Option[String]): F[MediaTagID]
}

trait TagMediaAlg[F[_]] {
  def search(
      tags: List[MediaTagID],
      medias: List[MediaID],
      methods: List[TaggingMethod],
  ): F[List[TagMediaEntry]]
  def create(e: TagMediaEntry): F[Unit]
}

case class YtbVideoEntry(mediaID: MediaID, videoInfo: MediaInfo[YoutubeMedia])

sealed abstract class Algebras[F[_]] private (
    val youtubeQueryAlg: YoutubeQueryAlg[F],
    val ytbChannelAlg: YtbChannelAlg[F],
)

object Algebras {
  def make[F[_]: Async: Logger](
      c: Client[F],
      session: DbPool[F],
      config: AppConfig,
  ): Resource[F, Algebras[F]] = {
    (YtbChannel.make(session), YtbChannel.make(session)).parMapN((ytbChannel, _) => {
      new Algebras[F](
        youtubeQueryAlg = Invidious.client[F](c, config.ytb),
        ytbChannelAlg = ytbChannel,
      ) {}
    })
  }
}
