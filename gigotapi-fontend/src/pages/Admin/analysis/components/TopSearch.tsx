import { InfoCircleOutlined } from '@ant-design/icons';
import { Area } from '@ant-design/plots';
import { Card, Col, Row, Table, Tooltip } from 'antd';
import numeral from 'numeral';
import React from 'react';
import type { DataItem } from '../data.d';
import useStyles from '../style.style';
import NumberInfo from './NumberInfo';
import Trend from './Trend';

const TopSearch = ({
  loading,
  visitData2,
  mostPopular,
  searchData,
  dropdownGroup,
}: {
  loading: boolean;
  visitData2: DataItem[];
  mostPopular: string;
  dropdownGroup: React.ReactNode;
  searchData: DataItem[];
}) => {
  const { styles } = useStyles();
  const columns = [
    {
      title: '排名',
      dataIndex: 'index',
      render: (text, record, index) => index + 1,
    },
    {
      title: '接口名称',
      dataIndex: 'name',
      key: 'name',
      render: (text: React.ReactNode) => <a href="/">{text}</a>,
    },
    {
      title: '调用次数',
      dataIndex: 'totalInvokes',
      key: 'totalInvokes',
      sorter: (
        a: {
          count: number;
        },
        b: {
          count: number;
        },
      ) => a.count - b.count,
    },
  ];
  return (
    <Card
      loading={loading}
      bordered={false}
      title="热门接口调用"
      extra={dropdownGroup}
      style={{
        height: '100%',
      }}
    >
      <Row gutter={68}>
        <Col
          sm={12}
          xs={24}
          style={{
            marginBottom: 24,
          }}
        >
          <NumberInfo
            subTitle={
              <span>
                接口调用数
                <Tooltip title="近7天接口调用次数条形图">
                  <InfoCircleOutlined
                    style={{
                      marginLeft: 8,
                    }}
                  />
                </Tooltip>
              </span>
            }
            gap={8}
            total={numeral(12321).format('0,0')}
            status="up"
            subTotal={17.1}
          />
          <Area
            xField="day"
            yField="count"
            shapeField="smooth"
            height={45}
            axis={false}
            padding={-12}
            style={{ fill: 'linear-gradient(-90deg, white 0%, #6294FA 100%)', fillOpacity: 0.4 }}
            data={visitData2}
          />
        </Col>
        <Col
          sm={12}
          xs={24}
          style={{
            marginBottom: 24,
          }}
        >
          <NumberInfo
            subTitle={<span>最受欢迎</span>}
            total={2.7}
            status="down"
            subTotal={26.2}
            gap={8}
          />
          <h1>{mostPopular}</h1>
        </Col>
      </Row>
      <Table<any>
        rowKey={(record) => record.index}
        size="small"
        columns={columns}
        dataSource={searchData}
        pagination={{
          style: {
            marginBottom: 0,
          },
          pageSize: 5,
        }}
      />
    </Card>
  );
};
export default TopSearch;
