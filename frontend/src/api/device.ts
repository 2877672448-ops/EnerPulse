import request from '@/utils/request'

export function listGateways() {
  return request.get('/gateways')
}

export function createGateway(data: any) {
  return request.post('/gateways', data)
}

export function updateGateway(id: number, data: any) {
  return request.put(`/gateways/${id}`, data)
}

export function deleteGateway(id: number) {
  return request.delete(`/gateways/${id}`)
}

export function listDevices() {
  return request.get('/devices')
}

export function createDevice(data: any) {
  return request.post('/devices', data)
}

export function updateDevice(id: number, data: any) {
  return request.put(`/devices/${id}`, data)
}

export function deleteDevice(id: number) {
  return request.delete(`/devices/${id}`)
}

export function listPoints(params?: { deviceId?: number; gatewayId?: number }) {
  return request.get('/points', { params })
}

export function createPoint(data: any) {
  return request.post('/points', data)
}

export function updatePoint(id: number, data: any) {
  return request.put(`/points/${id}`, data)
}

export function deletePoint(id: number) {
  return request.delete(`/points/${id}`)
}
