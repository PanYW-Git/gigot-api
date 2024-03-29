// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** getInterfaceIntroduceRow GET /api/analysis/interface/introduceRow */
export async function getInterfaceIntroduceRowUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseIntroduceRowVO_>('/api/analysis/interface/introduceRow', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getInterfaceinfoProportion GET /api/analysis/interfaceinfoProportion */
export async function getInterfaceinfoProportionUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseProportionSalesVO_>('/api/analysis/interfaceinfoProportion', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getIntroduceRow GET /api/analysis/introduceRow */
export async function getIntroduceRowUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseIntroduceRowVO_>('/api/analysis/introduceRow', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getSalesCard GET /api/analysis/salesCard */
export async function getSalesCardUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseSalesCardVO_>('/api/analysis/salesCard', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getTopInterfaceInfo GET /api/analysis/topInterfaceInfo */
export async function getTopInterfaceInfoUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseTopInterfaceInfoVO_>('/api/analysis/topInterfaceInfo', {
    method: 'GET',
    ...(options || {}),
  });
}
