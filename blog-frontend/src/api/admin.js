import request from './request'

export const login = (data) => request.post('/admin/login', data)

export const getStats = () => request.get('/admin/stats')

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const changePassword = (data) => request.post('/admin/change-password', data)
