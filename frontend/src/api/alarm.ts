import request from '@/utils/request'

export function listAlarms(params?: { status?: string; level?: string; page?: number; pageSize?: number }) {
  return request.get('/alarms', { params })
}

export function listAlarmRules() {
  return request.get('/alarm-rules')
}