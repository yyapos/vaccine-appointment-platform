import request from '@/utils/request'

// 疫苗管理
export const getVaccinePage = (params) => {
  return request.get('/vaccine/page', { params })
}

export const createVaccine = (data) => {
  return request.post('/vaccine/add', data)
}

export const updateVaccine = (data) => {
  return request.put('/vaccine/update', data)
}

export const deleteVaccine = (id) => {
  return request.delete(`/vaccine/${id}`)
}

export const getVaccineById = (id) => {
  return request.get(`/vaccine/${id}`)
}

export const searchVaccine = (name) => {
  return request.get('/vaccine/search', { params: { name } })
}

export const updateVaccineStock = (data) => {
  return request.put('/vaccine/stock', data)
}

// 疫苗批次管理
export const getBatchPage = (params) => {
  return request.get('/vaccine-batch/page', { params })
}

export const createBatch = (data) => {
  return request.post('/vaccine-batch/create', data)
}

export const updateBatchQuantity = (data) => {
  return request.put('/vaccine-batch/update-quantity', data)
}

export const getExpiringBatches = (params) => {
  return request.get('/vaccine-batch/expiring', { params })
}

export const getExpiredBatches = () => {
  return request.get('/vaccine-batch/expired')
}

export const getLowStockBatches = (params) => {
  return request.get('/vaccine-batch/low-stock', { params })
}

export const traceBatch = (traceCode) => {
  return request.get(`/vaccine-batch/trace/${traceCode}`)
}

export const allocateBatch = (data) => {
  return request.post('/vaccine-batch/allocate', data)
}

export const markBatchExpired = (data) => {
  return request.post('/vaccine-batch/mark-expired', data)
}

export const getBatchStatistics = () => {
  return request.get('/vaccine-batch/statistics')
}

export const getBatchById = (id) => {
  return request.get(`/vaccine-batch/${id}`)
}

export const updateBatch = (data) => {
  return request.put('/vaccine-batch/update', data)
}

export const deleteBatch = (id) => {
  return request.delete(`/vaccine-batch/${id}`)
}