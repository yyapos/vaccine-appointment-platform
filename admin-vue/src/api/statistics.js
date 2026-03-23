import request from '@/utils/request'


// 获取儿童总数
export const getChildCount = () => {
  return request.get('/child/count')
}

// 获取疫苗总数
export const getVaccineCount = () => {
  return request.get('/vaccine/count')
}

// 获取预约总数
export const getAppointmentCount = () => {
  return request.get('/appointment/count')
}
//获取待审核预约数
export const getreviewCount=()=>{
  return request.get('/appointment/count/pending-review-count')
}
//获取已通过数
export const getApprovedCount = ()=>{
  return request.get('/appointment/count/approved-count')
}
//获取已拒绝数
export const getRejectedCount = ()=>{
  return request.get('/appointment/count/rejected-count')
}
//获取已完成数
export const getCompletedCount = ()=>{
  return request.get('/appointment/count/completed-count')
}
//获取已取消数
export const getCancelledCount = ()=>{
  return request.get('/appointment/count/cancelled-count')
}
//疫苗预警
export const getStockWarning = (lowStockThreshold,outOfStockThreshold)=>{
  return request.get('/vaccine/stock-warning',{
    params:{
      lowStockThreshold:lowStockThreshold,
      outOfStockThreshold:outOfStockThreshold
    }
  })
}
// 获取家长总数
export const getParentCount = () => {
  return request.get('/parent/count')
}



// 获取疫苗库存统计
export const getVaccineStockStats = () => {
  return request.get('/vaccine/stock-stats')
}