
export const productStatusList = {
    'SUCCESS': {
        text: '支付成功',
        status: "success",
    },
    'PAY_ERROR': {
        text: '支付失败',
        status: "error",
    },
    'USER_PAYING': {
        text: '用户支付中',
    },
    'CLOSED': {
        text: '已关闭',
        status: "default",
    },
    'NOTPAY': {
        text: '未支付',
        status: "Processing",
    },
    'REFUND': {
        text: '转入退款',
    },
    'PROCESSING': {
        text: '退款中',
    },
    'REVOKED': {
        text: '已撤销（刷卡支付）',
    },
    'UNKNOW': {
        text: '未知状态',
        status: "default",
    },
};


export const productPayTypeList = {
  'WX': {
    text: '微信支付',
  },
  'AliPay': {
    text: '支付宝支付',
  },
};
