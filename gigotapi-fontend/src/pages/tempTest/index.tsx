import {
  BugOutlined,
  CodeOutlined,
  FileExclamationOutlined,
  FileTextOutlined,
} from '@ant-design/icons';
import { PageContainer, ProColumns } from '@ant-design/pro-components';
import { Button } from 'antd';
import React, { useEffect, useState } from 'react';
import './index.less';

type toolsParams = {
  paramsName: string;
  params: string;
};

/**
 * 提交在线调试
 */
const submitTools = () => {
  return;
};
const responseExampleContentList: Record<string, React.ReactNode> = {
    tools: (
        <Button name={'send'} type="primary" size={'large'} onClick={submitTools}>
            发送
        </Button>
    ),
};
const tempTest: React.FC = () => {



  return (
      <></>
  );
};

export default tempTest;
