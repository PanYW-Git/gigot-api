import React, { useEffect, useState } from 'react';
import { Button, Card, message, Spin, Tooltip } from 'antd';
import { history, useModel } from '@umijs/max';
import { PayCircleOutlined } from '@ant-design/icons';
import { listProductInfoVoByPageUsingPost } from '@/services/gigotapi-backend/productInfoController';
import { CheckCard, ProCard } from '@ant-design/pro-components';
import GoldCoin from "@/components/Icon/GoldCoin";

const ProductInfo: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [product, setProduct] = useState<API.ProductInfoVO[]>();
  const { initialState, setInitialState } = useModel('@@initialState');
  const { loginUser } = initialState || {};
  const [total, setTotal] = useState<any>('0.00');
  const [productId, setProductId] = useState<any>('');

  useEffect(() => {
    if (total === '0.00') {
      setProductId('');
    }
  }, [total]);

  const loadData = async () => {
    setLoading(true);
    const res = await listProductInfoVoByPageUsingPost({ current: "1", pageSize: "10" });
    if (res.data && res.code === 0) {
      setProduct(res.data.records || []);
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // @ts-ignore
  return (
    <>
      <Spin spinning={loading}>
        <Card style={{ minWidth: 360 }}>
          <ProCard
            type={'inner'}
            headerBordered
            bordered
            tooltip={'用于平台接口调用'}
            title={<strong>我的钱包</strong>}
          >
            <strong>金币 : </strong>
            <span style={{ color: 'red', fontSize: 18 }}>🪙{loginUser?.balanceGoldCoin}️</span>
          </ProCard>
          <br />
          <Card type={'inner'} title={<strong>积分商城 🪙️</strong>}>
            <ProCard wrap>
              <CheckCard.Group
                onChange={(checkedValue) => {
                  if (!checkedValue) {
                    setTotal('0.00');
                    return;
                  }
                  setTotal(checkedValue);
                }}
              >
                {product &&

                    product.slice().sort((a, b) => parseInt(a.total) - parseInt(b.total)).map((item) => (
                    <CheckCard
                      key={item.id}
                      onClick={() => {
                        setTotal(item.total);
                        setProductId(item.id);
                      }}
                      description={item.description}
                      extra={
                        <>
                          <h3
                            // @ts-ignore
                            style={{
                              color: 'red',
                              fontSize: item.productType === 'EXPERIENCE' ? 16 : 18,
                              fontWeight: 'bold',
                            }}
                          >
                            ￥{item.productType === 'EXPERIENCE' ? '🔥体验 ' : null}
                            {/*// @ts-ignore*/}
                            {item?.total / 100}
                          </h3>
                        </>
                      }
                      // @ts-ignore
                      actions={
                        <>
                          <GoldCoin></GoldCoin>
                        </>
                      }
                      style={{ width: 220, height: 330 }}
                      title={<strong>🪙 {item.addGoldCoin} 金币</strong>}
                      value={item.total}
                    />
                  ))}
              </CheckCard.Group>
            </ProCard>
            <br />
            <ProCard style={{ marginTop: -20 }} layout={'center'}>
              <span>
                本商品为虚拟内容,用于平台接口调用,购买后不支持
                <strong style={{ color: 'red' }}>退换</strong>。确认支付表示您已阅读并接受
                {/*todo 改为自己的用户协议*/}
                <a
                  href={'#'}
                  onClick={() => {
                    window.location.href = '/UserAgreement.html';
                  }}
                >
                  用户协议
                </a>
                ，如付款成功后10分钟后未到账，请联系站长微信：
                <Tooltip
                  placement="bottom"
                  title={<img src={"https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/wechatQRCode.jpg"} alt="微信 code_nav" width="120" />}
                >
                  <a>PYW</a>
                </Tooltip>
              </span>
            </ProCard>
          </Card>
          <br />
          <ProCard bordered headerBordered>
            <div
              style={{
                display: 'flex',
                justifyContent: 'flex-end',
                alignItems: 'center',
                alignContent: 'center',
              }}
            >
              <div style={{ marginRight: '12px', fontWeight: 'bold', fontSize: 18 }}>实付</div>
              <div style={{ marginRight: '20px', fontWeight: 'bold', fontSize: 18, color: 'red' }}>
                ￥ {total / 100} 元
              </div>
              <Button
                style={{ width: 100, padding: 5 }}
                onClick={() => {
                  if (!productId) {
                    message.error('请先选择积分规格哦');
                    return;
                  }
                  // 校验是否已经购买过活动商品
                  message.loading('正在前往收银台,请稍后.....', 0.6);
                  setTimeout(() => {
                    history.push(`/ProductInfo/pay/${productId}`);
                  }, 800);
                }}
                size={'large'}
                type={'primary'}
              >
                立即购买
              </Button>
            </div>
          </ProCard>
        </Card>
      </Spin>
    </>
  );
};

export default ProductInfo;
