declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseInterfaceInfoVO_ = {
    code?: number;
    data?: InterfaceInfoVO;
    message?: string;
  };

  type BaseResponseIntroduceRowVO_ = {
    code?: number;
    data?: IntroduceRowVO;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLoginXcxQrVO_ = {
    code?: number;
    data?: LoginXcxQrVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponsePageInterfaceInfoVO_ = {
    code?: number;
    data?: PageInterfaceInfoVO_;
    message?: string;
  };

  type BaseResponsePageProductInfoVO_ = {
    code?: number;
    data?: PageProductInfoVO_;
    message?: string;
  };

  type BaseResponsePageProductOrderVO_ = {
    code?: number;
    data?: PageProductOrderVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserInterfaceInfoVO_ = {
    code?: number;
    data?: PageUserInterfaceInfoVO_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponseProductInfoVO_ = {
    code?: number;
    data?: ProductInfoVO;
    message?: string;
  };

  type BaseResponseProductOrderVO_ = {
    code?: number;
    data?: ProductOrderVO;
    message?: string;
  };

  type BaseResponseProportionSalesVO_ = {
    code?: number;
    data?: ProportionSalesVO;
    message?: string;
  };

  type BaseResponseSalesCardVO_ = {
    code?: number;
    data?: SalesCardVO;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseTopInterfaceInfoVO_ = {
    code?: number;
    data?: TopInterfaceInfoVO;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserInterfaceInfoVO_ = {
    code?: number;
    data?: UserInterfaceInfoVO;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type BaseResponseUserVoucherVO_ = {
    code?: number;
    data?: UserVoucherVO;
    message?: string;
  };

  type checkUsingGETParams = {
    /** echostr */
    echostr?: string;
    /** nonce */
    nonce?: string;
    /** signature */
    signature?: string;
    /** timestamp */
    timestamp?: string;
  };

  type DeleteListRequest = {
    ids?: string[];
  };

  type DeleteRequest = {
    id?: string;
  };

  type downloadAvatarUsingGETParams = {
    /** name */
    name: string;
  };

  type downloadQRCodeUsingGETParams = {
    /** name */
    name: string;
  };

  type getInterfaceInfoVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type getProductInfoVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type getProductOrderVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type getUserInterfaceInfoVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: string;
  };

  type IdRequest = {
    id?: string;
  };

  type InterfaceInfoAddRequest = {
    createTime?: string;
    description?: string;
    id?: string;
    isDelete?: number;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    updateTime?: string;
    url?: string;
    userId?: string;
  };

  type InterfaceInfoEditRequest = {
    description?: string;
    id?: string;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    url?: string;
  };

  type InterfaceInfoInvokeRequest = {
    id?: string;
    requestParams?: Record<string, any>;
  };

  type InterfaceInfoProportion = {
    name?: string;
    ratio?: number;
  };

  type InterfaceInfoQueryRequest = {
    current?: string;
    description?: string;
    id?: string;
    liked?: number;
    method?: string;
    name?: string;
    pageSize?: string;
    payGoldCoin?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalInvokes?: string;
    url?: string;
    userId?: string;
  };

  type InterfaceInfoTotalCountVO = {
    id?: string;
    name?: string;
    totalInvokes?: string;
  };

  type InterfaceInfoUpdateRequest = {
    createTime?: string;
    description?: string;
    id?: string;
    isDelete?: number;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    updateTime?: string;
    url?: string;
    userId?: string;
  };

  type InterfaceInfoVO = {
    createTime?: string;
    description?: string;
    id?: string;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: string;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    updateTime?: string;
    url?: string;
    userId?: string;
  };

  type InterfaceLogWeekCount = {
    count?: string;
    day?: string;
  };

  type IntroduceRowVO = {
    cost?: number;
    dayOverDay?: string;
    dayTotal?: string;
    interfaceInfoCount?: string;
    noPayCount?: string;
    onLineUserCount?: string;
    pv?: string;
    successPayCount?: string;
    sucessTotalAmount?: string;
    weekOverWeek?: string;
  };

  type LoginUserVO = {
    balanceGoldCoin?: string;
    createTime?: string;
    email?: string;
    id?: string;
    phone?: string;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type LoginXcxQrVO = {
    qrName?: string;
    scene?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageInterfaceInfoVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: InterfaceInfoVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageProductInfoVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: ProductInfoVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageProductOrderVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: ProductOrderVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageUser_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: User[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageUserInterfaceInfoVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: UserInterfaceInfoVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: string;
    maxLimit?: string;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: string;
    records?: UserVO[];
    searchCount?: boolean;
    size?: string;
    total?: string;
  };

  type PayCreateOrderRequest = {
    payType?: string;
    productId?: string;
  };

  type ProductInfo = {
    addGoldCoin?: string;
    createTime?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    isDelete?: number;
    name?: string;
    productType?: string;
    status?: number;
    total?: string;
    updateTime?: string;
    userId?: string;
  };

  type ProductInfoAddRequest = {
    addGoldCoin?: string;
    current?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    name?: string;
    pageSize?: string;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    total?: string;
    userId?: string;
  };

  type ProductInfoEditRequest = {
    addGoldCoin?: string;
    current?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    name?: string;
    pageSize?: string;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    total?: string;
    userId?: string;
  };

  type ProductInfoQueryRequest = {
    addGoldCoin?: string;
    current?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    name?: string;
    pageSize?: string;
    productType?: string;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    total?: string;
    userId?: string;
  };

  type ProductInfoUpdateRequest = {
    addGoldCoin?: string;
    createTime?: string;
    current?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    isDelete?: number;
    name?: string;
    pageSize?: string;
    productType?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    total?: string;
    updateTime?: string;
    userId?: string;
  };

  type ProductInfoVO = {
    addGoldCoin?: string;
    description?: string;
    expirationTime?: string;
    id?: string;
    name?: string;
    productType?: string;
    status?: number;
    total?: string;
    userId?: string;
  };

  type ProductOrderPayoutRank = {
    payOutRank?: string;
    total?: string;
    userName?: string;
  };

  type ProductOrderQueryRequest = {
    addGoldCoin?: string;
    codeUrl?: string;
    createTime?: string;
    current?: string;
    expirationTime?: string;
    formData?: string;
    id?: string;
    orderName?: string;
    orderNo?: string;
    pageSize?: string;
    payType?: string;
    productId?: string;
    productInfo?: string;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: string;
    total?: string;
    updateTime?: string;
    userId?: string;
  };

  type ProductOrderTotalDay = {
    day?: string;
    total?: string;
  };

  type ProductOrderUpdateStatusRequest = {
    id?: string;
    status?: string;
  };

  type ProductOrderVO = {
    addGoldCoin?: string;
    codeUrl?: string;
    createTime?: string;
    expirationTime?: string;
    formData?: string;
    id?: string;
    orderName?: string;
    orderNo?: string;
    payType?: string;
    productId?: string;
    productInfo?: ProductInfo;
    status?: string;
    total?: string;
    updateTime?: string;
    userId?: string;
  };

  type ProportionSalesVO = {
    interfaceInfoProportionList?: InterfaceInfoProportion[];
  };

  type SalesCardVO = {
    payoutRank?: ProductOrderPayoutRank[];
    totalDay?: ProductOrderTotalDay[];
  };

  type TopInterfaceInfoVO = {
    interfaceInfoTotalCount?: InterfaceInfoTotalCountVO[];
    interfaceLogWeekCounts?: InterfaceLogWeekCount[];
    mostPopular?: string;
  };

  type uploadFileForTencentCosUsingPOSTParams = {
    biz?: string;
  };

  type User = {
    balanceGoldCoin?: string;
    createTime?: string;
    email?: string;
    id?: string;
    isDelete?: number;
    mpOpenId?: string;
    phone?: string;
    secretId?: string;
    secretKey?: string;
    unionId?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserBindEmailRequest = {
    email?: string;
    verificationCode?: string;
  };

  type UserInterfaceInfoAddRequest = {
    createTime?: string;
    id?: string;
    interfaceId?: string;
    isDelete?: number;
    status?: number;
    totalInvokes?: string;
    updateTime?: string;
    userId?: string;
  };

  type UserInterfaceInfoEditRequest = {
    description?: string;
    id?: string;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: number;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    url?: string;
  };

  type UserInterfaceInfoQueryRequest = {
    current?: string;
    id?: string;
    interfaceId?: string;
    pageSize?: string;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    totalInvokes?: string;
    userId?: string;
  };

  type UserInterfaceInfoUpdateRequest = {
    id?: string;
    interfaceId?: string;
    status?: number;
    totalInvokes?: string;
    userId?: string;
  };

  type UserInterfaceInfoVO = {
    createTime?: string;
    description?: string;
    id?: string;
    liked?: number;
    method?: string;
    name?: string;
    payGoldCoin?: number;
    requestExample?: string;
    requestHeader?: string;
    requestParams?: string;
    responseHeader?: string;
    responseParams?: string;
    returnFormat?: string;
    status?: number;
    totalInvokes?: string;
    updateTime?: string;
    url?: string;
    userId?: string;
  };

  type UserLoginByEmailRequest = {
    email?: string;
    verificationCode?: string;
  };

  type userLoginByWxOpenUsingGETParams = {
    /** code */
    code: string;
  };

  type UserLoginByXcxRequest = {
    code?: string;
    scene?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserLoginXcxCheckRequest = {
    scene?: string;
  };

  type UserNameUpdateRequest = {
    id?: string;
    userName?: string;
  };

  type UserQueryRequest = {
    balanceGoldCoin?: string;
    current?: string;
    id?: string;
    mpOpenId?: string;
    pageSize?: string;
    sortField?: string;
    sortOrder?: string;
    unionId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserSendEmailRequest = {
    email?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdatePasswordRequest = {
    checkNewPassword?: string;
    newPassword?: string;
    oldPassword?: string;
  };

  type UserUpdateRequest = {
    id?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    balanceGoldCoin?: string;
    createTime?: string;
    email?: string;
    id?: string;
    phone?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVoucherVO = {
    id?: string;
    secretId?: string;
    secretKey?: string;
  };
}
