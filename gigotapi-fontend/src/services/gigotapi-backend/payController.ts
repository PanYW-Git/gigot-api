// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** parseOrderNotifyResult POST /api/pay/order/notify */
export async function parseOrderNotifyResultUsingPost(
  body: string,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString_>('/api/pay/order/notify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
