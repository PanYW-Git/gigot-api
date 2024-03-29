import { getIntroduceRowUsingGet } from '@/services/gigotapi-backend/analyseController';
import { Area, Column } from '@ant-design/plots';
import { Col, Progress, Row } from 'antd';
import numeral from 'numeral';
import { useEffect, useState } from 'react';
import type { DataItem } from '../data.d';
import useStyles from '../style.style';
import { ChartCard, Field } from './Charts';
import Trend from './Trend';

const topColResponsiveProps = {
  xs: 24,
  sm: 12,
  md: 12,
  lg: 12,
  xl: 6,
  style: {
    marginBottom: 24,
  },
};
const IntroduceRow = ({ loading, visitData }: { loading: boolean; visitData: DataItem[] }) => {
  const { styles } = useStyles();
  const [introduceRow, setIntroduceRow] = useState<API.IntroduceRowVO>();
  const [dayOverDayFlag, setDayOverDayFlag] = useState<string>('');
  const [weekOverWeekFlag, setWeekOverWeekFlag] = useState<string>('');

  const onload = async () => {
    // 发起请求
    const resIntroduceRow = await getIntroduceRowUsingGet();
    if (resIntroduceRow && resIntroduceRow.code === 0) {
      if(resIntroduceRow.data.dayOverDay === null){
        setDayOverDayFlag('');
        resIntroduceRow.data.dayOverDay = "无对比数据";
      }
      else {
        if (resIntroduceRow.data.dayOverDay[0] === "-") {
          resIntroduceRow.data.dayOverDay = resIntroduceRow.data.dayOverDay.substring(1);
          setDayOverDayFlag('down');
        } else {
          setDayOverDayFlag('up');
        }
      }
      if(resIntroduceRow.data.weekOverWeek === null){
        setWeekOverWeekFlag('');
        resIntroduceRow.data.weekOverWeek = "无对比数据";
      }else {
        if (resIntroduceRow.data.weekOverWeek[0] === '-') {
          resIntroduceRow.data.weekOverWeek = resIntroduceRow.data.weekOverWeek.substring(1);
          setWeekOverWeekFlag('down');
        } else {
          setWeekOverWeekFlag('up');
        }
      }
      setIntroduceRow(resIntroduceRow.data);
    }
  };
  useEffect(() => {
    onload();
  }, []);
  return (
    <Row gutter={24}>
      <Col {...topColResponsiveProps}>
        <ChartCard
          bordered={false}
          title="收入总额"
          loading={loading}
          total={() => <>{introduceRow?.sucessTotalAmount / 100}</>}
          footer={<Field label="日销售额" value={`￥${introduceRow?.dayTotal / 100}`} />}
          contentHeight={46}
        >
          <Trend
            flag={weekOverWeekFlag}
            style={{
              marginRight: 16,
            }}
          >
            周同比
            <span className={styles.trendText}>{introduceRow?.weekOverWeek}</span>
          </Trend>
          <Trend flag={dayOverDayFlag}>
            日同比
            <span className={styles.trendText}>{introduceRow?.dayOverDay}</span>
          </Trend>
        </ChartCard>
      </Col>

      <Col {...topColResponsiveProps}>
        <ChartCard
          bordered={false}
          loading={loading}
          title="支付笔数"
          total={numeral(introduceRow?.successPayCount).format('0,0')}
          footer={<Field label="待支付" value={introduceRow?.noPayCount} />}
          contentHeight={46}
        >
          <Column
            xField="x"
            yField="y"
            padding={-20}
            axis={false}
            height={46}
            data={visitData}
            scale={{ x: { paddingInner: 0.4 } }}
          />
        </ChartCard>
      </Col>

      <Col {...topColResponsiveProps}>
        <ChartCard
          bordered={false}
          loading={loading}
          title="近期接口平均耗时"
          total={introduceRow?.cost + 'ms'}
          footer={
            <Field
              label="调用次数"
              value={numeral(introduceRow?.interfaceInfoCount).format('0,0')}
            />          }
          contentHeight={46}
        >
          <Area
            xField="x"
            yField="y"
            shapeField="smooth"
            height={46}
            axis={false}
            style={{
              fill: 'linear-gradient(-90deg, white 0%, #975FE4 100%)',
              fillOpacity: 0.6,
              width: '100%',
            }}
            padding={-20}
            data={visitData}
          />
        </ChartCard>
      </Col>

      <Col {...topColResponsiveProps}>
        <ChartCard
          bordered={false}
          loading={loading}
          title="访问量"
          total={numeral(introduceRow?.pv).format('0,0')}
          footer={
            <Field label="在线用户" value={numeral(introduceRow?.onLineUserCount).format('0,0')} />
          }
          contentHeight={46}
        >
          <Area
            xField="x"
            yField="y"
            shapeField="smooth"
            height={46}
            axis={false}
            style={{
              fill: 'linear-gradient(-90deg, white 0%, #975FE4 100%)',
              fillOpacity: 0.6,
              width: '100%',
            }}
            padding={-20}
            data={visitData}
          />
        </ChartCard>
      </Col>
    </Row>
  );
};
export default IntroduceRow;
