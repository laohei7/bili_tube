package com.laohei.bili_tube.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.laohei.bili_sdk.model_v2.common.Dimension
import com.laohei.bili_sdk.model_v2.video.AreaModel
import com.laohei.bili_sdk.model_v2.video.BangumiDetailModel
import com.laohei.bili_sdk.model_v2.video.BangumiStat
import com.laohei.bili_sdk.model_v2.video.EpisodeModel
import com.laohei.bili_sdk.model_v2.video.NewEpModel
import com.laohei.bili_sdk.model_v2.video.PublishModel
import com.laohei.bili_sdk.model_v2.video.RatingModel
import com.laohei.bili_sdk.model_v2.video.SeasonModel
import com.laohei.bili_sdk.model_v2.video.SeasonStat
import com.laohei.bili_sdk.model_v2.video.SkipModel
import com.laohei.bili_sdk.model_v2.video.VideoCard
import com.laohei.bili_sdk.model_v2.video.VideoCardContent
import com.laohei.bili_sdk.model_v2.video.VideoDetailModel
import com.laohei.bili_sdk.model_v2.video.VideoDimension
import com.laohei.bili_sdk.model_v2.video.VideoOwner
import com.laohei.bili_sdk.model_v2.video.VideoStat
import com.laohei.bili_sdk.model_v2.video.VideoTagItem
import com.laohei.bili_sdk.model_v2.video.VideoView
import com.laohei.bili_tube.model.play.PlayParam
import com.laohei.bili_tube.features.player.MediaPlayerUIState
import com.laohei.bili_tube.features.player.state.media.MediaState
import com.laohei.bili_tube.features.player.state.screen.ScreenState

class FakePlayerState :
    PreviewParameterProvider<Triple<MediaPlayerUIState, MediaState, ScreenState>> {
    override val values: Sequence<Triple<MediaPlayerUIState, MediaState, ScreenState>>
        get() = sequenceOf(
            Triple(
                MediaPlayerUIState(
                    playParam = PlayParam.VideoParam(bvid = "video"),
                    videoDetail = VideoDetailFakeData.fakeVideoDetail()
                ),
                MediaState(),
                ScreenState(
                    screenHeight = 1080,
                    screenWidth = 1920,
                )
            ),
            Triple(
                MediaPlayerUIState(
                    playParam = PlayParam.BangumiParam(bvid = "bangumi"),
                    bangumiDetail = BangumiFakeData.fakeBangumiDetail()
                ),
                MediaState(),
                ScreenState(
                    screenHeight = 1080,
                    screenWidth = 1920
                )
            )
        )
}

object VideoDetailFakeData {

    fun fakeVideoDetail(): VideoDetailModel = VideoDetailModel(
        view = fakeVideoView(),
        card = fakeVideoCard(),
        tags = listOf(fakeTagItem(), fakeTagItem(tagId = 2L, tagName = "搞笑")),
        related = listOf(
            fakeVideoView("BV111", "相关视频1"),
            fakeVideoView("BV222", "相关视频2")
        )
    )

    fun fakeVideoView(
        bvid: String = "BV1xx411c7mD",
        title: String = "测试视频标题"
    ): VideoView = VideoView(
        bvid = bvid,
        aid = 12345678L,
        cid = 87654321L,
        pic = "https://placehold.co/600x400",
        title = title,
        pubdate = System.currentTimeMillis() / 1000,
        ctime = System.currentTimeMillis() / 1000,
        desc = "这是一个测试用的视频简介，用于UI预览。",
        duration = 120L,
        owner = fakeOwner(),
        stat = fakeStat(),
        dimension = fakeDimension(),
        seasonId = null
    )

    fun fakeOwner(): VideoOwner = VideoOwner(
        mid = 1001L,
        name = "测试用户",
        face = "https://placehold.co/100x100"
    )

    fun fakeStat(): VideoStat = VideoStat(
        aid = 12345678L,
        view = 98765L,
        danmaku = 1234L,
        reply = 567L,
        favorite = 321L,
        coin = 888L,
        share = 99L,
        like = 1000L,
        dislike = 10L
    )

    fun fakeDimension(): VideoDimension = VideoDimension(
        width = 1920,
        height = 1080,
        rotate = 0
    )

    fun fakeVideoCard(): VideoCard = VideoCard(
        card = fakeCardContent(),
        likeNum = 500L
    )

    fun fakeCardContent(): VideoCardContent = VideoCardContent(
        mid = "1001",
        name = "测试UP主",
        sex = "男",
        rank = "10000",
        face = "https://placehold.co/100x100",
        fans = 123456,
        friend = 50,
        attention = 10
    )

    fun fakeTagItem(
        tagId: Long = 1L,
        tagName: String = "测试标签"
    ): VideoTagItem = VideoTagItem(
        tagId = tagId,
        tagName = tagName,
        musicId = "music123",
        tagType = "general",
        jumpUrl = "https://www.bilibili.com"
    )
}

object BangumiFakeData {

    fun fakeBangumiDetail(): BangumiDetailModel = BangumiDetailModel(
        actors = "配音演员A / 配音演员B",
        cover = "https://placehold.co/600x800",
        evaluate = "这是一部非常精彩的番剧，剧情紧凑，人物鲜明。",
        mediaId = 10001,
        mode = 1,
        record = "制作公司：B站动画工作室",
        seasonId = 2001,
        seasonTitle = "测试季度1",
        staff = "导演：测试导演 / 编剧：测试编剧",
        stat = fakeBangumiStat(),
        styles = listOf("热血", "冒险", "奇幻"),
        subtitle = "副标题示例",
        title = "测试番剧标题",
        total = 12,
        areas = listOf(
            AreaModel(1, "中国"),
            AreaModel(2, "日本")
        ),
        episodes = listOf(
            fakeEpisode(epId = 101, title = "第一集 少年出发", index = "01"),
            fakeEpisode(epId = 102, title = "第二集 奇遇伙伴", index = "02")
        ),
        publish = fakePublish(),
        rating = RatingModel(
            count = 10000,
            score = 9.2f
        ),
        seasons = listOf(
            fakeSeason(2001, "第一季"),
            fakeSeason(2002, "第二季")
        )
    )

    fun fakeBangumiStat(): BangumiStat = BangumiStat(
        views = 1234567,
        vt = 10000,
        danmakus = 54321,
        reply = 1234,
        favorite = 2222,
        favorites = 3333,
        coins = 444,
        share = 555,
        likes = 9999,
        followText = "100万人追番"
    )

    fun fakeEpisode(
        epId: Long,
        title: String,
        index: String
    ): EpisodeModel = EpisodeModel(
        aid = 12345678L + epId,
        bvid = "BV${epId}abc",
        cid = 87654321L + epId,
        cover = "https://placehold.co/320x180",
        dimension = Dimension(width = 1920, height = 1080, rotate = 0),
        duration = 1500,
        epId = epId,
        from = "bangumi",
        id = epId,
        longTitle = "$title - 完整标题",
        pubTime = System.currentTimeMillis() / 1000,
        shareUrl = "https://www.bilibili.com/bangumi/play/ep$epId",
        shortLink = "https://b23.tv/ep$epId",
        showTitle = "第${index}集",
        skip = mapOf(
            "op" to SkipModel(start = 0, end = 90),
            "ed" to SkipModel(start = 1400, end = 1500)
        ),
        subtitle = "本集副标题",
        title = title,
        vid = "VID$epId"
    )

    fun fakePublish(): PublishModel = PublishModel(
        isFinish = 0,
        isStarted = 1,
        pubTime = "2023-01-01 12:00:00",
        pubTimeShow = "2023年1月1日开播",
        weekday = 7
    )

    fun fakeSeason(seasonId: Long, title: String): SeasonModel = SeasonModel(
        cover = "https://placehold.co/400x600",
        mediaId = 1000 + seasonId,
        newEp = NewEpModel(
            cover = "https://placehold.co/200x100",
            id = 9999,
            indexShow = "更新至第12集"
        ),
        seasonId = seasonId,
        seasonTitle = title,
        stat = SeasonStat(
            views = 100000,
            vt = 200,
            favorites = 5000,
            seriesFollow = 3000
        )
    )
}

