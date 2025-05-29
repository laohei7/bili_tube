package com.laohei.bili_sdk.apis

const val BILIBILI = "https://www.bilibili.com"
const val BILIBILI_API = "https://api.bilibili.com"
const val BILIBILI_PASSPORT = "https://passport.bilibili.com"
const val BILIBILI_APP = "https://app.biliapi.com"

// 风控
const val URL_SPI = "$BILIBILI_API/x/frontend/finger/spi"
const val URL_CAPTCHA = "$BILIBILI_PASSPORT/x/passport-login/captcha"


// 登录
/// 1、扫码登录
const val URL_REQUEST_QRCODE = "$BILIBILI_PASSPORT/x/passport-login/web/qrcode/generate"
const val URL_CHECK_SCAN_STATUS = "$BILIBILI_PASSPORT/x/passport-login/web/qrcode/poll"

/// 2、短信登录
const val URL_COUNTRY_LIST = "$BILIBILI_PASSPORT/web/generic/country/list"
const val URL_SEND_SMS_CODE = "$BILIBILI_PASSPORT/x/passport-login/web/sms/send"
const val URL_SMS_LOGIN = "$BILIBILI_PASSPORT/x/passport-login/web/login/sms"

// 用户
const val URL_USER_UPLOADED_VIDEO = "$BILIBILI_APP/x/v2/space/archive/cursor"
const val URL_USER_INFO_CARD = "$BILIBILI_API/x/web-interface/card"
const val URL_USER_RELATION_MODIFY = "$BILIBILI_API/x/relation/modify"

// 稍后观看
const val URL_TO_VIEW = "$BILIBILI_API/x/v2/history/toview"
const val URL_ADD_TO_VIEW = "$BILIBILI_API/x/v2/history/toview/add"

// 合集
const val URL_FOLDER = "$BILIBILI_API/x/v3/fav/folder/list4navigate"
const val URL_SIMPLE_FOLDER = "$BILIBILI_API/x/v3/fav/folder/created/list-all"
const val URL_FOLDER_DEAL = "$BILIBILI_API/medialist/gateway/coll/resource/deal"
const val URL_FOLDER_RESOURCE_LIST = "$BILIBILI_API/x/v3/fav/resource/list"


const val RECOMMEND_VIDEOS = "https://api.bilibili.com/x/web-interface/wbi/index/top/feed/rcmd"

const val HOT_VIDEOS = "https://api.bilibili.com/x/web-interface/popular"

const val WBI = "https://api.bilibili.com/x/web-interface/nav"

const val VIDEO_PLAY_URL = "https://api.bilibili.com/x/player/wbi/playurl"
const val BANGUMI_PLAY_URL = "https://api.bilibili.com/pgc/player/web/playurl"

const val TIMELINE_URL = "https://api.bilibili.com/pgc/web/timeline"

const val VIDEO_INFO = "https://api.bilibili.com/x/web-interface/wbi/view"

const val VIDEO_DETAIL_URL = "https://api.bilibili.com/x/web-interface/wbi/view/detail"

const val BANGUMI_DETAIL_URL = "https://api.bilibili.com/pgc/view/web/season"

const val BANGUMI_FILTER_URL = "https://api.bilibili.com/pgc/season/index/result"

const val RELATED_BANGUMI_URL = "https://api.bilibili.com/pgc/season/web/related/recommend"

const val USER_PROFILE_URL = "https://api.bilibili.com/x/web-interface/nav"

const val VIDEO_REPLY_URL = "https://api.bilibili.com/x/v2/reply"

const val VIDEO_HEART_BEAT_URL = "https://api.bilibili.com/x/click-interface/web/heartbeat"

const val VIDEO_HISTORY_REPORT = "https://api.bilibili.com/x/v2/history/report"

const val RANK_URL = "https://api.bilibili.com/pgc/web/rank/list"
//?day=3&season_type=1

// 番剧
const val URL_BANGUMI_FILTER = "$BILIBILI_API/pgc/season/index/result"
const val URL_RELATED_BANGUMI = "$BILIBILI_API/pgc/season/web/related/recommend"
const val URL_BANGUMI_TIMELINE = "$BILIBILI_API/pgc/web/timeline"
const val URL_BANGUMI_DETAIL = "$BILIBILI_API/pgc/view/web/season"
const val URL_BANGUMI_PLAY = "$BILIBILI_API/pgc/player/web/playurl"


// 视频
const val URL_RECOMMEND = "$BILIBILI_API/x/web-interface/wbi/index/top/feed/rcmd"
const val URL_HOT = "$BILIBILI_API/x/web-interface/popular"
const val URL_DYNAMIC = "$BILIBILI_API/x/polymer/web-dynamic/v1/feed/all"
const val URL_VIDEO_PLAY = "$BILIBILI_API/x/player/wbi/playurl"
const val URL_VIDEO_DETAIL = "$BILIBILI_API/x/web-interface/wbi/view/detail"

// 状态（点赞、收藏等）
const val URL_HAS_LIKE = "$BILIBILI_API/x/web-interface/archive/has/like"
const val URL_HAS_COIN = "$BILIBILI_API/x/web-interface/archive/coins"
const val URL_HAS_FAVORED = "$BILIBILI_API/x/v2/fav/video/favoured"
const val URL_DEAL_LIKE = "$BILIBILI_API/x/web-interface/archive/like"

// 评论
const val URL_VIDEO_REPLY = "$BILIBILI_API/x/v2/reply"

const val VIDEO_HAS_COIN_URL = "https://api.bilibili.com/x/web-interface/archive/coins"

const val VIDEO_COIN_ADD_URL = "https://api.bilibili.com/x/web-interface/coin/add"

const val VIDEO_HAS_FAVORED_URL = "https://api.bilibili.com/x/v2/fav/video/favoured"

const val VIDEO_PAGELIST_URL = "https://api.bilibili.com/x/player/pagelist"

const val SEARCH_URL = "https://api.bilibili.com/x/web-interface/wbi/search/all/v2"
const val SEARCH_TYPE_URL = "https://api.bilibili.com/x/web-interface/wbi/search/type"

const val USER_INFO_URL = "https://api.bilibili.com/x/space/wbi/acc/info"