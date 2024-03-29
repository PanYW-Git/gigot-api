import React, { useEffect, useRef, useState } from 'react';
import { Button, Modal } from 'antd';
import { ProFormText } from '@ant-design/pro-form';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import { LoginForm } from '@ant-design/pro-components';
import { ProFormInstance } from '@ant-design/pro-form/lib';
import OldPassword from "@/components/Icon/oldPassword";
import NewPassword from "@/components/Icon/newPassword";
import CheckPassword from "@/components/Icon/CheckPassword";

export type Props = {
  open: boolean;
  onCancel: () => void;
  data?: API.UserVO;
  bindSubmit: (values: API.UserUpdatePasswordRequest) => Promise<void>;
};

const PasswordModal: React.FC<Props> = (props) => {
  const formRef = useRef<ProFormInstance>();
  const [key, setKey] = useState<"bind" | "unbind">()
  const {open, data, onCancel, bindSubmit} = props;
  useEffect(() => {
    // 关闭表单时刷新form
    if (!open) {
      formRef.current?.resetFields()
    }
  }, [open]);
  return (
    <Modal
        title={"修改密码"}
      footer={null}
      centered
      open={open}
      width={500}
      onCancel={onCancel}
    >
      <LoginForm
        formRef={formRef}
        contentStyle={{
          minWidth: 280,
          maxWidth: '75vw',
        }}
        submitter={
          {
            render: () => {
              return [
                <>
                  <Button
                    type={"primary"}
                    key="submit"
                    block
                    onClick={() => {
                      setKey("bind")
                      formRef.current?.submit()
                    }}
                  >
                    修改密码
                  </Button>
                </>
              ];
            },
          }
        }
        onFinish={async (values) => {
          if (key === "bind") {
            bindSubmit?.(values)
          }
        }}
      >
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <OldPassword />,
          }}
          name="oldPassword"
          placeholder={'请输入旧密码！'}
          rules={[
            {
              required: true,
              message: '旧密码必填！',
            },
          ]}
        />
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <NewPassword/>,
          }}
          name="newPassword"
          placeholder={'请输入新的密码！'}
          rules={[
            {
              required: true,
              message: '新密码必填！',
            },
          ]}
        />
        <ProFormText
          fieldProps={{
            size: 'large',
            prefix: <CheckPassword/>,
          }}
          name="checkNewPassword"
          placeholder={'请再输入一次新密码！'}
          rules={[
            {
              required: true,
              message: '重复输入密码必填',
            },
              {
                validator: (_, value) => {
                  if (!value || value !== formRef.current?.getFieldValue('newPassword')) {
                    return Promise.reject(new Error('两次输入的密码不一致'));
                  }
                  return Promise.resolve();
                }
              }
          ]}
        />
      </LoginForm>
    </Modal>
  );
};

export default PasswordModal;
