import { productPayTypeList, productStatusList } from '@/enum/ProductOrderEnum';
import {
  deleteByIdProductOrderUsingPost,
  getProductOrderVoByIdUsingGet,
  updateStatusByIdUsingPost,
} from '@/services/gigotapi-backend/productOrderController';
import { history, useParams } from '@@/exports';
import { PageContainer, RouteContext } from '@ant-design/pro-components';
import {Badge, Button, Card, Descriptions, Form, message, Popconfirm, Select, Steps} from 'antd';
import React, { useEffect, useState } from 'react';
import './index.less';
import {Step} from "rc-steps";
import GoldCoin from "@/components/Icon/GoldCoin";
import {valueLength} from "@/pages/User/info";

const { Option } = Select;

const ProductOrderInfo: React.FC = () => {
  // 定义状态和钩子函数
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.ProductOrderVO>();
  const params = useParams();
  const [activeTabKey, setActiveTabKey] = useState<
    'tools' | 'api' | 'errorCode' | 'sampleCode' | string
  >('api');
  const [form] = Form.useForm();
  const [requestExampleActiveTabKey, setRequestExampleActiveTabKey] = useState<string>('javadoc');
  const [stepsCurrent, setStepsCurrent] = useState(1);

  /**
   * 跳转支付
   * @param record
   */
  const toPay = (record: API.ProductOrderVO) => {
    setLoading(true);
    if (record.payType === 'WX') {
      if (!record.codeUrl) {
        message.error('订单获取失败');
        return;
      }
      // 判断是否为手机设备
      const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent,
      );
      if (isMobile) {
        window.location.href = record.codeUrl;
      } else {
        message.loading('正在前往收银台,请稍后.....', 0.6);
        setTimeout(() => {
          history.push(
            `/ProductInfo/pay/${
              record.productId
            }?codeUrl=${record?.codeUrl?.trim()}&total=${record?.total?.trim()}&payType=${record?.payType?.trim()}`,
          );
        }, 800);
      }
    } else {
      message.loading('正在前往收银台,请稍后....');
      setTimeout(() => {
        if (!record.formData) {
          message.error('订单获取失败');
          return;
        }
        document.write(record.formData);
        setLoading(false);
      }, 2000);
    }
  };
  const cancelProductOrder = () => {
    const res = updateStatusByIdUsingPost({ id: data?.id, status: 'CLOSED' });
    if (res) {
      message.success('取消订单成功');
      window.location.reload();
    }
  };

  /**
   *  Delete node
   * @zh-CN 删除
   *
   * @param selectedRows
   */
  const handleRemove = async () => {
    const hide = message.loading('正在删除...');
    if (!data) return true;
    try {
      const currentId = data?.id;
      await deleteByIdProductOrderUsingPost({ id: currentId });
      hide();
      history.push('/ProductOrder');
      message.success('删除成功');
      return true;
    } catch (error) {
      hide();
      return false;
    }
  };

  const loadData = async () => {
    // 初始化数据
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true);
    try {
      const res = await getProductOrderVoByIdUsingGet({
        id: params.id,
      });
      if(res && res.code ===0){
        setData(res.data);
        if(res.data?.status === "NOPAY"){
          setStepsCurrent(1);
        }
        if(res.data?.status === "CLOSED"){
          setStepsCurrent(2);
        }
        if(res.data?.status === "SUCCESS"){
          setStepsCurrent(2);
        }
      }


      // 获取请求参数和响应参数
    } catch (error: any) {
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageContainer
      header={{
        breadcrumb: {},
      }}
    >
      <Card
        title="流程进度"

      >
        <RouteContext.Consumer>
          {({ isMobile }) => (
            <Steps
              direction={isMobile ? 'vertical' : 'horizontal'}
              current={stepsCurrent}
            >
              <Step title="创建订单" description={data?.createTime} />
              <Step title="未支付"/>
              {data?.status != "CLOSED" ? <Step title="完成支付" />
                : <Step status={"error"} title="已关闭"/>
              }
            </Steps>
          )}
        </RouteContext.Consumer>
      </Card>
      <Card>
        {data ? (
          <Descriptions
            title={'订单信息'}
            column={2}
            extra={
              data?.status === 'NOTPAY' ? (
                <>
                  <Button
                    type="primary"
                    style={{ right: 10, background: '#1EBE1F', color: 'white' }}
                    onClick={() => {
                      toPay(data);
                    }}
                  >
                    前往支付
                  </Button>
                  <Popconfirm
                    key={'Closed'}
                    title="请确认是否取消该订单!"
                    onConfirm={cancelProductOrder}
                    okText="Yes"
                    cancelText="No"
                  >
                    <Button danger style={{ right: 5 }}>
                      取消订单
                    </Button>
                  </Popconfirm>
                  <Popconfirm
                      key={'Delete'}
                      title="请确认是否删除该订单!"
                      onConfirm={handleRemove}
                      okText="Yes"
                      cancelText="No"
                  >
                    <Button type='primary' danger>
                      删除订单
                    </Button>
                  </Popconfirm>
                </>
              ) : (
                  <Popconfirm
                      key={'Delete'}
                      title="请确认是否删除该订单!"
                      onConfirm={handleRemove}
                      okText="Yes"
                      cancelText="No"
                  >
                    <Button type='primary' danger>
                      删除订单
                    </Button>
                  </Popconfirm>
              )
            }
          >
            <Descriptions.Item label="订单名称">{data.orderName}</Descriptions.Item>
            <Descriptions.Item label="订单号">{data.orderNo}</Descriptions.Item>
            <Descriptions.Item label="商品描述">{data.productInfo?.description}</Descriptions.Item>
            <Descriptions.Item label="金额">￥ {data.total/ 100} 元</Descriptions.Item>
            <Descriptions.Item label="增加金币">{data.addGoldCoin}个</Descriptions.Item>
            <Descriptions.Item label="支付方式">
              {data && data.payType === 'WX' ? <>{productPayTypeList['WX'].text}</> : null}
              {data && data.payType === 'AliPay' ? <>{productPayTypeList['AliPay'].text}</> : null}
            </Descriptions.Item>
            <Descriptions.Item label="过期时间">{data.expirationTime}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
          </Descriptions>
        ) : (
          <>接口不存在</>
        )}
        <Card type="inner" title={<strong>商品信息</strong>}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <GoldCoin></GoldCoin>
            <div style={{ marginLeft: 10 }}>
              <h3>{data?.productInfo?.name}</h3>
              <h4 style={{ color: '#aaaaaa' }}>
                {valueLength(data?.productInfo?.description)
                  ? data?.productInfo?.description
                  : '暂无商品描述信息'}
              </h4>
            </div>
            <div style={{ marginLeft: '60px', fontSize: '30px', fontWeight: 'bold' }}>x1</div>
          </div>
        </Card>
      </Card>
    </PageContainer>
  );
};

export default ProductOrderInfo;
