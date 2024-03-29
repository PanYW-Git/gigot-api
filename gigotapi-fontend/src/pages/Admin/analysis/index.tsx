import {
  getInterfaceinfoProportionUsingGet,
  getSalesCardUsingGet,
  getTopInterfaceInfoUsingGet
} from '@/services/gigotapi-backend/analyseController';
import { EllipsisOutlined } from '@ant-design/icons';
import { GridContent } from '@ant-design/pro-components';
import { useRequest } from '@umijs/max';
import { Col, Dropdown, Row } from 'antd';
import type { RangePickerProps } from 'antd/es/date-picker/generatePicker';
import type { RadioChangeEvent } from 'antd/es/radio';
import type dayjs from 'dayjs';
import type { FC } from 'react';
import { Suspense, useEffect, useState } from 'react';
import IntroduceRow from './components/IntroduceRow';
import OfflineData from './components/OfflineData';
import PageLoading from './components/PageLoading';
import ProportionSales from './components/ProportionSales';
import type { TimeType } from './components/SalesCard';
import SalesCard from './components/SalesCard';
import TopSearch from './components/TopSearch';
import type { AnalysisData } from './data.d';
import { fakeChartData } from './service';
import useStyles from './style.style';
import { getTimeDistance } from './utils/utils';
import {sleep} from "@antfu/utils";

type RangePickerValue = RangePickerProps<dayjs.Dayjs>['value'];
type AnalysisProps = {
  dashboardAndanalysis: AnalysisData;
  loading: boolean;
};
type SalesType = 'all' | 'online' | 'stores';
const Analysis: FC<AnalysisProps> = () => {
  const { styles } = useStyles();
  const [salesType, setSalesType] = useState<SalesType>('all');
  const [salesCardTotalDay, setSaleCardTotalDay] = useState<API.ProductOrderTotalDay[]>();
  const [salesCardPayoutRank, setSaleCardPayoutRank] = useState<API.ProductOrderPayoutRank[]>();
  const [interfaceLogWeekCount, setInterfaceLogWeekCount] = useState<API.InterfaceLogWeekCount[]>();
  const [interfaceInfoTotalCount, setInterfaceInfoTotalCount] = useState<API.InterfaceInfoTotalCountVO[]>();
  const [interfaceinfoProport, setInterfaceinfoProport] = useState<API.ProportionSalesVO>();
  const [mostPopular, setMostPopular] = useState<string>();
  const [currentTabKey, setCurrentTabKey] = useState<string>('');
  const [rangePickerValue, setRangePickerValue] = useState<RangePickerValue>(
    getTimeDistance('year'),
  );
  const { loading, data } = useRequest(fakeChartData);
  const selectDate = (type: TimeType) => {
    setRangePickerValue(getTimeDistance(type));
  };
  const handleRangePickerChange = (value: RangePickerValue) => {
    setRangePickerValue(value);
  };
  const isActive = (type: TimeType) => {
    if (!rangePickerValue) {
      return '';
    }
    const value = getTimeDistance(type);
    if (!value) {
      return '';
    }
    if (!rangePickerValue[0] || !rangePickerValue[1]) {
      return '';
    }
    if (
      rangePickerValue[0].isSame(value[0] as dayjs.Dayjs, 'day') &&
      rangePickerValue[1].isSame(value[1] as dayjs.Dayjs, 'day')
    ) {
      return styles.currentDate;
    }
    return '';
  };

  let salesPieData;

  if (salesType === 'all') {
    salesPieData = data?.salesTypeData;
  } else {
    salesPieData = salesType === 'online' ? data?.salesTypeDataOnline : data?.salesTypeDataOffline;
  }

  const onload = async () => {
    const salesCardRes = await getSalesCardUsingGet();
    const topInterfaceInfoRes = await getTopInterfaceInfoUsingGet();
    const interfaceinfoProportRes = await getInterfaceinfoProportionUsingGet();
    if(salesCardRes && salesCardRes.code ===0 ) {
      salesCardRes.data.totalDay.map((item) => {
        item.total = item.total / 100;
      });
      setSaleCardPayoutRank(salesCardRes.data.payoutRank);
      setSaleCardTotalDay(salesCardRes.data.totalDay);
    }

    if(topInterfaceInfoRes && topInterfaceInfoRes.code ===0){
      setInterfaceLogWeekCount(topInterfaceInfoRes.data.interfaceLogWeekCounts);
      setInterfaceInfoTotalCount(topInterfaceInfoRes.data.interfaceInfoTotalCount);
      setMostPopular(topInterfaceInfoRes.data.mostPopular);
    }

    if(interfaceinfoProportRes && interfaceinfoProportRes.code === 0){
      interfaceinfoProportRes.data.interfaceInfoProportionList.map((item) => {
        item.ratio = item.ratio * 100;
      });
      setInterfaceinfoProport(interfaceinfoProportRes.data.interfaceInfoProportionList);
    }

  }

  useEffect(() => {
    onload();
  }, []);

  const dropdownGroup = (
    <span className={styles.iconGroup}>
      <Dropdown
        menu={{
          items: [
            {
              key: '1',
              label: '操作一',
            },
            {
              key: '2',
              label: '操作二',
            },
          ],
        }}
        placement="bottomRight"
      >
        <EllipsisOutlined />
      </Dropdown>
    </span>
  );
  const handleChangeSalesType = (e: RadioChangeEvent) => {
    setSalesType(e.target.value);
  };
  const handleTabChange = (key: string) => {
    setCurrentTabKey(key);
  };
  const activeKey = currentTabKey || (data?.offlineData[0] && data?.offlineData[0].name) || '';
  return (
    <GridContent>
      <>
        {/*指标块*/}
        <Suspense fallback={<PageLoading />}>
          <IntroduceRow loading={loading} visitData={data?.visitData || []} />
        </Suspense>

        <Suspense fallback={null}>
          <SalesCard
            rangePickerValue={rangePickerValue}
            salesData={salesCardTotalDay}
            salesCardPayoutRank={salesCardPayoutRank}
            isActive={isActive}
            handleRangePickerChange={handleRangePickerChange}
            loading={loading}
            selectDate={selectDate}
          />
        </Suspense>

        <Row
          gutter={24}
          style={{
            marginTop: 24,
          }}
        >
          <Col xl={12} lg={24} md={24} sm={24} xs={24}>
            <Suspense fallback={null}>
              <TopSearch
                loading={loading}
                mostPopular={mostPopular}
                visitData2={interfaceLogWeekCount || []}
                searchData={interfaceInfoTotalCount || []}
                dropdownGroup={dropdownGroup}
              />
            </Suspense>
          </Col>
          <Col xl={12} lg={24} md={24} sm={24} xs={24}>
            <Suspense fallback={null}>
              <ProportionSales
                dropdownGroup={dropdownGroup}
                salesType={salesType}
                loading={loading}
                salesPieData={interfaceinfoProport || []}
                handleChangeSalesType={handleChangeSalesType}
              />
            </Suspense>
          </Col>
        </Row>
      </>
    </GridContent>
  );
};
export default Analysis;
