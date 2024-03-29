import { uploadAvatarUsingPost } from '@/services/gigotapi-backend/fileController';
import {
  ProColumns,
  ProFormInstance,
  ProFormUploadButton,
  ProTable,
} from '@ant-design/pro-components';
import '@umijs/max';
import { message, Modal, Upload } from 'antd';
import { RcFile } from 'antd/lib/upload';
import React, { useEffect, useRef } from 'react';
import {requestConfig} from "@/requestConfig";

export type Props = {
  values: API.UserUpdateRequest;
  columns: ProColumns<API.UserVO>[];
  onCancel: () => void;
  onSubmit: (Values: API.UserUpdateRequest) => Promise<void>;
  visible: boolean;
};

const UpdateModal: React.FC<Props> = (props) => {
  const { values, visible, columns, onCancel, onSubmit } = props;

  const formRef = useRef<ProFormInstance>();

  /**
   * 上传文件前校验
   * @param file
   */
  const beforeUpload = (file: RcFile) => {
    // 检查文件类型是否为 JPEG 或 PNG
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';

    if (!isJpgOrPng) {
      // 如果不是 JPEG 或 PNG，显示错误消息并阻止上传
      message.error('只允许上传 JPG/PNG 格式的文件');
      return false;
    }

    // 检查文件大小是否小于 2MB
    const isLt2M = file.size / 1024 / 1024 < 2;

    if (!isLt2M) {
      // 如果文件大小超过 2MB，显示错误消息并阻止上传
      message.error('仅支持2M以下的文件');
      return false;
    }

    // 如果通过了上述条件，允许上传
    return true;
  };

  useEffect(() => {
    if (formRef) {
      formRef.current?.setFieldsValue(values);
    }
  }, [values]);

  return (
    <Modal
      title="编辑用户"
      visible={visible}
      footer={null}
      onCancel={() => onCancel?.()}
      // centered
      style={{ padding: 24 }}
    >
      <ProFormUploadButton
        name="avatarUrl"
        label="头像"
        listType="picture-card"
        max={1}
        fieldProps={{
          multiple: false,
          name: 'file',
          accept: 'image/png, image/jpeg',

          headers: { timestamp: new Date().valueOf() + '' },
          beforeUpload: (file) => {
            beforeUpload(file);
          },
        }}
        action={async (file: RcFile) => {
          const result = await uploadAvatarUsingPost({file});
          // result就是图片名称
          const apiUrl =
            requestConfig.baseURL;

          values.userAvatar = apiUrl + '/api/file/downloadAvatar?name=' + result.data;
        }}
      />
      <ProTable
        type="form"
        formRef={formRef}
        columns={columns}
        onSubmit={async (value: API.UserUpdateRequest) => {
          value.userAvatar = values.userAvatar;
          onSubmit?.(value);
        }}
      />
    </Modal>
  );
};
export default UpdateModal;
