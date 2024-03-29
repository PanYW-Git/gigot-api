import { Pie } from '@ant-design/plots';
import { Card, Radio, Typography } from 'antd';
import type { RadioChangeEvent } from 'antd/es/radio';
import numeral from 'numeral';
import React from 'react';
import type { DataItem } from '../data.d';
import useStyles from '../style.style';
const { Text } = Typography;
const ProportionSales = ({
  dropdownGroup,
  salesType,
  loading,
  salesPieData,
  handleChangeSalesType,
}: {
  loading: boolean;
  dropdownGroup: React.ReactNode;
  salesType: 'all' | 'online' | 'stores';
  salesPieData: DataItem[];
  handleChangeSalesType?: (e: RadioChangeEvent) => void;
}) => {
  const { styles } = useStyles();
  return (
    <Card
      loading={loading}
      className={styles.salesCard}
      bordered={false}
      title="调用次数占比"
      style={{
        height: '100%',
      }}
    >
      <div>
        <Text>接口调用</Text>
        <Pie
          height={340}
          radius={0.8}
          innerRadius={0.5}
          angleField="ratio"
          colorField="name"
          data={salesPieData as any}
          label={{
            position: 'spider',
            text: (item: { ratio: number; name: string }) => {
              return `${item.name}: ${(item.ratio).toFixed(2)}%`;
            },
          }}
        />
      </div>
    </Card>
  );
};
export default ProportionSales;
