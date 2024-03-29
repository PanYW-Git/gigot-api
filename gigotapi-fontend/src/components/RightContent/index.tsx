import {BookFilled, QuestionCircleOutlined} from '@ant-design/icons';
import '@umijs/max';
import React from "react";
export type SiderTheme = 'light' | 'dark';
export const SelectLang = () => {
  return (
    <UmiSelectLang
      style={{
        padding: 4,
      }}
    />
  );
};
export const Question = () => {
  return (
    <div
      style={{
        display: 'flex',
        height: 'auto',
        fontSize: 14,
        fontWeight: "bold"
      }}
      onClick={() => {
        window.open('http://doc.panyuwen.top/');
      }}
    >
      <div ><BookFilled /> 开发者文档</div>
    </div>
  );
};
