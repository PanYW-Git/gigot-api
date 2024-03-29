// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** deleteByIdProductOrder POST /api/productOrder/deleteById */
export async function deleteByIdProductOrderUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productOrder/deleteById', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteByIdsProductOrder POST /api/productOrder/deleteByIds */
export async function deleteByIdsProductOrderUsingPost(
  body: API.DeleteListRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productOrder/deleteByIds', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getOrderStatus POST /api/productOrder/get/status */
export async function getOrderStatusUsingPost(
  body: API.ProductOrderQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productOrder/get/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getProductOrderVOById GET /api/productOrder/get/vo */
export async function getProductOrderVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProductOrderVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductOrderVO_>('/api/productOrder/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductOrderVOByPage POST /api/productOrder/list/page/vo */
export async function listProductOrderVoByPageUsingPost(
  body: API.ProductOrderQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductOrderVO_>('/api/productOrder/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyProductOrderVOByPage POST /api/productOrder/my/list/page/vo */
export async function listMyProductOrderVoByPageUsingPost(
  body: API.ProductOrderQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductOrderVO_>('/api/productOrder/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** payCreateProductOrder POST /api/productOrder/pay/create */
export async function payCreateProductOrderUsingPost(
  body: API.PayCreateOrderRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductOrderVO_>('/api/productOrder/pay/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateStatusById POST /api/productOrder/update/status */
export async function updateStatusByIdUsingPost(
  body: API.ProductOrderUpdateStatusRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productOrder/update/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
