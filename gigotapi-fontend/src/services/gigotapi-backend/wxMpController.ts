// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** check GET /api/wxMp/check */
export async function checkUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.checkUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<string>('/api/wxMp/check', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** receiveMessage POST /api/wxMp/receiveMessage */
export async function receiveMessageUsingPost(options?: { [key: string]: any }) {
  return request<any>('/api/wxMp/receiveMessage', {
    method: 'POST',
    ...(options || {}),
  });
}

/** setMenu GET /api/wxMp/setMenu */
export async function setMenuUsingGet(options?: { [key: string]: any }) {
  return request<string>('/api/wxMp/setMenu', {
    method: 'GET',
    ...(options || {}),
  });
}

/** wxLogin GET /api/wxMp/wxLogin */
export async function wxLoginUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/wxMp/wxLogin', {
    method: 'GET',
    ...(options || {}),
  });
}
