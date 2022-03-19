import request from '@/utils/request'

export function postfields(data) {
  return request({
    url: '/wfwactivity/apply',
    method: 'post',
    data
  })
}


/**
 * 更新
 * @param {} data
 */
 export function updateSalaryItem(data) {
  return request({
    url: '/salary/updateSalaryItem',
    method: 'post',
    data
  })
}

/**
 * 获取一个server
 * @param {} id
 */
export function getSalaryItem(id) {
  return request({
    url: `/salary/getSalaryItem/${id}`,
    method: 'get'
  })
}

/**
 * list
 * @param {int}} page
 * @param {int} pageSize
 */
export function startprocess(id) {
  return request({
    url: `/salary/startProcess/${id}`,
    method: 'get',
  })
}


/**
 * list
 * @param {} id
 */
 export function getSalaryItemList(id) {
  return request({
    url: `/salary/getSalaryItemList/${id}`,
    method: 'get',
   })
}


/**
 * 删除
 * @param {*} id
 */
export function deleteMessage(id) {
  return request({
    url: `message/${id}`,
    method: 'DELETE'
  })
}
