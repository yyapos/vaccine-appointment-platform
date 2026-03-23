import request from '@/utils/request'

export const getAppointmentPage = (params) => {
  return request.get('/appointment/page', { params })
}

export const approveAppointment = (id) => {
  return request.put(`/appointment/approve/${id}`)
}

export const rejectAppointment = (id, reason) => {
  return request.put(`/appointment/reject/${id}`, { reason })
}

export const completeAppointment = (id) => {
  return request.put(`/appointment/complete/${id}`)
}