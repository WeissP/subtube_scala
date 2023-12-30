package com.jb.programs

import cats.effect.kernel.{Async, Clock}
import cats.syntax.all.*
import cats.{Applicative, Functor, Monad, MonadThrow}
import com.jb.algebras.{Algebras, YoutubeQueryAlg}
import com.jb.domain.*
import io.github.iltotore.iron.*
import org.typelevel.log4cats.Logger

final case class GetChannelInfo[F[_]: Async: MonadThrow: Functor](
    yqa: YoutubeQueryAlg[F],
) {
  def channelInfo(id: YtbChannelID): F[ChannelInfo] = {
    for {
      ch <- yqa.channelInfo(id)
      c <- CachedAt.now[F]
    } yield ChannelInfo(ch, ChannelMeta(c, None))
  }
}

final case class FetchCacheVideos[F[_]: Async: MonadThrow: Functor: Logger](
    yqa: YoutubeQueryAlg[F],
) {
  def videosByChannel(
      id: YtbChannelID,
      order: MediaSortOrder,
      cacheRefreshThreshold: UnixTS,
      pag: Pagination,
  ): F[List[MediaInfo[YoutubeMedia]]] = {
    for {
      videos <- yqa.videosByChannel(id, order, pag.limit + pag.offset)
      _ <- Logger[F].info("fetched")
      c <- CachedAt.now[F]
    } yield videos
      .drop(pag.offset)
      .take(pag.limit)
      .map(
        MediaInfo(_, MediaMeta(MediaID(0), MediaSource.Youtube, c, None)),
      )
  }

  def videosByTag(
      tag: MediaTag,
      cacheRefreshThreshold: UnixTS,
      pag: Pagination,
      mediaSources: List[MediaSource],
      taggingMethods: List[TaggingMethod],
  ): F[List[MediaInfo[YoutubeMedia]]] = {
    if (
      mediaSources.contains(MediaSource.Youtube)
      && taggingMethods.contains(TaggingMethod.YoutubeChannel)
    ) {
      for {
        chai <- videosByChannel(
          YtbChannelID("UCjuNibFJ21MiSNpu8LZyV4w"),
          MediaSortOrder.Newest,
          cacheRefreshThreshold,
          pag,
        )
        uncle <- videosByChannel(
          YtbChannelID("UCVjlpEjEY9GpksqbEesJnNA"),
          MediaSortOrder.Newest,
          cacheRefreshThreshold,
          pag,
        )
      } yield (chai ++ uncle).sortBy(-_.media.published.value).take(pag.limit)

    } else {
      List().pure
    }
  }
}

sealed abstract class Programs[F[_]: Async: Logger] private (
    algebras: Algebras[F],
) {
  val getChannelInfo: GetChannelInfo[F] = GetChannelInfo(algebras.youtubeQueryAlg)
  val fetchCacheVideos: FetchCacheVideos[F] = FetchCacheVideos(
    algebras.youtubeQueryAlg,
  )
}

object Programs {
  def make[F[_]: Async: Logger](
      algebras: Algebras[F],
  ): Programs[F] =
    new Programs[F](algebras) {}
}
