import { userRoleList } from '@/enum/userEnum';
import CreateModal from '@/pages/Admin/UserManage/components/CreateModal';
import UpdateModal from '@/pages/Admin/UserManage/components/UpdateModal';
import {
  offlineInterfaceInfoUsingPost,
  onlineInterfaceInfoUsingPost,
} from '@/services/gigotapi-backend/interfaceInfoController';
import {
  addUserUsingPost,
  deleteUserByIdsUsingPost,
  listUserVoByPageUsingPost,
  updateUserUsingPost,
} from '@/services/gigotapi-backend/userController';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import { PageContainer, ProDescriptions, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Button, Drawer, message , Image } from 'antd';
import { SortOrder } from 'antd/lib/table/interface';
import React, { useRef, useState } from 'react';

const UserList: React.FC = () => {
  /**
   * @en-US Pop-up window of new window
   * @zh-CN 新建窗口的弹窗
   *  */
  const [createModalVisiable, handleModalOpen] = useState<boolean>(false);
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalVisible, handleUpdateModalOpen] = useState<boolean>(false);
  const [showDetail, setShowDetail] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.UserVO>();
  const [selectedRowsState, setSelectedRows] = useState<API.UserVO[]>([]);

  /**
   * @en-US Update node
   * @zh-CN 更新用户
   *
   * @param fields
   */
  const handleUpdate = async (fields: API.UserUpdateRequest) => {
    const hide = message.loading('修改中...');
    if (!currentRow) {
      message.success('请选中一列');
      return;
    }
    try {
      // id的类型是index，因此不会包含在fields中，因此需要从选中列中获取
      await updateUserUsingPost({
        id: currentRow.id,
        ...fields,
      });
      hide();
      message.success('修改成功！');
      return true;
    } catch (error) {
      hide();
      message.error('修改失败，请再试一次');
      return false;
    }
  };

  /**
   *  Delete node
   * @zh-CN 删除用户
   *
   * @param selectedRows
   */
  const handleRemove = async (selectedRows: API.DeleteRequest[]) => {
    const hide = message.loading('正在删除...');
    if (!selectedRows) return true;
    try {
      await deleteUserByIdsUsingPost({
        ids: selectedRows.map((row) => row.id),
      });
      hide();
      message.success('删除成功');
      return true;
    } catch (error) {
      hide();
      return false;
    }
  };

  /**
   *  online node
   * @zh-CN 禁用用户
   *
   * @param fields
   */
  const handleOnLine = async (fields: API.IdRequest) => {
    const hide = message.loading('正在发布...');
    if (!currentRow) {
      message.error('未查询到当前行信息');
    }
    try {
      await onlineInterfaceInfoUsingPost({
        id: fields.id,
      });
      hide();
      message.success('发布成功');
      return true;
    } catch (error) {
      hide();
      return false;
    }
  };

  /**
   *  online node
   * @zh-CN 解除禁用用户
   *
   * @param fields
   */
  const handleOffLine = async (fields: API.IdRequest) => {
    const hide = message.loading('正在下线...');
    if (!currentRow) {
      message.error('未查询到当前行信息');
    }
    try {
      await offlineInterfaceInfoUsingPost({
        id: fields.id,
      });
      hide();
      message.success('下线成功');
      return true;
    } catch (error) {
      hide();
      return false;
    }
  };

  /**
   * @en-US Add node
   * @zh-CN 添加用户
   * @param fields
   */
  const handleAdd = async (fields: API.UserAddRequest) => {
    const hide = message.loading('正在添加');
    try {
      const res = await addUserUsingPost({
        ...fields,
      });
      hide();
      if (res.code === 0) {
        message.success('创建成功');
        handleModalOpen(false);
        // 刷新用户信息表单
        location.reload();
        return true;
      }
      return false;
    } catch (error: any) {
      hide();
      message.error('创建失败，' + error.message);
      return false;
    }
  };

  // @ts-ignore
    /**
   * @en-US International configuration
   * @zh-CN 国际化配置
   * */
  const columns: ProColumns<API.UserVO>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'text',
      hideInTable: true,
      hideInForm: true,
      search: false,
    },
    {
      title: '用户账号',
      dataIndex: 'userAccount',
      valueType: 'text',
      hideInTable: true,
      hideInSearch: true,
      formItemProps: {
        rules: [
          {
            required: true,
          },
        ],
      },
    },

    {
      title: '用户昵称',
      dataIndex: 'userName',
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
      title: '头像',
      dataIndex: 'userAvatar',
      search: false,
      hideInForm: true,
      render: (_, record) => (
        <div>
          <Image src={record.userAvatar} width={100} />
        </div>
      ),
    },
    {
      title: '用户简介',
      dataIndex: 'userProfile',
      valueType: 'textarea',
    },
    {
      title: '用户角色',
      dataIndex: 'userRole',
      valueType: 'select',
      valueEnum: userRoleList,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            handleUpdateModalOpen(true);
            setCurrentRow(record);
          }}
        >
          修改
        </a>,
        record.userRole === 'ban' ? (
          <a
            onClick={() => {
              handleOnLine(record);
              setCurrentRow(record);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            解除
          </a>
        ) : (
          <a
            onClick={() => {
              handleOffLine(record);
              setCurrentRow(record);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            封禁
          </a>
        ),
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
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalOpen(true);
            }}
          >
            <PlusOutlined /> 新建
          </Button>,
          <Button
            type="primary"
            danger
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            删除
          </Button>,
        ]}
        /*
                                                                                          分页查询后端数据，这个地方是一个组件的托管，不管是刷新还是点击查询都是调用这个
                                                                                          这儿我们直接调用的返回参数和他不一致，因此需要重写返回参数
                                                                                        */
        request={async (
          params,
          sort: Record<string, SortOrder>,
          filter: Record<string, React.ReactText[] | null>,
        ) => {
          const res: any = await listUserVoByPageUsingPost({
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

      <CreateModal
        columns={columns}
        onCancel={() => {
          handleModalOpen(false);
        }}
        onSubmit={(values) => {
          handleAdd(values);
        }}
        visible={createModalVisiable}
      />
      <UpdateModal
        /*  排除dataIndex为userAccount的元素*/
        columns={columns.filter((column) => column.dataIndex !== 'userAccount')}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalOpen(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalOpen(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalVisible}
        values={currentRow || {}}
      />
    </PageContainer>
  );
};
export default UserList;
