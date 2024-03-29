import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import {Card, Col, Row, theme} from 'antd';
import React from 'react';
import CodeHighlighting from '@/components/CodeHighlighting';
import { quickStartJava, sdkMaven } from '@/pages/ListOpenApi/info/components/defaultCode';
import useStyles from "@/pages/ListOpenApi/style.style";


const Info: React.FC<{
  title: React.ReactNode;
  value: React.ReactNode;
  bordered?: boolean;
}> = ({ title, value, bordered }) => {
  const { styles } = useStyles();
  return (
    <div className={styles.headerInfo}>
      <span>{title}</span>
      <p>{value}</p>
      {bordered && <em />}
    </div>
  );
};

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({ title, href, index, desc }) => {
  const { useToken } = theme;

  const { token } = useToken();

  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <a href={href} target="_blank" rel="noreferrer">
        了解更多 {'>'}
      </a>
    </div>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  return (
    <>
      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage:
            initialState?.settings?.navTheme === 'realDark'
              ? 'background-image: linear-gradient(75deg, #1A1B1F 0%, #191C1F 100%)'
              : 'background-image: linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
        }}
      >
        <div
          style={{
            margin: '0 auto', /* 居中 */
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: token.colorTextHeading,
            }}
          >
            {/*  加粗*/}
            <h1 style={{ fontWeight: 700 }}>欢迎使用羊腿Api开放平台🎉</h1>
          </div>

          <p
            style={{
              fontSize: '14px',
              color: token.colorTextSecondary,
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '65%',
            }}
          >
            <b style={{fontSize: '18px'}}>🚀 简单便捷，助力您的开发之旅</b>
            <br/>
            <br/>

            欢迎来到我们的羊腿API开放平台，这是一个专为开发者设计的平台，我们提供一系列强大的API接口，让您能够轻松地访问和使用我们的数据。

            我们的API接口涵盖了各种类型的数据，无论您是想开发一个新的应用，还是想进行数据快速获取，我们的API都能为您提供强大的支持。

            为了让您能够快速上手，我们还提供了一套完整的SDK，让您能够轻松地将我们的API接入到您的应用中。

            我们的开放平台致力于提供高质量、实时的数据，以满足您的各种需求。我们的API接口设计简洁易用，文档详尽，让您能够快速上手。

            加入我们的羊腿API开放平台，让我们一起创造更多的可能性！
          </p>

          <div
            style={{
              marginTop: 20,
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >

            <InfoCard
              index={1}
              href="http://doc.panyuwen.top/"
              title="文档支持"
              desc="我们的官方文档支持，为您提供全面的技术指导和帮助，确保您轻松解决问题并顺利使用我们的产品和服务。"
            />
            <InfoCard
              index={2}
              title="SDK快速开始"
              href="https://github.com/PanYW-Git/gigotapi-client-sdk"
              desc="提供客户端SDK，方便您快速集成到你的项目中。"
            />
            <InfoCard
              index={3}
              title="可视化在线调试"
              href="/ListOpenApi"
              desc="通过我们的可视化在线调试工具快速访问接口，提高开发效率。帮助您轻松解决问题，确保您的应用程序顺利运行。"
            />
          </div>

        </div>


      </Card>
    </>
  );
};

export default Welcome;
