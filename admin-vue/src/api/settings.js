import request from '@/utils/request'

// 获取基本设置
export const getBasicSettings = () => {
  return request.get('/settings/basic')
}

// 更新基本设置
export const updateBasicSettings = (data) => {
  return request.put('/settings/basic', data)
}

// 获取预约设置
export const getAppointmentSettings = () => {
  return request.get('/settings/appointment')
}

// 更新预约设置
export const updateAppointmentSettings = (data) => {
  return request.put('/settings/appointment', data)
}

// 获取提醒设置
export const getReminderSettings = () => {
  return request.get('/settings/reminder')
}

// 更新提醒设置
export const updateReminderSettings = (data) => {
  return request.put('/settings/reminder', data)
}