// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** downloadAvatar GET /api/file/downloadAvatar */
export async function downloadAvatarUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadAvatarUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<any>('/api/file/downloadAvatar', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** downloadQRCode GET /api/file/downloadQRCode */
export async function downloadQrCodeUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadQRCodeUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<any>('/api/file/downloadQRCode', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** sdk GET /api/file/sdk */
export async function sdkUsingGet(options?: { [key: string]: any }) {
  return request<any>('/api/file/sdk', {
    method: 'GET',
    ...(options || {}),
  });
}

/** uploadAvatar POST /api/file/uploadAvatar */
export async function uploadAvatarUsingPost(body: string, options?: { [key: string]: any }) {
  const formData = new FormData();

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.BaseResponseString_>('/api/file/uploadAvatar', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** uploadFileForTencentCos POST /api/file/uploadForTencentCos */
export async function uploadFileForTencentCosUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadFileForTencentCosUsingPOSTParams,
  body: {},
  file?: File,
  options?: { [key: string]: any },
) {
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.BaseResponseString_>('/api/file/uploadForTencentCos', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}
