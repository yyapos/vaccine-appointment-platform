import request from '@/utils/request'

export const adminLogin = (username, password) => {
  return request.post('/admin/login', { username, password })
}

export const getUserPage = (params) => {
  return request.get('/user/page', { params })
}

export const createUser = (data) => {
  return request.post('/user/create', data)
}

export const updateUser = (data) => {
  return request.put('/user/admin/update', data)
}

export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}

export const enableUser = (id) => {
  return request.put(`/user/enable/${id}`)
}

export const disableUser = (id) => {
  return request.put(`/user/disable/${id}`)
}

// 家长管理API
export const getParentPage = (params) => {
  return request.get('/parent/page', { params })
}

export const updateParent = (data) => {
  return request.put('/parent/update', data)
}

export const enableParent = (id) => {
  return request.put(`/parent/enable/${id}`)
}

export const disableParent = (id) => {
  return request.put(`/parent/disable/${id}`)
}

// 儿童管理API
export const getChildPage = (params) => {
  return request.get('/child/page', { params })
}

export const getChildList = (parentId) => {
  return request.get('/child/list', { params: { parentId } })
}

export const createChild = (data) => {
  return request.post('/child/add', data)
}

export const updateChild = (data) => {
  return request.put('/child/update', data)
}

export const deleteChild = (id) => {
  return request.delete(`/child/${id}`)
}