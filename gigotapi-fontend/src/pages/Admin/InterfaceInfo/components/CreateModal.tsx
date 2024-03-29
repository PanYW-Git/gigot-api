import { ProColumns, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';

export type CreateFormProps = {
  columns: ProColumns<API.InterfaceInfoVO>[];
  onCancel: () => void;
  // 当用户提交表单，将用户输入的信息传递后台
  onSubmit: (values: API.InterfaceInfoAddRequest) => Promise<void>;
  // 是否可见
  visible: boolean;
  // 不用传递
  // values: Partial<API.RuleListItem>;
};
const CreateModal: React.FC<CreateFormProps> = (props) => {
    // 使用解构赋值获取props中的属性
    const {visible, columns, onCancel, onSubmit} = props;


    return (
    // 创建一个modal组件，通过visible控制是否隐藏，footer设置为null把表单的确认和取消按钮去掉
    <Modal
        visible={visible} footer={null} onCancel={() => onCancel?.()}
    >
        {/*   创建一个protable组件，设定它的类型为表单，通过columns属性设置表格的列，提交表单调用onSubmit方法 */}
        <ProTable
            headerTitle={'新建接口'}
            type="form"
            columns={columns}
            onSubmit={async (value) => {
                onSubmit?.(value);
            }}
        />

    </Modal>
);
};
export default CreateModal;
