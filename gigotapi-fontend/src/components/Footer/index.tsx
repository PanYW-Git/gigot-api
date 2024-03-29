import { GithubOutlined, HomeFilled, WechatFilled, WechatOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import { Tooltip } from 'antd';
import React from 'react';
const Footer: React.FC = () => {
  const defaultMessage = 'PanYW研发出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={<>
        {`${currentYear} ${defaultMessage}`} |{' '}
        <img src='https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/beian.png' alt="beian"/><a target={'_blank'} href={"https://beian.miit.gov.cn/"} rel="noreferrer"> 渝ICP备2023001069号-1</a>
      </>}
      links={[
        {
          key: 'blog',
          title: <><HomeFilled /> PanYW Blog </>,
          href: 'https://panyw-git.gitee.io/',
          blankTarget: true,
        },
        {
          key: 'weChat',
          title: (
            <Tooltip title={<img src="https://gigot-1315824716.cos.ap-chongqing.myqcloud.com/pictrue/wechatQRCode.jpg" alt="微信 code_nav" width="120"/>}>
              <WechatOutlined/> 联系作者
            </Tooltip>
          ),
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined /> PanYW GitHub </>,
          href: 'https://github.com/PanYW-Git',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
