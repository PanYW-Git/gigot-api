import Footer from '@/components/Footer';
import {
  getLoginUserXcxCheckUsingGet, getLoginUserXcxCheckUsingPost,
  loginSendVerificationCodeUsingPost,
  userLoginByEmailUsingPost,
  userLoginByXcxCreateQrUsingPost,
  userLoginUsingPost,
} from '@/services/gigotapi-backend/userController';
import { LockOutlined, MobileOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCaptcha, ProFormText } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import {
  Helmet,
  history,
  Link,
  RequestConfig,
  useModel,
} from '@umijs/max';
import { Alert, Image, message, Modal, QRCode, Tabs, theme, Tooltip } from 'antd';
import React, { useEffect, useState } from 'react';
import Settings from '../../../../config/defaultSettings';
import WxXcxApp from './CustomIcon';
import {requestConfig} from "@/requestConfig";

const ActionIcons = () => {
  const langClassName = useEmotionCss(({ token }) => {
    return {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.2)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    };
  });
  return (
    <>
      <Tooltip title="微信小程序登录">
        <WxXcxApp></WxXcxApp>
      </Tooltip>
    </>
  );
};
const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};
const Login: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [loginXcxQr, setLoginXcxQr] = useState<API.LoginXcxQrVO>();
  const [type, setType] = useState<string>('email');
  const [loading, setLoading] = useState<boolean>(false);
  const [xcxQRVisible, setxcxQRVisible] = useState<boolean>(false);
  const { token } = theme.useToken();
  const [userAgreementVisible, setuserAgreementVisible] = useState<boolean>(false);
  const [tooltipVisible, setTooltipVisible] = useState<boolean>(true);
  const { setInitialState } = useModel('@@initialState');
  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage: 'url(/assets/tencentCloudBackGround.jpg)',
      backgroundSize: '100% 100%',
    };
  });
  const getXcxQR = async () =>{
    if (xcxQRVisible) {
      const intervalId = setInterval(async () => {
        // 定时任务逻辑
        const res = await getLoginUserXcxCheckUsingPost({scene: loginXcxQr?.scene});
        if (res.data && res.code === 0) {
          localStorage.setItem('token', res.data?.id);
          setLoading(true);
          message.success('登录成功');
          clearInterval(intervalId);
          // 解决需要点两次登录的问题
          // 这个问题是由于 React 组件更新的异步性质引起的。
          // 在调用 setInitialState 后，状态可能并没有立即更新，而你又立即执行了 history.push
          // 可以等待后增加一个定时器解决这个问题
          await setInitialState({
            loginUser: res.data,
          });
          setTimeout(function () {
            setLoading(false);
            const urlParams = new URL(window.location.href).searchParams;
            history.push(urlParams.get('redirect') || '/welcome');
          }, 2000);

          return;
        }
      }, 3000);
      return () => {
        clearInterval(intervalId);
      };
    }
  }
  useEffect(() => {
    getXcxQR();
  }, [xcxQRVisible]);

  setTimeout(() => {
    setTooltipVisible(false);
  }, 4000);


  const handleSubmit = async (values: any) => {
    try {
      // 登录
      let res = undefined;
      if (type === 'account') {
        res = await userLoginUsingPost({
          ...values,
        });
      }
      if (type === 'email') {
        res = await userLoginByEmailUsingPost({
          ...values,
        });
      }

      // setInitialState({loginUser: res.data, settings: Settings});
      if (res.data) {
        const urlParams = new URL(window.location.href).searchParams;
        // 解决需要点两次登录的问题
        // 这个问题是由于 React 组件更新的异步性质引起的。
        // 在调用 setInitialState 后，状态可能并没有立即更新，而你又立即执行了 history.push
        // 可以等待后增加一个定时器解决这个问题
        sessionStorage.setItem('token', res.data?.id);
        await setInitialState({
          loginUser: res.data,
        });
        setTimeout(() => {
          history.push(urlParams.get('redirect') || '/');
        }, 100);

        return;
      }
    } catch (error) {
      console.log(error);
    }
  };
  const { status, type: loginType } = userLoginState;
  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {'登录'}- {Settings.title}
        </title>
      </Helmet>
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          containerStyle={{}}
          logo={<img alt="logo" src="/icons/icon-128x128.png" />}
          title="羊腿Api开放平台"
          subTitle={'简单便捷，助力您的开发之旅'}
          initialValues={{
            autoLogin: false,
          }}
          actions={[
            <>
              <span style={{ color: '#9B9EA0' }}>快速登录: </span>

              <Tooltip title="点我，微信扫一扫免注册登录" open={tooltipVisible} placement="bottom" color={'#87d068'} defaultOpen={true}>
              <a
                href="#"
                onClick={async () => {
                  if (loginXcxQr === undefined) {
                    const res = await userLoginByXcxCreateQrUsingPost();
                    if (res && res.code === 0) {
                      setLoginXcxQr({ scene: res.data?.scene, qrName: res.data?.qrName });
                    }
                  }
                  setxcxQRVisible(true);
                }}
              >
                <span>
                  <ActionIcons></ActionIcons>
                </span>
              </a>
              </Tooltip>
              <br />
              <br />
              <h5 style={{ color: '#9B9EA0' }}>
                在使用该网站或应用程序前,请用户仔细阅读本
                <a
                  href={'#'}
                  onClick={() => {
                    window.location.href = '/UserAgreement.html';
                  }}
                >
                  用户协议
                </a>
                ,并确保同意所有条款和条件。用户登录即表明已经充分理解并同意本协议的所有内容
              </h5>
            </>,
          ]}
          onFinish={async (values) => {
            await handleSubmit(values);
          }}
        >
          <Modal
            title="微信小程序登录"
            visible={xcxQRVisible}
            footer={null}
            onCancel={() => setxcxQRVisible(false)}
          >
            <div style={{ textAlign: 'center' }}>
              <h2>请使用微信扫一扫二维码</h2>
              <h5>安全登录</h5>
              <Image
                style={{
                  width: '200px',
                  height: '200px',
                  display: 'block',
                  margin: '0 auto', // 水平居中
                }}
                src={requestConfig.baseURL + 'api/file/downloadQRCode?name=' + loginXcxQr?.qrName}
                alt="小程序二维码"
              />
              <h2>进入小程序授权</h2>
              <h5 style={{ color: '#9B9EA0' }}>
                在使用该网站或应用程序前,请用户仔细阅读本
                <a
                  onClick={() => {
                    window.location.href = '/UserAgreement.html';
                  }}
                >
                  用户协议
                </a>
                ,并确保同意所有条款和条件。用户登录即表明已经充分理解并同意本协议的所有内容
              </h5>
            </div>
          </Modal>
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'email',
                label: '邮箱免注册登录',
              },
              {
                key: 'account',
                label: '账户密码登录',
              },
            ]}
          />

          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的用户名和密码'} />
          )}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={'请输入用户名'}
                rules={[
                  {
                    required: true,
                    message: '用户名是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={'请输入密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                ]}
              />
            </>
          )}
          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的用户名和密码'} />
          )}
          {/*邮箱验证码登录*/}
          {status === 'error' && loginType === 'email' && <LoginMessage content="验证码错误" />}
          {type === 'email' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined />,
                }}
                name="email"
                placeholder={'请输入邮箱号！'}
                rules={[
                  {
                    required: true,
                    message: '邮箱号是必填项！',
                  },
                  {
                    pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                    message: '不合法的邮箱号！',
                  },
                ]}
              />
              <ProFormCaptcha
                phoneName={'email'}
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码！'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                name="verificationCode"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={async (email) => {
                  // todo 获取验证码
                  const res = await loginSendVerificationCodeUsingPost({
                    email: email,
                    verificationCode: null,
                  });
                  if (res && res.code === 0) {
                    await setInitialState({
                      loginUser: res.data,
                    });
                  }
                  return;
                }}
              />
            </>
          )}
          <div
            style={{
              marginBottom: 24,
            }}
          >
            {/*<ProFormCheckbox noStyle name="autoLogin">*/}
            {/*  自动登录*/}
            {/*</ProFormCheckbox>*/}
            {type === 'account' && (
              <Link to="/user/register" style={{ float: 'right' }}>
                <Tooltip title="来注册一个用户吧~">新用户注册</Tooltip>
              </Link>
            )}
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
