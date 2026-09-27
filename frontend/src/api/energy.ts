import request from '@/utils/request'

export function getEnergyHistory(params: {
  pointId?: number
  gatewayId?: number
  deviceId?: number
  energyTypeId?: number
  quality?: string
  startTime: string
  endTime: string
}) {
  return request.get('/energy/history', { params })
}

export function getDeviceData(params: {
  deviceId: number
  startTime: string
  endTime: string
}) {
  return request.get('/energy/device-data', { params })
}

export function getConsumption(params: {
  pointId?: number
  energyTypeId?: number
  period: string
  startDate: string
  endDate: string
}) {
  return request.get('/energy/consumption', { params })
}

export function getAnalysis(params: {
  objectType: string
  objectId?: number
  energyTypeId?: number
  energyClassification?: string
  period: string
  startDate: string
  endDate: string
}) {
  return request.get('/energy/analysis', { params })
}

export function getRatio(params: {
  objectType?: string
  energyTypeId?: number
  startDate: string
  endDate: string
}) {
  return request.get('/energy/ratio', { params })
}

export function getYoy(params: {
  energyTypeId?: number
  startDate: string
  endDate: string
}) {
  return request.get('/energy/yoy', { params })
}

export function exportDeviceData(params: {
  deviceId: number
  startTime: string
  endTime: string
}) {
  return request.get('/energy/export/device', { params, responseType: 'blob' })
}

export function exportHistoryData(params: {
  pointId: number
  startTime: string
  endTime: string
}) {
  return request.get('/energy/export/history', { params, responseType: 'blob' })
}