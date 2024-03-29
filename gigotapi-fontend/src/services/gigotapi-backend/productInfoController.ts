// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addProductInfo POST /api/productInfo/add */
export async function addProductInfoUsingPost(
  body: API.ProductInfoAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/productInfo/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteByIdProductInfo POST /api/productInfo/deleteById */
export async function deleteByIdProductInfoUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/deleteById', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteByIdsProductInfo POST /api/productInfo/deleteByIds */
export async function deleteByIdsProductInfoUsingPost(
  body: API.DeleteListRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/deleteByIds', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** editProductInfo POST /api/productInfo/edit */
export async function editProductInfoUsingPost(
  body: API.ProductInfoEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getProductInfoVOById GET /api/productInfo/get/vo */
export async function getProductInfoVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProductInfoVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductInfoVO_>('/api/productInfo/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductInfoVOByPage POST /api/productInfo/list/page/vo */
export async function listProductInfoVoByPageUsingPost(
  body: API.ProductInfoQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductInfoVO_>('/api/productInfo/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyProductInfoVOByPage POST /api/productInfo/my/list/page/vo */
export async function listMyProductInfoVoByPageUsingPost(
  body: API.ProductInfoQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductInfoVO_>('/api/productInfo/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateProductInfo POST /api/productInfo/update */
export async function updateProductInfoUsingPost(
  body: API.ProductInfoUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
