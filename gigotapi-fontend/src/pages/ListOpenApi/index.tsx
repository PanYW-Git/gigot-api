import { interfaceMethodList, interfaceStatusList } from '@/enum/interfaceInfoEnum';
import {getInterfaceIntroduceRowUsingGet, getIntroduceRowUsingGet} from '@/services/gigotapi-backend/analyseController';
import { listInterfaceInfoVoByPageUsingPost } from '@/services/gigotapi-backend/interfaceInfoController';
import { PageContainer, ProList, ProListMeta } from '@ant-design/pro-components';
import { Badge, Card, Col, Row, Space, Tag } from 'antd';
import { SortOrder } from 'antd/lib/table/interface';
import React, { Key, useEffect, useState } from 'react';
import useStyles from './style.style';

const Info: React.FC<{
  title: React.ReactNode;
  value: React.ReactNode;
  bordered?: boolean;
}> = ({ title, value, bordered }) => {
  const { styles } = useStyles();
  return (
    <div className={styles.headerInfo}>
      <span>{title}</span>
      <p>{value}</p>
      {bordered && <em />}
    </div>
  );
};

const ListOpenApi: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfoVO[]>([]);
  const [nowCurrent, setNowCurrent] = useState<'1'>();
  const [nowPageSize, setNowPageSize] = useState<'10'>();
  const [nowTotal, setNowTotal] = useState<string>();
  const [countInterface, setCountInterface] = useState<string>();
  const [introduceRow, setIntroduceRow] = useState<API.IntroduceRowVO>();

  // matas
  const matas: ProListMeta<API.InterfaceInfoVO> = {
    subTitle: {
      title: '请求方式',
      dataIndex: 'method',
      valueType: 'select',
      valueEnum: interfaceMethodList,
      render: (_, record) => {
        if (record.method === interfaceMethodList.GET.text) {
          return (
            <Space size={0}>
              <Tag color="blue">GET</Tag>
            </Space>
          );
        }
        if (record.method === interfaceMethodList.POST.text) {
          return (
            <Space size={0}>
              <Tag color="green">POST</Tag>

            </Space>
          );
        }
        if (record.method === interfaceMethodList.DELETE.text) {
          return (
            <Space size={0}>
              <Tag color="red">DELETE</Tag>
            </Space>
          );
        }
        if (record.method === interfaceMethodList.PUT.text) {
          return (
            <Space size={0}>
              <Tag color="orange">PUT</Tag>
            </Space>
          );
        }
      },
    },
    title: {
      title: '名称',
      valueType: 'text',
      dataIndex: 'name',
      render: (_, record) => {
        const apiLink = `/ListOpenApi/info/${record.id}`;

        return (
          <strong>
            <a key={record.id} href={apiLink}>
              {record.name}
            </a>
          </strong>
        );
      },
    },
    description: {
      title: '描述',
      valueType: 'text',
      dataIndex: 'description',
      ellipsis: true,
      render: (_, record) => {
        return record.description;
      },
    },
    content: {
      search: false,
      render: (_, record) => (
        <div
          style={{
            minWidth: 200,
            flex: 1,
            display: 'flex',
            justifyContent: 'flex-end',
          }}
        >
          <div style={{ marginRight: '30px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <div style={{ color: '#AAAAAA', marginBottom: '5px' }}>调用次数</div>
            <Badge color="#0075FF" count={record.totalInvokes} overflowCount={9999}></Badge>
          </div>

          <div
            style={{
              width: '100px',
            }}
          >
            {record && record.status === 0 ? (
              <Badge style={{color: '#AAAAAA'}}
                status={interfaceStatusList['0'].status}
                text={interfaceStatusList['0'].text}
              />
            ) : null}
            {record && record.status === 1 ? (
              <Badge style={{color: '#AAAAAA'}}
                status={interfaceStatusList['1'].status}
                text={interfaceStatusList['1'].text}
              />
            ) : null}
          </div>
        </div>
      ),
    },
    actions: {
      render: (_, record) => {
        const apiLink = `/ListOpenApi/info/${record.id}`;
        return (
          <a key={record.id} href={apiLink}>
            查看
          </a>
        );
      },
    },
  };

  /**
   * 加载数据
   * @param values
   */
  const loadData = async (values: API.InterfaceInfoQueryRequest) => {
    const res = await listInterfaceInfoVoByPageUsingPost(values);
    setNowTotal(res?.data?.total);
    setCountInterface(res?.data?.total);
    const records = res?.data?.records ?? [];
    setList(records);
    const resIntroduceRow = await getInterfaceIntroduceRowUsingGet();
    if (resIntroduceRow && resIntroduceRow.code === 0) {
      setIntroduceRow(resIntroduceRow.data);
    }
    return res;
  };

  const flushData = async (
    params,
    sort: Record<string, SortOrder>,
    filter: Record<string, React.ReactText[] | null>,
  ) => {
    const res = await listInterfaceInfoVoByPageUsingPost({ ...params });
    const records = res?.data?.records ?? [];
    if (res?.data) {
      setList(records);
      // @ts-ignore
      setNowTotal(res?.data?.total || 0);
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
  };

  useEffect(() => {
    // 页面加载完成后调用加载数据的函数
    loadData({ current: nowCurrent, pageSize: nowPageSize });
  }, []);

  const [expandedRowKeys, setExpandedRowKeys] = useState<readonly Key[]>([]);

  const getInterfaceCount = () => {
    let count = 0;
    for (let i = 0; i < list.length; i++) {
      count = count + parseInt(list[i].totalInvokes);
    }
    return count;
  };

  return (
    <PageContainer>
      <Card bordered={false}>
        <Row>
          <Col sm={8} xs={24}>
            <Info title="接口数量" value={countInterface} bordered />
          </Col>
          <Col sm={8} xs={24}>
            <Info title="本周接口平均处理时长" value={introduceRow?.cost + 'ms'} bordered />
          </Col>
          <Col sm={8} xs={24}>
            <Info title="接口调用次数" value={getInterfaceCount()} />
          </Col>
        </Row>
      </Card>
      <ProList<API.InterfaceInfoVO>
        loading={loading}
        pagination={{
          showSizeChanger: true,
          defaultCurrent: 1,
          defaultPageSize: 10,
          total: Number(nowTotal),
          pageSizeOptions: ['5', '10', '15', '20'],
          onChange(current, pageSize) {
            setNowCurrent(current + 1);
            setNowPageSize(pageSize);
          },
        }}
        request={flushData}
        rowKey="title"
        headerTitle=""
        split={true}
        // 控制支持展开
        // expandable={{ expandedRowKeys, onExpandedRowsChange: setExpandedRowKeys }}
        dataSource={list}
        // 设置matas 类似coloum的设置
        metas={matas}
      />
    </PageContainer>
  );
};

export default ListOpenApi;
