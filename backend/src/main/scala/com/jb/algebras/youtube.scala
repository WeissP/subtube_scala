package com.jb.algebras

import com.jb.domain.*
import cats.data.NonEmptyList

trait YoutubeQueryAlg[F[_]] {
  def videosByChannel(
      id: YtbChannelID,
      cont: Continuation,
      order: MediaSortOrder,
  ): F[List[MediaInfo[YoutubeMedia]]]
  def channelInfo(id: YtbChannelID): F[Channel]
  def searchChannels(query: String): F[List[Channel]]
}

trait YoutubeAlg[F[_]] {
  def get(id: MediaID): F[Option[MediaInfo[YoutubeMedia]]]
  def create(media: MediaInfo[YoutubeMedia]): F[MediaID]
  def update(id: MediaID, update: YtbMediaUpdate): F[Unit]
}

trait YtbChannelAlg[F[_]] {
  def get(id: YtbChannelID): F[Option[ChannelInfo]]
  def create(chan: ChannelInfo): F[Unit]
  def update(id: YtbChannelID, update: YtbChannelUpdate): F[Unit]
  def search(
      order: MediaSortOrder,
      mediaSources: NonEmptyList[MediaSource],
      taggingMethods: NonEmptyList[TaggingMethod],
      pagination: Pagination,
  ): F[List[MediaInfo[YoutubeMedia]]]
}

trait MediaTagAlg[F[_]] {
  def getOrCreateID(title: MediaTag): F[MediaTagID]
  def bindMedia(tag: MediaTagID, id: MediaID): F[Unit]
  def bindChannel(tag: MediaTagID, id: YtbChannelID): F[Unit]
}
