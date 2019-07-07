package education.juxin.com.educationpro.http;

/**
 * 所有api后台接口
 * The type Http constant.
 */
public class HttpConstant {
    /**
     * URL基路径
     */
    //private static final String BASE_URL = "http://edu.nightpalace.cn/ld";
    //private static final String BASE_URL = "http://192.168.0.101:9999/ld";
    //private static final String BASE_URL = "http://app.qiantu66.com/ld";
    private static final String BASE_URL = "http://edu.app.qiantu66.com/ld";

    /**
     * 不需上传Token的接口
     */
    // 首页轮播图
    public static final String ABS_BANNER_URL = BASE_URL + "/api/shuffing/list";
    // 精品课程
    public static final String QUALITY_COURSE_URL = BASE_URL + "/api/homecontent/boutiqueList";
    // 名师推荐
    public static final String RECOMMEND_TEACHER_URL = BASE_URL + "/api/homecontent/getTeacherRecommendList";
    // 精品课堂
    public static final String EXCELLENT_CLASS_URL = BASE_URL + "/api/homecontent/getLessonList";
    // 主页的课程分类接口（获取课程分类列表）
    public static final String COURSE_CATEGORY = BASE_URL + "/api/courseClassification/list";
    // 获取课程分类详情数据接口
    public static final String COURSE_DETAILS = BASE_URL + "/api/courseInfo/getById/";
    // 课节信息相关接口(没有分页最多只返回十条课节信息相关接口)
    public static final String COURSE_DETAILS_HISTORY_LIST = BASE_URL + "/api/lessonInfo/historyList";
    // 根据课程id获取课节列表(全部列表)
    public static final String COURSE_DETAILS_ALL_LIST = BASE_URL + "/api/lessonInfo/list";
    // 发送短信验证码
    public static final String SEND_SMS = BASE_URL + "/sms/send/";
    // 用户注册协议接口
    public static final String USER_AGREEMENT = BASE_URL + "/api/registeredAgreement/get";
    // 用户注册接口
    public static final String USER_REGISTER = BASE_URL + "/api/appUserInfo/register";
    // 修改密码接口
    public static final String USER_FORGET_PSW = BASE_URL + "/api/appUserInfo/forgetPassword";
    // 获取礼品列表
    public static final String USER_GIFT_LIST = BASE_URL + "/api/giftInfo/list";
    // 获取礼品详情信息
    public static final String USER_GIFT_DETAIL_INFO = BASE_URL + "/api/giftInfo/get/";
    // 公众号信息相关（轮播接口 ）
    public static final String ACCOUNT_TEACHER = BASE_URL + "/api/publicaccount/getList";
    // 根据老师id获取老师信息
    public static final String TEACHER_BASE_INFO = BASE_URL + "/api/publicaccount/getById/";
    // 获取空间主页信息
    public static final String TEACHER_HOME_PAGE = BASE_URL + "/api/publicaccount/getHomePage";
    // 获取课程集合
    public static final String ALL_COURSE_LIST = BASE_URL + "/api/publicaccount/getCourseList";
    // 根据分类id获取课程列表
    public static final String GET_COURSE_LIST = BASE_URL + "/api/courseClassification/getByIdCourseList/";
    // 获取购买协议信息
    public static final String GET_BUY_PROTOCOL = BASE_URL + "/api/buyagreement/get";
    // 获取安卓最新版本
    public static final String GET_NEW_VERSION = BASE_URL + "/api/androidversioninfo/getNewVersion";
    //搜过课程列表
    public static final String SEARCH_COURSE_LIST = BASE_URL + "/api/search/searchCourse";
    //下一步验证验证码接口
    public static final String NEXT_VERFIY_CODE = BASE_URL + "/api/appUserInfo/nextStepCVerifyCode";
    // 获取视频加密连接地址
    public static final String ENCRYPT_VIDEO_URL = BASE_URL + "/api/qiniu/getVideoUrl";

    /**
     * 需上传Token的接口
     */
    // 用户登录接口
    public static final String USER_LOGIN = BASE_URL + "/api/appUserInfo/login";
    // 用户退出登录接口
    public static final String USER_EXIT_LOGIN = BASE_URL + "/api/appUserInfo/logout";
    // 修改密码(需要token)
    public static final String USER_FORGET_PSW_AUTH = BASE_URL + "/api/appUserInfo/forgetPasswordAuth";
    // 获取个人资料接口
    public static final String GET_USER_PERSONAL_DATA = BASE_URL + "/api/appUserInfo/get";
    // 修改个人资料接口
    public static final String UPDATE_USER_PERSONAL_DATA = BASE_URL + "/api/appUserInfo/modificationUserInfo";
    // 关于我们相关接口（获取关于我们信息）
    public static final String ABOUT_US = BASE_URL + "/api/aboutUsInfo/get";
    // 意见反馈接口
    public static final String USER_FEED_BACK = BASE_URL + "/api/userFeedbackInfo/add";
    // 是否关注接口
    public static final String IS_CATE = BASE_URL + "/api/userConcernInfo/whetherAttention";
    // 关注接口
    public static final String USER_CATE = BASE_URL + "/api/userConcernInfo/affirm";
    // 取消关注
    public static final String USER_CANCEL_CATE = BASE_URL + "/api/userConcernInfo/cancel";
    // 关注列表接口
    public static final String CATE_LIST = BASE_URL + "/api/userConcernInfo/list";
    // 收藏课程接口
    public static final String COURSE_COLLECT = BASE_URL + "/api/collectionInfo/affirm";
    // 用户取消课程收藏
    public static final String COURSE_CANCEL_COLLECT = BASE_URL + "/api/collectionInfo/cancel";
    // 判断用户是否收藏课程
    public static final String IS_COURSE_COLLECT = BASE_URL + "/api/collectionInfo/whethercollect";
    // 收藏列表接口
    public static final String COURSE_COLLECT_LIST = BASE_URL + "/api/collectionInfo/list";
    // 获取开关控制器状态
    public static final String USER_SETTINGS = BASE_URL + "/api/switch/get";
    // 更改开关控制状态
    public static final String UPDATE_STATE_BTN = BASE_URL + "/api/switch/update";
    // 获取动态信息(关注过的老师发布课节)
    public static final String DYNAMIC_LIST = BASE_URL + "/api/dynamic/list";
    // 查询观看记录列表
    public static final String LOOK_COURSE_RECORD = BASE_URL + "/api/appuserlookrecord/list";
    // 保存观看记录
    public static final String LOOK_COURSE_SAVE = BASE_URL + "/api/appuserlookrecord/save";
    // 获取用户的微信账号信息
    public static final String USER_WX_INFO_GET = BASE_URL + "/api/appuserwechatinfo/get";
    // 保存/修改微信用户信息
    public static final String USER_WX_INFO_SAVE = BASE_URL + "/api/appuserwechatinfo/saveOrUpdate";
    // 创建预订单信息
    public static final String CREATE_PRE_ORDER = BASE_URL + "/api/order/create";
    // 获取微信发起支付所需的信息
    public static final String GET_PAY_PARAMS = BASE_URL + "/api/pay/startPay";
    // 获取微信授权所需要的参数
    public static final String GET_WX_PARAMS = BASE_URL + "/api/appuserwechatinfo/getWechatInfo";
    // 获取支付宝账号接口
    public static final String GET_AIPAY_ACCOUNT = BASE_URL + "/api/appuseralipayinfo/get";
    // 修改支付宝账号信息
    public static final String UPDATE_AIPAY = BASE_URL + "/api/appuseralipayinfo/saveOrUpdate";
    // 支付订单列表
    public static final String ORDER_LIST = BASE_URL + "/api/order/list";
    // 清除观看记录
    public static final String CLEAR_LOOK_COURSE_RECORD = BASE_URL + "/api/appuserlookrecord/clear";
    // 获取用户购买课程的列表
    public static final String BUY_COURSE_LIST = BASE_URL + "/api/order/getBuyCourseList";
    // 获取用户账户余额
    public static final String USER_BALANCE = BASE_URL + "/api/appUserInfo/getBalance";
    // 用户提现价设置价格相关接口
    public static final String EXTRACTION_PRICE_LIST = BASE_URL + "/api/extractionprice/list";
    // 用户提现相关接口，用户发起提现
    public static final String START_WITHDRAW_CASH = BASE_URL + "/api/appuserextractionmoney/startPresent";
    // 用户返现记录相关接口，获取用户返现记录列表
    public static final String WITHDRAW_CASH_LIST = BASE_URL + "/api/usersales/list";
    // 用户提现相关接口 获取提现列表
    public static final String WITHDRAW_LIST = BASE_URL + "/api/appuserextractionmoney/list";
    // 获取用户订单打赏列表
    public static final String MY_REWARD_LIST = BASE_URL + "/api/order/getUserGiveOrderList";
    // 获取用户消息的数量
    public static final String GET_MESSAGE_COUNT = BASE_URL + "/api/messageinfo/getNums";
    // 更新消息状态为已读
    public static final String UPDATE_MESSAGE_READ = BASE_URL + "/api/messageinfo/updateState";
    // 查询未读消息列表
    public static final String MESSAGE_COUNT_LIST = BASE_URL + "/api/messageinfo/selectUnreadList";

    /**
     * 其他接口
     */
    // 获取七牛图片上传Token
    public static final String GET_QINIU_TOKEN = BASE_URL + "/api/qiniu/getToken";
}
