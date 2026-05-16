import request from './request'

export const getOutlines = (categoryId) => request.get('/outlines', { params: { categoryId } })

export const createOutline = (data) => request.post('/admin/outlines', data)

export const updateOutline = (id, data) => request.put(`/admin/outlines/${id}`, data)

export const deleteOutline = (id) => request.delete(`/admin/outlines/${id}`)
