import request from './request'

export const initHome = () => request.get('/init')

export const getArticles = (outlineId) => request.get('/articles', { params: { outlineId } })

export const getArticle = (id) => request.get(`/articles/${id}`)

export const searchArticles = (keyword) => request.get('/search', { params: { keyword } })

export const createArticle = (data) => request.post('/admin/articles', data)

export const updateArticle = (id, data) => request.put(`/admin/articles/${id}`, data)

export const deleteArticle = (id) => request.delete(`/admin/articles/${id}`)
