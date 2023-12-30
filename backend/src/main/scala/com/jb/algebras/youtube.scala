package com.jb.algebras

import cats.data.NonEmptyList
import com.jb.domain.*

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

trait YoutubeAlg[F[_]] {
  def get(id: MediaID): F[Option[MediaInfo[YoutubeMedia]]]
  def search(
      videoIDs: List[YtbVideoID],
      channelIDs: List[YtbChannelID],
  ): F[List[MediaInfo[YoutubeMedia]]]
  def create(media: MediaInfo[YoutubeMedia]): F[MediaID]
  def update(id: MediaID, update: YtbMediaUpdate): F[Unit]
}

trait YtbChannelAlg[F[_]] {
  def get(id: YtbChannelID): F[Option[ChannelInfo]]
  def create(chan: ChannelInfo): F[Unit]
  def update(id: YtbChannelID, update: YtbChannelUpdate): F[Unit]
}

trait MediaTagAlg[F[_]] {
  def getOrCreateID(title: MediaTag): F[MediaTagID]
  def singleMedias(tag: MediaTagID): F[List[MediaID]]
  def ytbChannels(tag: MediaTagID): F[List[YtbChannelID]]
  def bindMedia(tag: MediaTagID, id: MediaID): F[Unit]
  def bindChannel(tag: MediaTagID, id: YtbChannelID): F[Unit]
}
