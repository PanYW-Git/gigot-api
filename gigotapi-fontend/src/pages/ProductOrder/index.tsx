import { productPayTypeList, productStatusList } from '@/enum/ProductOrderEnum';
import {
  deleteByIdProductOrderUsingPost,
  listMyProductOrderVoByPageUsingPost, updateStatusByIdUsingPost,
} from '@/services/gigotapi-backend/productOrderController';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Link } from '@umijs/max';
import { Drawer, message, Popconfirm } from 'antd';
import { SortOrder } from 'antd/lib/table/interface';
import React, { useRef, useState } from 'react';
import {history} from "@@/core/history";


const ProductOrder: React.FC = () => {
  /**
   * @en-US Pop-up window of new window
   * @zh-CN 新建窗口的弹窗
   *  */
  const [createModalVisiable, handleModalOpen] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalVisible, handleUpdateModalOpen] = useState<boolean>(false);
  const [total, setTotal] = useState<number>();

  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.ProductOrderVO>();
  const [selectedRowsState, setSelectedRows] = useState<API.ProductOrderVO[]>([]);


  const cancelProductOrder = () => {
    const res = updateStatusByIdUsingPost({id: currentRow.id, status: 'CLOSED'});
    if (res) {
      message.success('取消订单成功');
      actionRef.current?.reloadAndRest?.();
    }
  }
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
            }?codeUrl=${record?.codeUrl?.trim()}&total=${record?.total?.trim()}&payType=${record?.payType?.trim()}&expirationTime=${record?.expirationTime}`,
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

  /**
   *  Delete node
   * @zh-CN 删除
   *
   * @param selectedRows
   */
  const handleRemove = async (selectedRows: API.DeleteRequest) => {
    const hide = message.loading('正在删除...');
    if (!selectedRows) return true;
    try {
      const currentId = currentRow?.id;
      await deleteByIdProductOrderUsingPost({ id: currentId });
      hide();
      message.success('删除成功');
      actionRef.current.reload();
      return true;
    } catch (error) {
      hide();
      return false;
    }
  };

  const deleteConfirm = async () => {
    await handleRemove(currentRow as API.DeleteRequest);
  };

  // @ts-ignore
  /**
   * @en-US International configuration
   * @zh-CN 国际化配置
   * */
  const columns: ProColumns<API.ProductOrderVO>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },
    {
      title: '订单名称',
      dataIndex: 'orderName',
      ellipsis: true,
      copyable: true,
      width: 120,
      render: (_, record) => (
        <Link key={record.id} to={`/ProductOrder/info/${record.id}`}>
          {record.orderName}
        </Link>
      ),
    },
    {
      title: '订单号',
      dataIndex: 'orderNo',
      valueType: 'text',
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },

    {
      title: '二维码地址',
      dataIndex: 'codeUrl',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },
    {
      title: '创建人',
      dataIndex: 'userId',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },
    {
      title: '商品id',
      dataIndex: 'productId',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },

    {
      title: '金额',
      dataIndex: 'total',
      valueType: 'text',
      render: (text, record) => {
        return '￥ ' + parseFloat(text) / 100;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueEnum: productStatusList,
    },
    {
      title: '支付方式',
      dataIndex: 'payType',
      valueEnum: productPayTypeList,
    },
    {
      title: '商品信息',
      dataIndex: 'productInfo',
      hideInTable: true,
      hideInSearch: true,
      hideInForm: true,
      valueType: 'text',
    },
    {
      title: '支付宝formData',
      dataIndex: 'formData',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },
    {
      title: '增加金币个数',
      dataIndex: 'addGoldCoin',
      valueType: 'text',
      render: (text, record) => {
        return '🪙 ' + text;
      },
    },
    {
      title: '过期时间',
      dataIndex: 'expirationTime',
      valueType: 'dateTime',
      search: false,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        record.status === 'NOTPAY' ? (
          <>
            <a
              onClick={() => {
                // 跳转付款
                toPay(record);
              }}
            >
              付款
            </a>
            <Popconfirm
                key={'CLOSED'}
                title="请确认是否取消该订单!"
                onConfirm={cancelProductOrder}
                okText="Yes"
                cancelText="No"
            >
              <a
                  key="CancelLink"
                  onClick={async () => {
                    setCurrentRow(record);
                  }}
              >
                取消
              </a>
            </Popconfirm>
          </>
        ) : record.status === 'SUCCESS' || record.status === 'CLOSED' ? (
          <a
            onClick={() => {
              // todo 取消订单
              setCurrentRow(record);
              history.push('/ProductOrder/info/' + record.id);

            }}
          >
            查看
          </a>
        ) : (
          <></>
        ),
        <Popconfirm
          key={'Delete'}
          title="请确认是否删除该订单!"
          onConfirm={deleteConfirm}
          okText="Yes"
          cancelText="No"
        >
          <a
            key="SUCCESS"
            style={{ color: 'red' }}
            onClick={async () => {
              setCurrentRow(record);
            }}
          >
            删除
          </a>
        </Popconfirm>,
      ],
    },
  ];
  return (
    <PageContainer>
      <ProTable<API.RuleListItem, API.PageParams>
        headerTitle={''}
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => []}
        /*
                                                                                                  分页查询后端数据，这个地方是一个组件的托管，不管是刷新还是点击查询都是调用这个
                                                                                                  这儿我们直接调用的返回参数和他不一致，因此需要重写返回参数
                                                                                                */
        request={async (
          params,
          sort: Record<string, SortOrder>,
          filter: Record<string, React.ReactText[] | null>,
        ) => {
          const res: any = await listMyProductOrderVoByPageUsingPost({
            ...params,
          });
          if (res?.data) {
            return {
              data: res?.data.records || [],
              success: true,
              total: res?.data.total || 0,
            };
          } else {
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />

      {/*{selectedRowsState?.length > 0 && (*/}
      {/*  <FooterToolbar*/}
      {/*    extra={*/}
      {/*      <div>*/}
      {/*        已选择{' '}*/}
      {/*        <a*/}
      {/*          style={{*/}
      {/*            fontWeight: 600,*/}
      {/*          }}*/}
      {/*        >*/}
      {/*          {selectedRowsState.length}*/}
      {/*        </a>{' '}*/}
      {/*        项 &nbsp;&nbsp;*/}
      {/*        <span>*/}
      {/*          服务调用次数总计 {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)} 万*/}
      {/*        </span>*/}
      {/*      </div>*/}
      {/*    }*/}
      {/*  >*/}
      {/*    <Button*/}
      {/*      onClick={async () => {*/}
      {/*        await handleRemove(selectedRowsState);*/}
      {/*        setSelectedRows([]);*/}
      {/*        actionRef.current?.reloadAndRest?.();*/}
      {/*      }}*/}
      {/*    >*/}
      {/*      批量删除*/}
      {/*    </Button>*/}
      {/*  </FooterToolbar>*/}
      {/*)}*/}

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<API.RuleListItem>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};
export default ProductOrder;
