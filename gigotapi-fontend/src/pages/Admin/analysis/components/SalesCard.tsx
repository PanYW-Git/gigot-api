import { Column } from '@ant-design/plots';
import { Card, Col, DatePicker, Row, Tabs } from 'antd';
import type { RangePickerProps } from 'antd/es/date-picker/generatePicker';
import type dayjs from 'dayjs';
import numeral from 'numeral';
import type { DataItem } from '../data.d';
import useStyles from '../style.style';

export type TimeType = 'today' | 'week' | 'month' | 'year';
const { RangePicker } = DatePicker;

const SalesCard = ({
  rangePickerValue,
  salesData,
  salesCardPayoutRank,
  isActive,
  handleRangePickerChange,
  loading,
  selectDate,
}: {
  rangePickerValue: RangePickerProps<dayjs.Dayjs>['value'];
  isActive: (key: TimeType) => string;
  salesData: DataItem[];
  salesCardPayoutRank: API.ProductOrderPayoutRank[];
  loading: boolean;
  handleRangePickerChange: RangePickerProps<dayjs.Dayjs>['onChange'];
  selectDate: (key: TimeType) => void;
}) => {
  const { styles } = useStyles();
  return (
    <Card
      loading={loading}
      title={"收入统计"}
      bordered={false}
      bodyStyle={{
        padding: 0,
      }}
    >
      <div className={styles.salesCard}>
        <Row>
          <Col xl={16} lg={12} md={12} sm={24} xs={24}>
            <div className={styles.salesBar}>
              <Column
                  height={300}
                  data={salesData}
                  xField="day"
                  yField="total"
                  paddingBottom={12}
                  axis={{
                    day: {
                      title: false,
                    },
                    total: {
                      title: false,
                      gridLineDash: null,
                      gridStroke: '#ccc',
                    },
                  }}
                  scale={{
                    x: { paddingInner: 0.4 },
                  }}
                  tooltip={{
                    name: '收入金额',
                    channel: 'y',
                  }}
              />
            </div>
          </Col>
          <Col xl={8} lg={12} md={12} sm={24} xs={24}>
            <div className={styles.salesRank}>
              <h3 className={styles.rankingTitle} style={{fontWeight: 'bold'}}>用户充值排名</h3>
              <ul className={styles.rankingList}>
                {
                    salesCardPayoutRank && salesCardPayoutRank.map((item, i) => (
                        <li key={item.payOutRank}>
                            <span
                                className={`${styles.rankingItemNumber} ${
                                    i < 3 ? styles.rankingItemNumberActive : ''
                                }`}
                            >
                              {i + 1}
                            </span>
                          <span className={styles.rankingItemTitle} title={item.userName}>
                              {item.userName}
                            </span>
                          <span>{numeral(item.total).format('0,0')}</span>
                        </li>
                    ))}
              </ul>
            </div>
          </Col>
        </Row>
      </div>
    </Card>
  );
};
export default SalesCard;
