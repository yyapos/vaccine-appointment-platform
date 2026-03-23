import request from '@/utils/request'

export const getPostPage = (params) => {
  return request.get('/forum/posts/page', { params })
}

export const createPost = (data) => {
  return request.post('/forum/posts', data)
}

export const updatePost = (data) => {
  return request.put(`/forum/posts/${data.id}`, data)
}

export const deletePost = (id) => {
  return request.delete(`/forum/posts/${id}`)
}

export const getAnnouncements = () => {
  return request.get('/forum/announcements')
}

export const createAnnouncement = (data) => {
  return request.post('/forum/announcements', data)
}

export const updateAnnouncement = (data) => {
  return request.put(`/forum/announcements/${data.id}`, data)
}

export const deleteAnnouncement = (id) => {
  return request.delete(`/forum/announcements/${id}`)
}