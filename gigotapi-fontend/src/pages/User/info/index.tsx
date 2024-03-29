import { userRoleList } from '@/enum/userEnum';
import EmailModal from '@/pages/User/info/components/EmailModal';
import { requestConfig } from '@/requestConfig';
import { uploadAvatarUsingPost } from '@/services/gigotapi-backend/fileController';
import {
  bindEmailUsingPost,
  getLoginUserUsingGet,
  getVoucherUsingGet,
  signUsingGet,
  unbindEmailUsingPost, updatePasswordUsingPost, updateUserNameUsingPost,
  updateUserUsingPost,
  updateVoucherUsingPost,
} from '@/services/gigotapi-backend/userController';
import { EditOutlined, PlusOutlined, VerticalAlignBottomOutlined } from '@ant-design/icons';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import {
  Button,
  Descriptions,
  Image,
  message,
  Modal,
  Tooltip,
  Upload,
  UploadFile,
  UploadProps,
} from 'antd';
import ImgCrop from 'antd-img-crop';
import Paragraph from 'antd/lib/typography/Paragraph';
import { RcFile } from 'antd/lib/upload';
import React, { useEffect, useState } from 'react';
import PasswordModal from "@/pages/User/info/components/PasswordModal";

/**
 * 校验值是否为空
 * @param val
 */
export const valueLength = (val: any) => {
  return val && val.trim().length > 0;
};
/**
 * 上传前校验
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

const UserInfo: React.FC = () => {
  // 定义状态和钩子函数
  const [loading, setLoading] = useState(false);
  const [loginUser, setLoginUser] = useState<API.UserVO>();
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewTitle, setPreviewTitle] = useState('');
  const [previewImage, setPreviewImage] = useState('');
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [voucherLoading, setVoucherLoading] = useState<boolean>(false);
  const [userVoucher, setUserVoucher] = useState<API.UserVoucherVO>();
  const [openEmailModal, setOpenEmailModal] = useState<false>();
  const [openPasswordModal, setOpenPasswordModal] = useState<false>();
  const handleCancel = () => setPreviewOpen(false);

  const updateuserName = async (value: string) => {
    const res = await updateUserNameUsingPost({ id: loginUser?.id, userName: value });
    if (res.code !== 0) {
      return;
    }
    location.reload();
    message.success('昵称更新成功');
    return;
  };

  const handlePasswordSubmit = async (values: API.UserUpdatePasswordRequest) => {
    try {
      // 绑定邮箱
      const res = await updatePasswordUsingPost(values);
      if (res.data && res.code === 0) {
        setOpenPasswordModal(false);
        message.success('修改密码成功');
      }
    } catch (error) {
      const defaultLoginFailureMessage = '操作失败！';
      message.error(defaultLoginFailureMessage);
    }
  };

  const handleBindEmailSubmit = async (values: API.UserBindEmailRequest) => {
    try {
      // 绑定邮箱
      const res = await bindEmailUsingPost({
        ...values,
      });
      if (res.data && res.code === 0) {
        setOpenEmailModal(false);
        message.success('绑定成功');
        location.reload();
      }
    } catch (error) {
      const defaultLoginFailureMessage = '操作失败！';
      message.error(defaultLoginFailureMessage);
    }
  };

  const handleUnBindEmailSubmit = async () => {
    try {
      // 绑定邮箱
      const res = await unbindEmailUsingPost();
      if (res.data && res.code === 0) {
        setOpenEmailModal(false);
        message.success('解绑成功');
      }
    } catch (error) {
      const defaultLoginFailureMessage = '操作失败！';
      message.error(defaultLoginFailureMessage);
    }
  };

  /**
   * 更新凭证
   */
  const updateVoucher = async () => {
    setVoucherLoading(true);
    const res = await updateVoucherUsingPost();
    if (res.data && res.code === 0) {
      setUserVoucher(res.data);
      setTimeout(() => {
        message.success(`凭证更新成功`);
        setVoucherLoading(false);
      }, 800);
    }
  };

  /**
   * 获取文件base64
   * @param file
   */
  const getBase64 = (file: RcFile): Promise<string> =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    });

  /**
   * 预览文件
   */
  const handlePreview = async (file: UploadFile) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj as RcFile);
    }
    setPreviewImage(file.url || (file.preview as string));
    setPreviewOpen(true);
    setPreviewTitle(file.name || file.url!.substring(file.url!.lastIndexOf('-') + 1));
  };

  /**
   * 上传文件参数列表
   */
  const uploadProps: UploadProps = {
    name: 'file',
    withCredentials: true,
    action: async (file: RcFile) => {
      setLoading(true);
      // @ts-ignore
      const res = await uploadAvatarUsingPost({ file });
      if (res.code !== 0) {
        message.error('上传失败');
        return '';
      }

      const url = requestConfig.baseURL + 'api/file/downloadAvatar?name=' + res.data;
      if (loginUser) {
        loginUser.userAvatar = url;
        setLoginUser(loginUser);
      }
      const updateRes = await updateUserUsingPost({ id: loginUser?.id, userAvatar: url });
      if (updateRes.code !== 0) {
        message.error('更新失败');
        return '';
      }
      message.success('头像更新成功');
      location.reload();
      setLoading(false);
      return url;
    },
    onChange: async function ({ file, fileList: newFileList }) {
      const { response } = file;
      if (file.response && response.data) {
        const {
          data: { status, url },
        } = response;
        const updatedFileList = [...fileList];
        if (response.code !== 0 || status === 'error') {
          message.error(response.message);
          file.status = 'error';
          updatedFileList[0] = {
            // @ts-ignore
            uid: loginUser?.userAccount,
            // @ts-ignore
            name: loginUser?.userAvatar
              ? loginUser?.userAvatar?.substring(loginUser?.userAvatar!.lastIndexOf('-') + 1)
              : 'error',
            status: 'error',
            percent: 100,
          };
          setFileList(updatedFileList);
          return;
        }
        file.status = status;
        updatedFileList[0] = {
          // @ts-ignore
          uid: loginUser?.userAccount,
          // @ts-ignore
          name: loginUser?.userAvatar?.substring(loginUser?.userAvatar!.lastIndexOf('-') + 1),
          status: status,
          url: url,
          percent: 100,
        };
        setFileList(updatedFileList);
      } else {
        setFileList(newFileList);
      }
    },
    listType: 'picture-circle',
    onPreview: handlePreview,
    fileList: fileList,
    beforeUpload: beforeUpload,
    maxCount: 1,
    progress: {
      strokeColor: {
        '0%': '#108ee9',
        '100%': '#87d068',
      },
      strokeWidth: 3,
      format: (percent) => percent && `${parseFloat(percent.toFixed(2))}%`,
    },
  };

  /**
   * 加载数据
   */
  const loadData = async () => {
    setLoading(true);
    setVoucherLoading(true);
    try {
      const resLoginUser = await getLoginUserUsingGet({});
      const resVoucher = await getVoucherUsingGet({});
      if (resLoginUser.code !== 0) {
        message.error('加载用户信息失败');
        return;
      }
      if (resVoucher.code !== 0) {
        message.error('加载用户信息失败');
        return;
      }
      setLoginUser(resLoginUser.data);
      setUserVoucher(resVoucher.data);
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
    setVoucherLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageContainer
      header={{
        breadcrumb: {},
      }}
    >
      <ProCard bordered direction="column">
        <ProCard
          loading={loading}
          extra={
            <>
                <Button
                  onClick={() => {
                    setOpenPasswordModal(true);
                  }}
                >
                  修改密码
                </Button>
              <Tooltip title={'用于接收订单信息'}>
                <Button
                  style={{ marginLeft: 10 }}
                  onClick={() => {
                    setOpenEmailModal(true);
                  }}
                >
                  {loginUser?.email ? '更新邮箱' : '绑定邮箱'}
                </Button>
              </Tooltip>
              <Tooltip>
                {loginUser?.email ? (
                  <Button
                    style={{ marginLeft: 10 }}
                    onClick={async () => {
                      const res = await unbindEmailUsingPost();
                      if (res.data && res.code === 0) {
                        // 刷新页面
                        location.reload();
                        message.success('解绑成功');
                      }
                    }}
                  >
                    解绑邮箱
                  </Button>
                ) : (
                  <></>
                )}
              </Tooltip>
            </>
          }
          bordered
          type="inner"
          title={<strong>基本信息</strong>}
        >
          <Descriptions column={1}>
            <Descriptions.Item>
              <ImgCrop
                rotationSlider
                quality={1}
                aspectSlider
                maxZoom={4}
                cropShape={'round'}
                zoomSlider
                showReset
              >
                <Upload {...uploadProps}>
                  {loginUser?.userAvatar ? (
                      <Image
                        src={loginUser?.userAvatar}
                        style={{ width: '100px', height: '100px', borderRadius: '50%', objectFit: 'cover' }}
                        alt="example"
                      />
                  ) : (
                    <div>
                      <PlusOutlined />
                      <div style={{ marginTop: 8 }}>Upload</div>
                    </div>
                  )}
                </Upload>
              </ImgCrop>
              <Modal open={previewOpen} title={previewTitle} footer={null} onCancel={handleCancel}>
                <img alt="example" style={{ width: '100%' }} src={previewImage} />
              </Modal>
            </Descriptions.Item>
            <Descriptions.Item label="昵称">
              <Paragraph
                editable={{
                  icon: <EditOutlined />,
                  tooltip: '编辑',
                  onChange: (value) => {
                    updateuserName(value);
                  },
                }}
              >
                {loginUser?.userName ? loginUser?.userName : '无名氏'}
              </Paragraph>
            </Descriptions.Item>
            <Descriptions.Item label="身份标识">
              <Paragraph copyable={true}>{loginUser?.id}</Paragraph>
            </Descriptions.Item>
            <Descriptions.Item label="角色">
              {loginUser && loginUser.userRole === 'user' ? (
                <Paragraph copyable={true}>{userRoleList.user.text}</Paragraph>
              ) : null}
              {loginUser && loginUser.userRole === 'admin' ? (
                <Paragraph copyable={true}>{userRoleList.admin.text}</Paragraph>
              ) : null}
            </Descriptions.Item>
            <Descriptions.Item label="邮箱">
              <Paragraph copyable={true}>
                {loginUser?.email ? loginUser?.email : '未绑定'}
              </Paragraph>
            </Descriptions.Item>
          </Descriptions>
        </ProCard>
        <br />
        <br />
        <ProCard
          type={'inner'}
          bordered
          tooltip={'用于平台接口调用'}
          title={<strong>我的钱包</strong>}
          extra={
            <>
              <Button
                onClick={() => {
                  window.location.href = '/ProductInfo';
                }}
              >
                充值余额
              </Button>
            </>
          }
        >
          <strong>金币 : </strong>{' '}
          <span style={{ color: 'red', fontSize: 18 }}>{loginUser?.balanceGoldCoin}🪙</span>
          <br />
          <strong>获取更多：</strong>
          <Button
            loading={loading}
            style={{ marginRight: 10 }}
            type={'default'}
            onClick={async () => {
              // 签到
              try {
                const res = await signUsingGet();
                if (res.data && res.code === 0) {
                  // 刷新页面
                  location.reload();
                  message.success('签到成功');
                }
              } catch (error: any) {}
            }}
          >
            <Tooltip
              title={
                <>
                  <p>每日签到可获取10积分</p>
                  {/*<p>普通用户上限100</p>*/}
                  {/*<p>VPI会员上限1000</p>*/}
                </>
              }
            >
              每日签到
            </Tooltip>
          </Button>
        </ProCard>
        <br />
        <ProCard
          loading={voucherLoading}
          bordered
          type="inner"
          title={<strong>Api密钥</strong>}
          extra={
            <Button loading={voucherLoading} onClick={updateVoucher}>
              {userVoucher?.secretId && userVoucher?.secretKey ? '更新' : '生成'}密钥
            </Button>
          }
        >
          {userVoucher?.secretId && userVoucher?.secretKey ? (
            <Descriptions column={1}>
              <Descriptions.Item label="AccessKey">
                <Paragraph copyable={valueLength(userVoucher?.secretId)}>
                  {userVoucher?.secretId}
                </Paragraph>
              </Descriptions.Item>
              <Descriptions.Item label="SecretKey">
                <Paragraph copyable={valueLength(userVoucher?.secretKey)}>
                  {userVoucher?.secretKey}
                </Paragraph>
              </Descriptions.Item>
            </Descriptions>
          ) : (
            '暂无凭证,请先生成凭证'
          )}
        </ProCard>
        <br />
        <ProCard type="inner" title={<strong>开发者 SDK（快速接入API接口）</strong>} bordered>
          <Button size={'large'}>
            <a target={'_blank'} href={requestConfig.baseURL + 'api/file/sdk'} rel="noreferrer">
              <VerticalAlignBottomOutlined />
              Java SDK
            </a>
          </Button>
        </ProCard>
        <EmailModal
          unbindSubmit={handleUnBindEmailSubmit}
          bindSubmit={handleBindEmailSubmit}
          data={loginUser}
          onCancel={() => setOpenEmailModal(false)}
          open={openEmailModal}
        />
        <PasswordModal
            bindSubmit={handlePasswordSubmit}
            data={loginUser}
            onCancel={() => setOpenPasswordModal(false)}
            open={openPasswordModal}
        />
      </ProCard>
    </PageContainer>
  );
};

export default UserInfo;
